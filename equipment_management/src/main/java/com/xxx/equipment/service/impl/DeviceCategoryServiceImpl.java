package com.xxx.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxx.equipment.dto.CategoryDTO;
import com.xxx.equipment.entity.Device;
import com.xxx.equipment.entity.DeviceCategory;
import com.xxx.equipment.exception.BusinessException;
import com.xxx.equipment.mapper.DeviceCategoryMapper;
import com.xxx.equipment.mapper.DeviceMapper;
import com.xxx.equipment.service.DeviceCategoryService;
import com.xxx.equipment.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceCategoryServiceImpl implements DeviceCategoryService {

    private final DeviceCategoryMapper categoryMapper;
    private final DeviceMapper deviceMapper;

    @Override
    public void addCategory(CategoryDTO dto) {
        DeviceCategory category = new DeviceCategory();
        BeanUtils.copyProperties(dto, category);
        try {
            categoryMapper.insert(category);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("分类名称已存在");
        }
    }

    @Override
    public void updateCategory(CategoryDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        DeviceCategory existing = categoryMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("分类不存在");
        }
        DeviceCategory category = new DeviceCategory();
        BeanUtils.copyProperties(dto, category);
        try {
            categoryMapper.updateById(category);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("分类名称已存在");
        }
    }

    @Override
    public void deleteCategory(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }
        // 检查是否有关联设备
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getCategoryId, id);
        if (deviceMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该分类下存在设备，无法删除");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public IPage<CategoryVO> getCategoryPage(int pageNum, int pageSize) {
        Page<DeviceCategory> page = new Page<>(pageNum, pageSize);
        IPage<DeviceCategory> categoryPage = categoryMapper.selectPage(page, null);
        return categoryPage.convert(this::toVO);
    }

    private CategoryVO toVO(DeviceCategory category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
