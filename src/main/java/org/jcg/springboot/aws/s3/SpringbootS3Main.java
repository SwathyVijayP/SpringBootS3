package org.jcg.springboot.aws.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class SpringbootS3Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringbootS3Main.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootS3Main.class, args);
		LOGGER.info("SpringbootS3 application started successfully.");
	}
}
