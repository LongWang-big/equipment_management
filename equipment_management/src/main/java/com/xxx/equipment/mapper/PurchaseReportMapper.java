package com.xxx.equipment.mapper;

import com.xxx.equipment.vo.PurchaseReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseReportMapper {

    /**
     * 按月统计采购金额
     */
    List<PurchaseReportVO> selectMonthlyReport(@Param("year") String year);

    /**
     * 按年统计采购金额
     */
    List<PurchaseReportVO> selectYearlyReport(@Param("year") String year);
}
