package com.xxx.equipment.service;

import com.xxx.equipment.vo.PurchaseReportVO;

import java.util.List;

public interface PurchaseReportService {

    List<PurchaseReportVO> getMonthlyReport(String year);

    List<PurchaseReportVO> getYearlyReport(String year);
}
