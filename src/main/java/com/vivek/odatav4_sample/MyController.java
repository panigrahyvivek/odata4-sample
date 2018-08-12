package com.vivek.odatav4_sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController{
	
	@GetMapping("/")
	public String sayHello() {
		return "Hello World!!";
	}
}