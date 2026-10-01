package com.uc.ms_security;

import com.uc.ms_security.dto.User.CreateUserDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSecurityApplication.class, args);
	}

	CreateUserDTO user = new CreateUserDTO();

}
