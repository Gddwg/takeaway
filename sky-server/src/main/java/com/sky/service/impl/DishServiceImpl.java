package com.sky.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.RedisConstants;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.redis.DishRedis;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.utils.RedisUtil;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.sky.constant.MessageConstant.DISH_BE_RELATED_BY_SETMEAL;
import static com.sky.constant.MessageConstant.DISH_ON_SALE;
import static com.sky.constant.RedisConstants.CACHE_NULL_TTL;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    SetmealDishMapper setmealDishMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插入一条数据
        dishMapper.insert(dish);

        Long id = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors != null && flavors.size() > 0){
            flavors.forEach(df->df.setDishId(id));
            //向口味表插入多条数据
            dishFlavorMapper.insertBatch(flavors);
        }
        removeRedis(dish);
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(DISH_ON_SALE);
            }
        }

        List<Long> setmealIdsByDishId = setmealDishMapper.getSetmealIdsByDishId(ids);
        if(setmealIdsByDishId != null && setmealIdsByDishId.size() != 0){
            throw new DeletionNotAllowedException(DISH_BE_RELATED_BY_SETMEAL);
        }
        dishMapper.delete(ids);
        dishFlavorMapper.deleteByDishId(ids);

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
        removeRedis(dishMapper.getById(id));
    }

    @Transactional
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        DishVO dishVO = new DishVO();
        Dish dish = dishMapper.getById(id);
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavorMapper.getByDishId(id));
        return dishVO;
    }

    @Transactional
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        Long id = dish.getId();
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        dishFlavorMapper.deleteByDishId(ids);
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors != null && flavors.size() > 0){
            flavors.forEach(df->df.setDishId(id));
            //向口味表插入多条数据
            dishFlavorMapper.insertBatch(flavors);
        }
        removeRedis(dish);
    }
    public List<DishVO> listWithFlavor(Long categoryId) {

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    public void removeRedis(Dish dish){
        Long categoryId = dish.getCategoryId();
        DishRedis.remove(categoryId);
    }

}
