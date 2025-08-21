package com.vitorbnr.evngest;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EvngestApplication {

	public static void main(String[] args) {
		loadDotenv();
		SpringApplication.run(EvngestApplication.class, args);
	}

	private static void loadDotenv() {
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
	}
}
