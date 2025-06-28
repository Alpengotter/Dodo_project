package com.alpengotter.dodo_project;

import org.springframework.boot.SpringApplication;

public class TestDodoProjectApplication {

	public static void main(String[] args) {
		SpringApplication.from(DodoProjectApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
