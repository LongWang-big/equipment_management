package com.xxx.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.dto.PurchaseOrderDTO;
import com.xxx.equipment.vo.PurchaseOrderVO;

public interface PurchaseOrderService {

    void addOrder(PurchaseOrderDTO dto);

    void updateOrder(PurchaseOrderDTO dto);

    void deleteOrder(Long id);

    PurchaseOrderVO getOrderById(Long id);

    IPage<PurchaseOrderVO> getOrderPage(int pageNum, int pageSize, String startDate, String endDate);
}
