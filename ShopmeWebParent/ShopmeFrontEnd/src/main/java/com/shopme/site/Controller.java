package com.shopme.site;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {
  @GetMapping("")
  public String homePage() {
	  return "index";
  }
}
