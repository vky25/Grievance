package org.upsmf.grievance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableScheduling
@EnableWebMvc
@EnableJpaRepositories(basePackages = "org.upsmf.grievance")
public class GrievanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrievanceServiceApplication.class, args);
	}

}
