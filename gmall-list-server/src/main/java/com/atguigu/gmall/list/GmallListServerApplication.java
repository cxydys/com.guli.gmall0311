package com.atguigu.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu")
public class GmallListServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallListServerApplication.class, args);
	}

}
