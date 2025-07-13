package com.bootcampbox.server;

import com.bootcampbox.server.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RookiePxServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RookiePxServerApplication.class, args);
	}

	@Bean
	public CommandLineRunner initializeData(CategoryService categoryService) {
		return args -> {
			// 애플리케이션 시작 시 기본 카테고리 초기화
			categoryService.initializeDefaultCategories();
		};
	}
}
