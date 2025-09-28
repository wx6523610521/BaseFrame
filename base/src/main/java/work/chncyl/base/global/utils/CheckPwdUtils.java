package work.chncyl.base.global.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.security.config.WeakPasswordCheckConfig;

import java.util.regex.Pattern;

/**
 * 弱密码检测工具
 * @author chncyl
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CheckPwdUtils implements CommandLineRunner {
    private static WeakPasswordCheckConfig config;

    private static final String NUM_REGEX = "[0-9]+";
    private static final String LOWER_REGEX = "[a-z]+";
    private static final String UPPER_REGEX = "[A-Z]+";

    /**
     * 检测密码中字符长度
     * password    密码字符串
     *
     * @return 符合长度要求 返回true
     */
    public static boolean checkPasswordLength(String password) {
        return password.length() >= config.getMinLength() && password.length() <= config.getMaxLength();
    }

    /**
     * 检测密码中是否包含数字
     * password  密码字符串
     *
     * @return 包含数字 返回true
     */
    public static boolean checkContainDigit(String password) {
        return Pattern.compile(NUM_REGEX).matcher(password).find();
    }

    /**
     * 检测密码中是否包含字母（不区分大小写）
     * password            密码字符串
     *
     * @return 包含字母 返回true
     */
    public static boolean checkContainCase(String password) {
        char[] chPass = password.toCharArray();

        for (char pass : chPass) {
            if (Character.isLetter(pass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检测密码中是否包含小写字母
     * password            密码字符串
     *
     * @return 包含小写字母 返回true
     */
    public static boolean checkContainLowerCase(String password) {
        return Pattern.compile(LOWER_REGEX).matcher(password).find();
    }

    /**
     * 检测密码中是否包含大写字母
     * password            密码字符串
     *
     * @return 包含大写字母 返回true
     */
    public static boolean checkContainUpperCase(String password) {
        return Pattern.compile(UPPER_REGEX).matcher(password).find();
    }

    /**
     * 检测密码中是否包含特殊符号
     * password            密码字符串
     *
     * @return 包含特殊符号 返回true
     */
    public static boolean checkContainSpecialChar(String password) {
        char[] chPass = password.toCharArray();
        for (char pass : chPass) {
            if (WeakPasswordCheckConfig.SPECIAL_CHAR_SET.indexOf(pass) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 键盘规则匹配器 横向连续检测
     * password            密码字符串
     *
     * @return 含有横向连续字符串 返回true
     */
    public static boolean checkLateralKeyboardSite(String password) {
        String t_password = password;
        //将所有输入字符转为小写
        t_password = t_password.toLowerCase();
        int n = t_password.length();
        /*
          键盘横向规则检测
         */
        int arrLen = WeakPasswordCheckConfig.KEYBOARD_HORIZONTAL_ARR.length;
        int limit_num = config.getHorizontalKeyLimitNum();

        for (int i = 0; i + limit_num <= n; i++) {
            String str = t_password.substring(i, i + limit_num);
            String distinguishStr = password.substring(i, i + limit_num);

            for (int j = 0; j < arrLen; j++) {
                String configStr = WeakPasswordCheckConfig.KEYBOARD_HORIZONTAL_ARR[j];
                String revOrderStr = new StringBuffer(WeakPasswordCheckConfig.KEYBOARD_HORIZONTAL_ARR[j]).reverse().toString();

                //检测包含字母(区分大小写)
                if (config.isCheckContainUpperLowerCase()) {
                    //考虑 大写键盘匹配的情况
                    String UpperStr = WeakPasswordCheckConfig.KEYBOARD_HORIZONTAL_ARR[j].toUpperCase();
                    if ((configStr.contains(distinguishStr)) || (UpperStr.contains(distinguishStr))) {
                        return true;
                    }
                    //考虑逆序输入情况下 连续输入
                    String revUpperStr = new StringBuffer(UpperStr).reverse().toString();
                    if ((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
                        return true;
                    }
                } else {
                    if (configStr.contains(str)) {
                        return true;
                    }
                    //考虑逆序输入情况下 连续输入
                    if (revOrderStr.contains(str)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 键盘规则匹配器 斜向规则检测
     * password            密码字符串
     *
     * @return 含有斜向连续字符串 返回true
     */
    public static boolean checkKeyboardSlantSite(String password) {
        String t_password = password;
        t_password = t_password.toLowerCase();
        int n = t_password.length();
        /*
          键盘斜线方向规则检测
         */
        int arrLen = WeakPasswordCheckConfig.KEYBOARD_SLOPE_ARR.length;
        int limit_num = config.getSlopeKeyLimitNum();

        for (int i = 0; i + limit_num <= n; i++) {
            String str = t_password.substring(i, i + limit_num);
            String distinguishStr = password.substring(i, i + limit_num);
            for (int j = 0; j < arrLen; j++) {
                String configStr = WeakPasswordCheckConfig.KEYBOARD_SLOPE_ARR[j];
                String revOrderStr = new StringBuffer(WeakPasswordCheckConfig.KEYBOARD_SLOPE_ARR[j]).reverse().toString();
                //检测包含字母(区分大小写)
                if (config.isCheckContainUpperLowerCase()) {
                    //考虑 大写键盘匹配的情况
                    String UpperStr = WeakPasswordCheckConfig.KEYBOARD_SLOPE_ARR[j].toUpperCase();
                    if ((configStr.contains(distinguishStr)) || (UpperStr.contains(distinguishStr))) {
                        return true;
                    }
                    //考虑逆序输入情况下 连续输入
                    String revUpperStr = new StringBuffer(UpperStr).reverse().toString();
                    if ((revOrderStr.contains(distinguishStr)) || (revUpperStr.contains(distinguishStr))) {
                        return true;
                    }
                } else {
                    if (configStr.contains(str)) {
                        return true;
                    }
                    //考虑逆序输入情况下 连续输入
                    if (revOrderStr.contains(str)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 评估a-z,z-a这样的连续字符
     * password            密码字符串
     *
     * @return 含有a-z,z-a连续字符串 返回true
     */
    public static boolean checkSequentialChars(String password) {
        String t_password = password;
        boolean flag = false;
        int limit_num = config.getLogicLimitNum();
        int normal_count;
        int reversed_count;

        //检测包含字母(区分大小写)
        if (!config.isCheckContainUpperLowerCase()) {
            t_password = t_password.toLowerCase();
        }
        int n = t_password.length();
        char[] pwdCharArr = t_password.toCharArray();

        for (int i = 0; i + limit_num <= n; i++) {
            normal_count = 0;
            reversed_count = 0;
            for (int j = 0; j < limit_num - 1; j++) {
                if (pwdCharArr[i + j + 1] - pwdCharArr[i + j] == 1) {
                    normal_count++;
                    if (normal_count == limit_num - 1) {
                        return true;
                    }
                }
                if (pwdCharArr[i + j] - pwdCharArr[i + j + 1] == 1) {
                    reversed_count++;
                    if (reversed_count == limit_num - 1) {
                        return true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 评估aaaa,1111这样的相同连续字符
     * password            密码字符串
     *
     * @return 含有aaaa, 1111等连续字符串 返回true
     */
    public static boolean checkSequentialSameChars(String password) {
        int n = password.length();
        char[] pwdCharArr = password.toCharArray();
        boolean flag = false;
        int limit_num = config.getSequentialCharNum();
        int count;
        for (int i = 0; i + limit_num <= n; i++) {
            count = 0;
            for (int j = 0; j < limit_num - 1; j++) {
                if (pwdCharArr[i + j] == pwdCharArr[i + j + 1]) {
                    count++;
                    if (count == limit_num - 1) {
                        return true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 评估密码中包含的字符类型是否符合要求
     * password            密码字符串
     *
     * @return 符合要求 返回true
     */
    public static boolean evalPWD(String password) {
        if (!config.isEnable()) {
            return true;
        }
        if (password == null || password.isEmpty()) {
            return false;
        }
        boolean flag;

        /*
          检测长度
         */
        if (config.isCheckPasswordLength()) {
            flag = checkPasswordLength(password);
            if (!flag) {
                log.warn("弱密码检测：长度不足");
                return false;
            }
        }
        /*
          大小写，数字，特殊符号，满足其中三种即可
         */
        int i = 0;
        /*
          检测包含数字
         */
        if (config.isCheckContainDigit()) {
            flag = checkContainDigit(password);
            if (flag) {
                i++;
            }
        }
        /*
          检测包含字母(区分大小写)
         */
        if (config.isCheckContainUpperLowerCase()) {
            //检测包含小写字母
            if (config.isCheckContainLowerCase()) {
                flag = checkContainLowerCase(password);
                if (flag) {
                    i++;
                }
            }

            //检测包含大写字母
            if (config.isCheckContainUpperCase()) {
                flag = checkContainUpperCase(password);
                if (flag) {
                    i++;
                }
            }
        } else {
            boolean b = checkContainCase(password);
            if (!b) {
                log.warn("弱密码检测：未包含大小写英文字符");
                return false;
            }
        }
        /*
          检测包含特殊符号
         */
        if (config.isCheckContainSpecialChar()) {
            flag = checkContainSpecialChar(password);
            if (flag) {
                i++;
            }
        }
        /*
        检测包含字符类型数量
         */
        if (config.isCheckContainType() && i < config.getCheckContainTypeNum()) {
            log.warn("弱密码检测：字符类型不满足要求");
            return false;
        }
        /*
          检测键盘横向连续
         */
        if (config.isCheckHorizontalKeySequential()) {
            flag = checkLateralKeyboardSite(password);
            if (flag) {
                log.warn("弱密码检测：键盘横向连续检测不通过");
                return false;
            }
        }
        /*
          检测键盘斜向连续
         */
        if (config.isCheckSlopeKeySequential()) {
            flag = checkKeyboardSlantSite(password);
            if (flag) {
                log.warn("弱密码检测：键盘斜向连续检测不通过");
                return false;
            }
        }
        /*
          检测逻辑位置连续
         */
        if (config.isCheckLogicSequential()) {
            flag = checkSequentialChars(password);
            if (flag) {
                log.warn("弱密码检测：逻辑连续检测不通过");
                return false;
            }
        }
        /*
          检测相邻字符是否相同
         */
        if (config.isCheckSequentialCharSame()) {
            flag = checkSequentialSameChars(password);
            if (flag) {
                log.warn("弱密码检测：连续相同字符检测不通过");
            }
            return !flag;
        }
        return true;
    }

    @Override
    public void run(String... args) {
        CheckPwdUtils.config = SpringUtils.getBean(WeakPasswordCheckConfig.class);
    }
}