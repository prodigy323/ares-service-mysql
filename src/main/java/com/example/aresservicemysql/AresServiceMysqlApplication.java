package com.example.aresservicemysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class AresServiceMysqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(AresServiceMysqlApplication.class, args);
	}
}
