package com.beautystore.adeline.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    @GetMapping("")
    public String home() {
        return "index";
    }

    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
    @GetMapping("/product")
    public String product() {
        return "product";
    }

    @GetMapping("/cart-view")
    public String cart() {
        return "cart";
    }

    @GetMapping("/order")
    public String order() {
        return "order";
    }
}
