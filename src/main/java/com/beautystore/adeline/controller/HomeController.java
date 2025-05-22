package com.beautystore.adeline.controller;

import com.beautystore.adeline.dto.response.CartResponse;
import com.beautystore.adeline.services.CartService;
import com.beautystore.adeline.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

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
    public String cart(HttpSession session, Model model) {
        Long userId = userService.getUserIdFromSession(session);
        CartResponse cart = cartService.getCart(userId);
        model.addAttribute("cart", cart); 
        return "cart";
    }

    @GetMapping("/order")
    public String order() {
        return "order";
    }
}
