package com.beautystore.adeline.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;


@RequestMapping("/admin")
@Controller
public class AdminController {

    @GetMapping("")
        public String adminDashboard(HttpSession session) {
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

            if (isAdmin == null || !isAdmin) {
                return "index"; // Nếu không phải admin, chuyển về trang chủ
            }
            return "admin/category/index"; // Trả về trang admin
        }
}
