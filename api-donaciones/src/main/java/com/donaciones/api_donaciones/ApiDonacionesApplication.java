package com.donaciones.api_donaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiDonacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDonacionesApplication.class, args);
	}

}
