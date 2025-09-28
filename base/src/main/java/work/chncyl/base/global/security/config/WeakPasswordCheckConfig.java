package work.chncyl.base.global.security.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 弱密码检查配置
 */
@Getter
@Data
@Configuration
@ConfigurationProperties(prefix = "weak-password-check")
public class WeakPasswordCheckConfig {

    private boolean enable = true;
    /**
     * 是否检测密码必须包含的字符种类数（大写字母、小写字母、特殊字符、数字）
     */
    private boolean checkContainType = true;
    /**
     * 密码至少须包含的字符种类数  最多为4
     */
    private int checkContainTypeNum = 2;
    /**
     * 是否进行密码长度检查
     */
    private boolean checkPasswordLength = true;
    /**
     * 密码最小长度
     */
    private int minLength = 6;
    /**
     * 密码最大长度
     */
    private int maxLength = 20;
    /**
     * 是否检测包含数字
     * ！！！如不检测则checkContainTypeNum上限应当 -1，否则检测永不通过
     */
    private boolean checkContainDigit = true;
    /**
     * 是否检测同时包含大小写字母，区分密码口令大小写
     */
    private boolean checkContainUpperLowerCase = true;
    /**
     * 是否检测包含小写字母
     * ！！！如不检测则checkContainTypeNum值上限应当 -1，否则检测永不通过
     */
    private boolean checkContainLowerCase = true;
    /**
     * 是否检测包含大写字母
     * ！！！如不检测则checkContainTypeNum值上限应当 -1，否则检测永不通过
     */
    private boolean checkContainUpperCase = true;
    /**
     * 是否检测包含特殊符号
     * ！！！如不检测则checkContainTypeNum值上限应当 -1，否则检测永不通过
     */
    private boolean checkContainSpecialChar = false;
    /**
     * 是否检测键盘横向连续，如qwe 包括逆序ewq
     */
    private boolean checkHorizontalKeySequential = true;
    /**
     * 允许键盘横向最大连续数，如为空，则设置为默认值
     */
    private int horizontalKeyLimitNum = 3;
    /**
     * 是否检测键盘斜向连续 如qaz 包括逆序
     */
    private boolean checkSlopeKeySequential = true;
    /**
     * 允许键盘斜向最大连续数
     */
    private int slopeKeyLimitNum = 3;
    /**
     * 是否检测逻辑位置连续  如abc
     */
    private boolean checkLogicSequential = true;
    /**
     * 允许逻辑位置连续最大数值
     */
    private int logicLimitNum = 3;
    /**
     * 是否检测连续相同字符，如aaa、111、!!!等
     */
    private boolean checkSequentialCharSame = true;
    /**
     * 允许连续相同字符最大数值
     */
    private int sequentialCharNum = 3;
    /**
     * 允许的特殊符号集合
     */
    public static final String SPECIAL_CHAR_SET = "!\"//$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    /**
     * 键盘横向连续规则
     */
    public static final String[] KEYBOARD_HORIZONTAL_ARR = new String[]{"01234567890", "qwertyuiop", "asdfghjkl", "zxcvbnm"};
    /**
     * 键盘斜线连续规则
     */
    public static final String[] KEYBOARD_SLOPE_ARR = new String[]{
            "1qaz", "2wsx", "3edc", "4rfv", "5tgb", "6yhn", "7ujm", "8ik,", "9ol.", "0p;/",
            "=[;.", "-pl,", "0okm", "9ijn", "8uhb", "7ygv", "6tfc", "5rdx", "4esz"};

    public void setCheckContainTypeNum(int checkContainTypeNum) {
        this.checkContainTypeNum = Math.max(checkContainTypeNum, 0);
        if (checkContainTypeNum > 4)
            this.checkContainTypeNum = 4;
    }

    public void setHorizontalKeyLimitNum(Integer horizontalKeyLimitNum) {
        if (horizontalKeyLimitNum == null) {
            this.horizontalKeyLimitNum = 3;
        } else {
            this.horizontalKeyLimitNum = Math.max(horizontalKeyLimitNum, 0);
        }
    }

    public void setSlopeKeyLimitNum(Integer slopeKeyLimitNum) {
        if (slopeKeyLimitNum == null) {
            this.slopeKeyLimitNum = 3;
        } else {
            this.slopeKeyLimitNum = Math.max(slopeKeyLimitNum, 0);
        }
    }

    public void setLogicLimitNum(Integer logicLimitNum) {
        if (logicLimitNum == null) {
            this.logicLimitNum = 3;
        } else {
            this.logicLimitNum = Math.max(logicLimitNum, 0);
        }
    }

    public void setSequentialCharNum(Integer sequentialCharNum) {
        if (sequentialCharNum == null) {
            this.sequentialCharNum = 3;
        } else {
            this.sequentialCharNum = Math.max(sequentialCharNum, 0);
        }
    }

}