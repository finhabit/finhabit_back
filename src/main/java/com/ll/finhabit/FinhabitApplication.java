package com.ll.finhabit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FinhabitApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinhabitApplication.class, args);
    }
}
