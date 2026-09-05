package com.myshop.core.service.report;

import com.myshop.core.dto.response.MonthlyRevenueRow;
import com.myshop.core.dto.response.ProductRevenueRow;
import com.myshop.core.mapper.ReportQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportQueryMapper reportQueryMapper;

    public List<MonthlyRevenueRow> monthlyRevenue(Integer year) {
        return reportQueryMapper.monthlyRevenue(year);
    }

    public List<ProductRevenueRow> productRevenue(Integer year, Integer month) {
        return reportQueryMapper.productRevenue(year, month);
    }
}
