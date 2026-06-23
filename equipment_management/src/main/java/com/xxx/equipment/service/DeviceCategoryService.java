package com.xxx.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xxx.equipment.dto.CategoryDTO;
import com.xxx.equipment.vo.CategoryVO;

public interface DeviceCategoryService {

    void addCategory(CategoryDTO dto);

    void updateCategory(CategoryDTO dto);

    void deleteCategory(Long id);

    IPage<CategoryVO> getCategoryPage(int pageNum, int pageSize);
}
