package com.myshop.core.mapper;

import com.myshop.core.dto.response.MonthlyRevenueRow;
import com.myshop.core.dto.response.ProductRevenueRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportQueryMapper {

    List<MonthlyRevenueRow> monthlyRevenue(@Param("year") Integer year);

    List<ProductRevenueRow> productRevenue(@Param("year") Integer year, @Param("month") Integer month);
}
