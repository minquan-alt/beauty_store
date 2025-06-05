package com.beautystore.adeline.controller.admin;

import com.beautystore.adeline.services.OrderService;
import com.beautystore.adeline.services.ProductService;
import com.beautystore.adeline.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@RequestMapping("/admin")
@Controller
public class AdminController {

    private final OrderService orderService;
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public AdminController(OrderService orderService, UserService userService, ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping
    String adminDashboard(
            @RequestParam(value = "filter", required = false, defaultValue = "month") String filter,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            Model model) {

        LocalDateTime startDate;
        LocalDateTime endDate;
        LocalDateTime now = LocalDateTime.now();
        
        switch (filter) {
            case "week":
                startDate = now.with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
                endDate = now.with(java.time.DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59);
                break;
            case "date":
                startDate = startDateStr != null ? LocalDateTime.parse(startDateStr) : now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
                endDate = endDateStr != null ? LocalDateTime.parse(endDateStr) : now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
                break;
            case "month":
            default:
                startDate = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
                endDate = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
                break;
        }
        
        var dashboardData = orderService.getDashboardData(startDate, endDate);

        //Data cho bảng
        int totalUsers = userService.countUsers();
        int totalProducts = productService.countProducts();
        model.addAttribute("totalRevenue", dashboardData.getTotalRevenue());
        model.addAttribute("totalOrders", dashboardData.getTotalOrders());
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);

        //Data cho chart
        model.addAttribute("chartData", orderService.getProfitChartData(startDate, endDate));

        //Data cho filter
        model.addAttribute("filter", filter);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "adminGUI/dashboard";
    }

    @GetMapping("/product") 
    String adminProduct(){
        return "adminGUI/products";
    }

    @GetMapping("/order") 
    String adminOrder(){
        return "adminGUI/orders";
    }

    @GetMapping("/user") 
    String adminUser(){
        return "adminGUI/users";
    }

    @GetMapping("/inventory") 
    String adminInventory(){
        return "adminGUI/inventory";
    }

    @GetMapping("/supplier") 
    String adminSupplier(){
        return "adminGUI/suppliers";
    }

    
    @GetMapping("/settings")
    public String getSettings() {
        return "settings";
    }

}
