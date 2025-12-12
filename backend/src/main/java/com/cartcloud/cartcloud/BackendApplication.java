package com.cartcloud.cartcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    // CONTROLLER I THJESHTË PËR TEST
    @RestController
    public static class HelloController {

        @GetMapping("/hello")
        public String hello() {
            return "Hello from BackendApplication!";
        }
    }
}
