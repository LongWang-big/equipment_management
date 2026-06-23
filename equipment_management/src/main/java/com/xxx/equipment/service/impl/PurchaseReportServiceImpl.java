package com.xxx.equipment.service.impl;

import com.xxx.equipment.mapper.PurchaseReportMapper;
import com.xxx.equipment.service.PurchaseReportService;
import com.xxx.equipment.vo.PurchaseReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseReportServiceImpl implements PurchaseReportService {

    private final PurchaseReportMapper purchaseReportMapper;

    @Override
    public List<PurchaseReportVO> getMonthlyReport(String year) {
        return purchaseReportMapper.selectMonthlyReport(year);
    }

    @Override
    public List<PurchaseReportVO> getYearlyReport(String year) {
        return purchaseReportMapper.selectYearlyReport(year);
    }
}
