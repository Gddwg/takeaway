package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Integer countByCategoryId(Long id);

    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    void delete(List<Long> ids);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    List<Dish> list(Dish dish);
}
