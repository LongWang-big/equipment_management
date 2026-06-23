package com.xxx.equipment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.entity.Inventory;
import com.xxx.equipment.vo.InventoryVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    /**
     * 分页查询库存（关联设备名称）
     */
    IPage<InventoryVO> selectPageWithDeviceName(IPage<InventoryVO> page);

    /**
     * 查询库存预警列表（quantity < warning_value）
     */
    IPage<InventoryVO> selectWarningList(IPage<InventoryVO> page);
}
