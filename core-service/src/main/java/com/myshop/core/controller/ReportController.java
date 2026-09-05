package com.myshop.core.controller;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.core.constant.ApiPath;
import com.myshop.core.dto.response.MonthlyRevenueRow;
import com.myshop.core.dto.response.ProductRevenueRow;
import com.myshop.core.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.REPORT)
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly-revenue")
    public ApiResponse<List<MonthlyRevenueRow>> monthlyRevenue(@RequestParam(required = false) Integer year) {
        return ApiResponse.ok(reportService.monthlyRevenue(year));
    }

    @GetMapping("/product-revenue")
    public ApiResponse<List<ProductRevenueRow>> productRevenue(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return ApiResponse.ok(reportService.productRevenue(year, month));
    }
}
