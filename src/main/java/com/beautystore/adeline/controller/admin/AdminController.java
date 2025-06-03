package com.beautystore.adeline.controller.admin;

import com.beautystore.adeline.services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class AdminController {

    private final OrderService orderService;

    @Autowired
    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }


    
    @GetMapping
    String adminDashboard(){
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

    // @GetMapping
    // public String adminDashboard(
    //         @RequestParam(value = "startDate", required = false) LocalDateTime startDate,
    //         @RequestParam(value = "endDate", required = false) LocalDateTime endDate,
    //         Model model) {

    //     // Mặc định báo cáo là tháng này
    //     if (startDate == null) {
    //         startDate = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0)
    //                 .withSecond(0);
    //     }
    //     if (endDate == null) {
    //         endDate = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59)
    //                 .withSecond(59);
    //     }

    //     DashboardDataResponse dashboardData = orderService.getDashboardData(startDate, endDate);

    //     model.addAttribute("totalOrders", dashboardData.getTotalOrders());
    //     model.addAttribute("totalProductsSold", dashboardData.getTotalProductsSold());
    //     model.addAttribute("totalAmount", dashboardData.getTotalAmount());
    //     model.addAttribute("totalSubTotal", dashboardData.getTotalSubTotal());
    //     model.addAttribute("totalCost", dashboardData.getTotalCost());
    //     model.addAttribute("totalProfit", dashboardData.getTotalProfit());
    //     model.addAttribute("startDate", startDate);
    //     model.addAttribute("endDate", endDate);
    //     model.addAttribute("chartData", dashboardData.getChartData());

    //     return "admin/category/index";
    // }

    // @GetMapping("")
    // public String adminDashboard(HttpSession session) {
    // Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

    // // if (isAdmin == null || !isAdmin) {
    // // return "index"; // Nếu không phải admin, chuyển về trang chủ
    // // }
    // return "admin/category/index"; // Trả về trang admin
    // }
}
