package com.foodorder.controller.common;

import com.foodorder.common.Result;
import com.foodorder.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * 验证码Controller
 */
@Api(tags = "验证码")
@RestController
public class CaptchaController {

    @Autowired
    private RedisUtil redisUtil;

    /** 验证码字符集（去除易混淆字符） */
    private static final String CHAR_POOL = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final int CODE_LENGTH = 4;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    /** 验证码有效期（秒） */
    private static final long CAPTCHA_EXPIRE = 60;

    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        // 生成随机验证码
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }

        // 绘制验证码图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        // 背景
        g.setColor(new Color(245, 245, 247));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // 绘制文字
        g.setFont(new Font("Arial", Font.BOLD, 26));
        for (int i = 0; i < CODE_LENGTH; i++) {
            g.setColor(new Color(random.nextInt(80), random.nextInt(80), random.nextInt(150)));
            // 随机旋转角度
            double theta = Math.toRadians(random.nextInt(30) - 15);
            g.rotate(theta, 24 + i * 24, 28);
            g.drawString(String.valueOf(code.charAt(i)), 12 + i * 24, 30);
            g.rotate(-theta, 24 + i * 24, 28);
        }
        // 干扰线
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT),
                    random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }
        // 噪点
        for (int i = 0; i < 30; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.fillRect(random.nextInt(WIDTH), random.nextInt(HEIGHT), 1, 1);
        }
        g.dispose();

        // 转Base64
        String base64;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            return Result.error("验证码生成失败");
        }

        // 存Redis，key为UUID
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        redisUtil.set("captcha:" + captchaKey, code.toString(), CAPTCHA_EXPIRE);

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("captchaImage", base64);
        return Result.success(result);
    }
}
