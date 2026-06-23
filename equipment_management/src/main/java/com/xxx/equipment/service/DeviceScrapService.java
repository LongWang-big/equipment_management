package com.xxx.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.dto.ScrapDTO;
import com.xxx.equipment.vo.ScrapVO;

public interface DeviceScrapService {

    void addScrap(ScrapDTO dto);

    void approveScrap(ScrapDTO dto);

    void deleteScrap(Long id);

    IPage<ScrapVO> getScrapPage(int pageNum, int pageSize, String status);
}
