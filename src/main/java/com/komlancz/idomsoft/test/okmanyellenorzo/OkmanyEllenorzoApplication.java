package com.komlancz.idomsoft.test.okmanyellenorzo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class OkmanyEllenorzoApplication extends SpringBootServletInitializer {

	@Override protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
		return application.sources(OkmanyEllenorzoApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(OkmanyEllenorzoApplication.class, args);
	}

}
