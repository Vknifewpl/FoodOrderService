package com.foodorder.controller.common;

import com.foodorder.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传Controller
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 上传图片
     */
    @ApiOperation(value = "上传图片", notes = "上传图片文件（最大10MB），返回图片访问URL路径")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(
            @ApiParam(value = "图片文件", required = true) @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 获取文件后缀名
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成新文件名
        String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 创建目录
        File dir = new File(uploadPath).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        File destFile = new File(dir, newFilename);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }

        // 返回访问路径
        Map<String, String> result = new HashMap<>();
        result.put("url", "/images/" + newFilename);
        return Result.success(result);
    }
}
