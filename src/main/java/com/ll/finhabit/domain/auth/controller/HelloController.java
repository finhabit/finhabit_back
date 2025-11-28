package com.ll.finhabit.domain.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "🚀 PR CICD Test -- Hello! Finhabit Back-end is running!";
    }
}
