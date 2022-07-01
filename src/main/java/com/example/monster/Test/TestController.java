package com.example.monster.Test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@RestController
public class TestController {

    @GetMapping(value = "/test")
    public String test(){
        return "build success!";
    }
}
