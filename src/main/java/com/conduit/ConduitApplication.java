package com.conduit;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ConduitApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConduitApplication.class, args);
    }
    
}
