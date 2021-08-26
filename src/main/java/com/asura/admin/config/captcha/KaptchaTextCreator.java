package com.asura.admin.config.captcha;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.util.Random;

/**
 * @Author Rhett
 * @Date 2021/8/9
 * @Description 验证码文本生成器
 */
public class KaptchaTextCreator extends DefaultTextCreator {
    private static final String[] C_NUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");

    @Override
    public String getText() {
        int result;
        Random random = new Random();
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        StringBuilder suChinese = new StringBuilder();
        int randomOperands = (int) Math.round(Math.random() * 2);
        if (randomOperands == 0) {
            result = x * y;
            suChinese.append(C_NUMBERS[x]);
            suChinese.append("*");
            suChinese.append(C_NUMBERS[y]);
        } else if (randomOperands == 1) {
            if (!(x == 0) && y % x == 0) {
                result = y / x;
                suChinese.append(C_NUMBERS[y]);
                suChinese.append("/");
                suChinese.append(C_NUMBERS[x]);
            } else {
                result = x + y;
                suChinese.append(C_NUMBERS[x]);
                suChinese.append("+");
                suChinese.append(C_NUMBERS[y]);
            }
        } else if (randomOperands == 2) {
            if (x >= y) {
                result = x - y;
                suChinese.append(C_NUMBERS[x]);
                suChinese.append("-");
                suChinese.append(C_NUMBERS[y]);
            } else {
                result = y - x;
                suChinese.append(C_NUMBERS[y]);
                suChinese.append("-");
                suChinese.append(C_NUMBERS[x]);
            }
        } else {
            result = x + y;
            suChinese.append(C_NUMBERS[x]);
            suChinese.append("+");
            suChinese.append(C_NUMBERS[y]);
        }
        suChinese.append("=?@").append(result);
        return suChinese.toString();
    }
}
