package com.shopme.admin.product;

import java.io.IOException;
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
	private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";
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
			@RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
			@RequestParam(name = "categoryId", defaultValue = "0") String categoryId,
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@PathVariable(name = "pageNum") int pageNum, Model model) {

		productService.listByPage(pageNum, sortField, sortDir, keyword, Integer.valueOf(categoryId), model);

		List<Category> listCategories = categoryService.listCategories();

		if (categoryId != null)
			model.addAttribute("categoryId", Integer.valueOf(categoryId));
		model.addAttribute("listCategories", listCategories);
		if (sortDir.equals("asc")) {
			model.addAttribute("reverseDir", "dis");
		} else {
			model.addAttribute("reverseDir", "asc");
		}
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

		// ProductSaveHelper.setMainImageName(mainImageMultipart, product);
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
		// ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
		if (imageIDs != null && imageIDs.length > 0) {

			Set<ProductImage> images = new HashSet<>();

			for (int count = 0; count < imageIDs.length; count++) {
				Integer id = Integer.parseInt(imageIDs[count]);
				String name = imageNames[count];

				images.add(new ProductImage(id, name, product));
			}

			product.setImages(images);
		}

		// ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
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
		// ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues,
		// product);

		if (detailNames != null && detailNames.length > 0) {

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

		Product savedProduct = productService.save(product);

		//ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
		if (!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "product-images/" + savedProduct.getId();
			
			//List<String> listObjectKeys = .listFolder(uploadDir + "/");
//			for (String objectKey : listObjectKeys) {
//				if (!objectKey.contains("/extras/")) {
//					FileUploadUtil.removeDir(objectKey);
//				}
//			}
			
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);					
		}
		
		if (extraImageMultiparts.length > 0) {
			String uploadDir = "product-images/" + savedProduct.getId() + "/extras";
			
			for (MultipartFile multipartFile : extraImageMultiparts) {
				if (multipartFile.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);	
			}
		}
		//ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);
		

		ra.addFlashAttribute("message", "The product has been saved successfully.");

		return defaultRedirectURL;
	}
}
