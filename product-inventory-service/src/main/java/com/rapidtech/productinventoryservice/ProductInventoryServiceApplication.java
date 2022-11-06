package com.rapidtech.productinventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ProductInventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductInventoryServiceApplication.class, args);
	}

}
