package com.xxx.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.dto.InventoryCheckDTO;
import com.xxx.equipment.dto.StockInDTO;
import com.xxx.equipment.dto.StockOutDTO;
import com.xxx.equipment.dto.StockReturnDTO;
import com.xxx.equipment.vo.InventoryCheckVO;
import com.xxx.equipment.vo.InventoryVO;

public interface InventoryService {

    IPage<InventoryVO> getInventoryPage(int pageNum, int pageSize);

    IPage<InventoryVO> getWarningList(int pageNum, int pageSize);

    void stockIn(StockInDTO stockInDTO);

    void stockOut(StockOutDTO stockOutDTO);

    void stockReturn(StockReturnDTO stockReturnDTO);

    InventoryCheckVO check(InventoryCheckDTO inventoryCheckDTO);
}
