package org.upsmf.grievance;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "org.upsmf.grievance")
@Slf4j
public class GrievanceServiceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+5:30"));
		log.info("Application time zone time is IST: " + new Date().toString());

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
