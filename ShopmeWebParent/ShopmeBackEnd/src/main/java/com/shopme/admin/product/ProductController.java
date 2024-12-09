package com.shopme.admin.product;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.sym.Name;
import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductImage;

@Controller
public class ProductController {
	private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=default&categoryId=0";
	@Autowired
	private ProductService productService;
	@Autowired
	private BrandService brandService;
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}

	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@RequestParam(name = "sortField", defaultValue = "name") String sortField,
			@RequestParam(name = "sortDir", defaultValue = "default") String sortDir,
			@RequestParam(name = "categoryId", defaultValue = "0") String categoryId,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@PathVariable(name = "pageNum") Integer pageNum, Model model) {
		productService.listByPage( sortField, sortDir, keyword, pageNum ,Integer.valueOf(categoryId), model);

		List<Category> listCategories = categoryService.listCategories();

		if (categoryId != null)
			model.addAttribute("categoryId", Integer.valueOf(categoryId));
		model.addAttribute("listCategories", listCategories);
		return "products/products";
	}

	@GetMapping("/products/new")
	public String createProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
        
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);

		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingExtraImages", 0);

		return "products/product_form";
	}

	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {

		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if (loggedUser.hasRole("Salesperson")) {
				productService.saveProductPrice(product);
				ra.addFlashAttribute("message", "The product has been saved successfully.");
				return defaultRedirectURL;
			}
		}
		
		setMainImage(mainImageMultipart, product);
		setExistingExtraImageNames(imageIDs, imageNames, product);
		setNewExtraImageNames(extraImageMultiparts, product);
		setProductDetails(detailIDs, detailNames, detailValues, product);

		

		Product savedProduct = productService.save(product);
        deleteMainImageIfMainImageWereUploaded(savedProduct);
		ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "product-images/" + savedProduct.getId();
//			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);	
			AmazonS3Util.uploadFile(uploadDir, fileName, mainImageMultipart.getInputStream());
		}
		
		if (extraImageMultiparts.length > 0) {
			String uploadDir = "product-images/" + savedProduct.getId() + "/extras";
			
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (multipartFile.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
//				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);	
				AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
			}
		}
		deleteExtrasImagesWereDeletedInForm(product);
		

		ra.addFlashAttribute("message", "The product has been saved successfully.");

		return defaultRedirectURL;
	}
	
	public void setMainImage(MultipartFile multipartFile, Product product) {
		if(!multipartFile.isEmpty()) {
			String fileName=StringUtils.cleanPath(multipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
	
	static void deleteMainImageIfMainImageWereUploaded(Product product) throws IOException {
		 String dir="product-images/"+product.getId();
		 String newMainImageName=product.getMainImagePath();
		   List<String> listFolder=new ArrayList<>();
		   Files.list(Paths.get(dir)).forEach(file->{
			   String fileName=file.toFile().getName();
			   if(newMainImageName.equals(fileName)) {
//				  try {
//					Files.delete(file);
					AmazonS3Util.deleteFile(fileName);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			   }
		   });
	}
	
	
	
	public void deleteExtrasImagesWereDeletedInForm(Product product) {
		 String extrasFileDir="product-images/"+product.getId()+"/extras";
		 Path dirPath= Paths.get(extrasFileDir);
		 try {
			Files.list(dirPath).forEach(file ->{
				String fileName=file.toFile().getName();
				if(!product.containsImageName(fileName)) {
//					try {
//						Files.delete(file);
						AmazonS3Util.deleteFile(fileName);
//					} catch (IOException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			});
			
			
		} catch (Exception ex) {
			// TODO: handle exception
		}
	}
	
	static void setProductDetails(String[] detailIDs, String[] detailNames, 
			String[] detailValues, Product product) {
		if (detailNames == null || detailNames.length == 0) return;
		
		for (int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailIDs[count]);
			
			if (id != 0) {
				product.addDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
		}
	}

	
	static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, 
			Product product) {
		if (imageIDs == null || imageIDs.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		
		for (int count = 0; count < imageIDs.length; count++) {
			Integer id = Integer.parseInt(imageIDs[count]);
			String name = imageNames[count];
			
			images.add(new ProductImage(id, name, product));
		}
		
		product.setImages(images);
		
	}
	
	static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
		if (extraImageMultiparts.length > 0) {
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					
					if (!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}
				}
			}
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra) {
		try {
			Product product = productService.get(id);			
			model.addAttribute("product", product);		
			
			return "products/product_detail_modal";
			
		} catch (Exception e) {
			ra.addFlashAttribute("message", e.getMessage());
			
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Product ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra, @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();
			
			boolean isReadOnlyForSalesperson = false;
			
			if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
				if (loggedUser.hasRole("Salesperson")) {
					isReadOnlyForSalesperson = true;
				}
			}
			
			model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			
			return "products/product_form";
			
		} catch (Exception e) {
			ra.addFlashAttribute("message", e.getMessage());
			
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, 
			Model model, RedirectAttributes redirectAttributes) {
		try {
			productService.delete(id);
			String productExtraImagesDir = "product-images/" + id + "/extras";
			String productImagesDir = "product-images/" + id;
			
//			FileUploadUtil.removeDir(productExtraImagesDir);
//			FileUploadUtil.removeDir(productImagesDir);
			AmazonS3Util.removeFolder(productExtraImagesDir);
			AmazonS3Util.removeFolder(productImagesDir);
			redirectAttributes.addFlashAttribute("message", 
					"The product ID " + id + " has been deleted successfully");
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
}
