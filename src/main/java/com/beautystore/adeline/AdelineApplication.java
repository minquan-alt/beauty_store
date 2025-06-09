package com.beautystore.adeline;

import com.beautystore.adeline.config.MomoConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(MomoConfig.class)
public class AdelineApplication {
	public static void main(String[] args) {
		SpringApplication.run(AdelineApplication.class, args);
	}
}