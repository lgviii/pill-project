package edu.harvard.ext.dgmd_e14.fall_2022.pill_match;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:mysql-datasource.properties")
@SpringBootApplication
public class PillMatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(PillMatcherApplication.class, args);
	}

}
