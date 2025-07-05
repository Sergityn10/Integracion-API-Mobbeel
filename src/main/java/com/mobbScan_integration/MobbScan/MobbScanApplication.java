package com.mobbScan_integration.MobbScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EntityScan(basePackages = "com.mobbScan_integration.MobbScan.Models")
public class MobbScanApplication {

	public static void main(String[] args) {
		SpringApplication.run(MobbScanApplication.class, args);
	}

}
