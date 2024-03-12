package com.sky.controller.admin;

import com.sky.constant.RedisConstants;
import com.sky.result.Result;
import com.sky.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置营业状态:{}",status == 1 ? "营业中" : "打样中");
        RedisUtil.set(RedisConstants.SHOP_STATUS,status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getStatus(){
        int status = Integer.parseInt(RedisUtil.get(RedisConstants.SHOP_STATUS));
        return Result.success(status);
    }


}
