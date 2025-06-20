package com.beautystore.adeline.controller.admin;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.CouponResponse;
import com.beautystore.adeline.dto.response.FinancialMetricsResponse;
import com.beautystore.adeline.dto.response.SupplierResponse;
import com.beautystore.adeline.entity.Coupon;
import com.beautystore.adeline.services.CouponService;
import com.beautystore.adeline.services.OrderService;
import com.beautystore.adeline.services.ProductService;
import com.beautystore.adeline.services.SupplierService;
import com.beautystore.adeline.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/admin")
@Controller
public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CouponService couponService;


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


        int totalUsers = userService.countUsers();
        int totalProducts = productService.countProducts();
        model.addAttribute("totalRevenue", dashboardData.getTotalRevenue());
        model.addAttribute("totalOrders", dashboardData.getTotalOrders());
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);


        model.addAttribute("chartData", orderService.getProfitChartData(startDate, endDate));


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
    String adminOrder(Model model){

        List<CouponResponse> coupons = couponService.getCoupons();
        model.addAttribute("coupons" ,coupons);
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
    String adminSupplier(Model model){
        List<SupplierResponse> suppliers = supplierService.getSuppliers();

        model.addAttribute("suppliers", suppliers);
        return "adminGUI/suppliers";
    }

    @GetMapping("/coupon") 
    String adminCoupon(){
        return "adminGUI/coupon";
    }

    @GetMapping("/report") 
    String adminReport(){
        return "adminGUI/report";
    }

    @GetMapping("/financial-metrics")
    @ResponseBody
    public ApiResponse<FinancialMetricsResponse> getFinancialMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        if (startDate == null) {
            startDate = LocalDateTime.now().minusMonths(1);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        ApiResponse<FinancialMetricsResponse> response = new ApiResponse<>();
        response.setResult(orderService.getFinancialMetrics(startDate, endDate));
        return response;
    }


}
