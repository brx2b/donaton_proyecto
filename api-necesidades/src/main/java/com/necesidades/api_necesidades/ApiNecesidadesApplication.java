package com.necesidades.api_necesidades;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiNecesidadesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiNecesidadesApplication.class, args);
	}

}
