package com.xxx.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.vo.StockRecordVO;

public interface StockRecordService {

    IPage<StockRecordVO> getRecordPage(int pageNum, int pageSize, String operationType);
}
