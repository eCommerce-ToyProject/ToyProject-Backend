package com.idrsys.toyprojectbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {

    @GetMapping("/api/loginPage")
    public String loginPage() {
        return "/login";
    }

    @GetMapping("/api/orderPage")
    public String hello() {
        return "/OrderPage";
    }
}
