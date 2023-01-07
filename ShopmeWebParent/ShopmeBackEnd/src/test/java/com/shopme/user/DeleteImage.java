package com.shopme.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration
public class DeleteImage {
   @Test
   public void deleteExtrasImage() throws IOException {
	   String dir="../product-images/1";
	   List<String> listFolder=new ArrayList<>();
	   Files.list(Paths.get(dir)).forEach(file->{
		   String fileName=file.toFile().getName();
		   System.out.println(fileName);
		   listFolder.add(fileName);
	   });
	   assertThat(listFolder).isNotEmpty();
   }
}
