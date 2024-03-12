package com.sky.controller.user;

import com.sky.constant.RedisConstants;
import com.sky.result.Result;
import com.sky.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
public class ShopController {

    @GetMapping("/status")
    public Result<Integer> getStatus(){
        int status = Integer.parseInt(RedisUtil.get(RedisConstants.SHOP_STATUS));
        log.info("获取店铺营业状态",status == 1 ? "营业中" : "打样中");
        return Result.success(status);
    }
}
