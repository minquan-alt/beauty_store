package com.beautystore.adeline.controller;

import java.util.List;

import com.beautystore.adeline.dto.response.AddressResponse;
import com.beautystore.adeline.dto.response.CartResponse;
import com.beautystore.adeline.dto.response.OrderResponse;
import com.beautystore.adeline.dto.response.UserResponse;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.services.AddressService;
import com.beautystore.adeline.services.CartService;
import com.beautystore.adeline.services.OrderService;
import com.beautystore.adeline.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {
    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;


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
        UserResponse user = (UserResponse) session.getAttribute("user");
        if(user == null) {
            return "signin";
        }

        CartResponse cart = cartService.getCart(user.getId());
        List<AddressResponse> addresses = addressService.getAddresses(session);
        model.addAttribute("addresses", addresses);
        model.addAttribute("cart", cart); 
        return "cart";
    }

    @GetMapping("/order")
    public String order() {
        return "order";
    }

    @GetMapping("/payment")
    public String payment(Model model ,HttpSession session) {
        UserResponse user = (UserResponse) session.getAttribute("user");
        if(user == null) {
            return "signin";
        }
        OrderResponse order = orderService.getOrderFromSession(session);


        model.addAttribute("user", user);
        model.addAttribute("order" ,order);
        
        return "checkout";
    }

    @GetMapping("/settings")
    public String getSettings(Model model ,HttpSession session) {
        UserResponse user = (UserResponse) session.getAttribute("user");
        if(user == null) {
            return "signin";
        }
        List<AddressResponse> addresses = addressService.getAddresses(session);

        model.addAttribute("user", user);
        model.addAttribute("addresses", addresses);

        return "settings";
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "payment_successful";  // Trả về success.html
    }
}
