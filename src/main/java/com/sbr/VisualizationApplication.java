package com.sbr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


//@EnableDiscoveryClient
@SpringBootApplication
//@EnableFeignClients
@EnableCaching
public class VisualizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisualizationApplication.class, args);
	}

}
