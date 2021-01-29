package com.work.wcs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.work.wcs.mapper")
public class WcsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WcsApplication.class, args);
	}

}
