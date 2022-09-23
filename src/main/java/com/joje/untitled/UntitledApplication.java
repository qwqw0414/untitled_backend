package com.joje.untitled;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ServletComponentScan
@EnableAspectJAutoProxy
@SpringBootApplication
public class UntitledApplication {

	public static void main(String[] args) {
		SpringApplication.run(UntitledApplication.class, args);
	}

}
