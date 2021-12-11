package com.jameseng.dscatalog.tests;

import java.time.Instant;

import com.jameseng.dscatalog.dto.ProductDTO;
import com.jameseng.dscatalog.entities.Category;
import com.jameseng.dscatalog.entities.Product;

public class Factory {

	public static Product createProduct() {
		Product product=new Product(1L, "Phone", "Good Phone", 800.0, "http://img.com/img.png", Instant.parse("2020-02-20T03:00:00Z"));
		product.getCategories().add(createCategory());
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product=createProduct();
		return new ProductDTO(product, product.getCategories());	
	}
	
	public static Category createCategory() { //FEITO PELA AULA DE CORREÇÃO
		return new Category(2L, "Electronics");
	}
}
