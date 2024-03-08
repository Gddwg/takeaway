package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传: {}",file);

        try {
            String[] originalFilename = file.getOriginalFilename().split(".");
            String last = originalFilename[originalFilename.length - 1];
            String filePath = aliOssUtil.upload(file.getBytes(), UUID.randomUUID() + last);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败：{}",e);
            return Result.error("文件上传失败");
        }
    }
}
