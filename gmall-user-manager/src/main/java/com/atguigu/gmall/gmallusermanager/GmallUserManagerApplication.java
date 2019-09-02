package com.atguigu.gmall.gmallusermanager;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.atguigu.gmall.gmallusermanager.Mapper")
@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu")
public class GmallUserManagerApplication {

	public static void main(String[] args) {

		SpringApplication.run(GmallUserManagerApplication.class, args);
	}

}
