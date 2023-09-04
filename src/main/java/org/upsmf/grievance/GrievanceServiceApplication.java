package org.upsmf.grievance;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "org.upsmf.grievance")
public class GrievanceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrievanceServiceApplication.class, args);
	}

	@Bean
	public VelocityEngine velocityEngine() {
		VelocityEngine engine = new VelocityEngine();

		engine.setProperty("resource.loader", "class");
		engine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		return engine;
	}


}
