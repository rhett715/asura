package com.asura.admin.controller.common;

import com.asura.admin.common.constant.RedisConstants;
import com.asura.admin.common.core.result.JsonResult;
import com.asura.admin.config.properties.CaptchaProperties;
import com.asura.admin.service.SysConfigService;
import com.asura.admin.util.Base64Util;
import com.asura.admin.util.IDUtil;
import com.asura.admin.util.RedisCache;
import com.google.code.kaptcha.Producer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Rhett
 * @Date 2021/8/25
 * @Description
 */
@Api(value = "验证码操作处理控制器")
@RestController
public class CaptchaController {
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;
    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private CaptchaProperties captchaProperties;

    @ApiOperation(value = "生成验证码")
    @GetMapping("/captchaImage")
    public JsonResult<Map<String, Object>> getCode(HttpServletResponse response) {
        //禁止页面缓存
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        Map<String, Object> result = new HashMap<>();
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        result.put("captchaOnOff", captchaOnOff);
        if (!captchaOnOff) {
            return JsonResult.success(result);
        }
        // 保存验证码信息
        String uuid = IDUtil.simpleUUID();
        //验证码缓存中的key
        String verifyKey = RedisConstants.CAPTCHA_CODE_KEY + uuid;
        String capStr, code = null;
        BufferedImage image = null;
        // 生成验证码
        if ("math".equals(captchaProperties.getType())) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaProperties.getType())) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }
        //存入缓存，有效期2分钟
        redisCache.setCacheObject(verifyKey, code, RedisConstants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            result.put("msg", e.getMessage());
            return JsonResult.success(result);
        }
        result.put("uuid", uuid);
        result.put("img", Base64Util.encode(os.toByteArray()));
        return JsonResult.success(result);
    }
}
