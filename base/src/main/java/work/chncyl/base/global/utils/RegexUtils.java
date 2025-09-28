package work.chncyl.base.global.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 正则工具
 * @author chncyl
 */
public class RegexUtils {

    /**
     * URL是否规范（^((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&amp;%_\\./-~-]*)?$）
     */
    public static boolean isValidUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        return url.matches("^((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&amp;%_\\./-~-]*)?$");
    }

    /**
     * 密码是否符合规范（[a-zA-Z\d]{6,20}）
     */
    public static boolean isValidPassword(String password) {
        if (null == password) {
            return false;
        }

        return password.matches("^([^\\s'‘’]{6,20})$");
    }

    /**
     * 是否有效手机号码
     */
    public static boolean isMobileNum(String mobileNum) {
        if (null == mobileNum) {
            return false;
        }
        return mobileNum.matches("^((13[0-9])|(14[4,7])|(15[^4,\\D])|(17[0-9])|(18[0-9])|(19[0-9]))(\\d{8})$");
    }

    /**
     * 是否有效邮箱
     */
    public static boolean isEmail(String email) {
        if (null == email) {
            return false;
        }

        String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        return email.matches(regex);
    }

    /**
     * 是否是QQ邮箱
     */
    public static boolean isQQEmail(String email) {
        if (null == email) {
            return false;
        }

        return email.matches("^[\\s\\S]*@qq.com$");
    }

    /**
     * 是否数字(小数||整数)
     */
    public static boolean isNumber(String number) {
        if (null == number) {
            return false;
        }

        return number.matches("^[+-]?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d)+)?$");
    }

    /**
     * 是否整数
     */
    public static boolean isInt(String number) {
        if (null == number) {
            return false;
        }

        return number.matches("^[+-]?(([1-9]{1}\\d*)|([0]{1}))$");
    }

    /**
     * 是否正整数
     */
    public static boolean isPositiveInt(String number) {
        if (null == number) {
            return false;
        }

        return number.matches("^[+]?(([1-9]{1}\\d*)|([0]{1}))$");
    }

    /**
     * 是否日期yyyy-mm-dd(yyyy/mm/dd)
     */
    public static boolean isDate(String date) {
        if (null == date) {
            return false;
        }
        return date.matches("^([1-2]\\d{3})[/|\\-](0?[1-9]|10|11|12)[/|\\-]([1-2]?[0-9]|0[1-9]|30|31)$");
    }

    /**
     * 逗号分隔的正则表达式
     */
    public static String getCommaSparatedRegex(String str) {
        if (str == null) {
            return null;
        }
        return "^(" + str + ")|([\\s\\S]*," + str + ")|(" + str + ",[\\s\\S]*)|([\\s\\S]*," + str + ",[\\s\\S]*)$";
    }

    /**
     * 字符串包含
     */
    public static boolean contains(String str, String regex) {
        if (str == null || regex == null) {
            return false;
        }

        return str.matches(regex);
    }

    /**
     * 是否为16-22位银行账号
     */
    public static boolean isBankAccount(String bankAccount) {
        if (null == bankAccount) {
            return false;
        }

        return bankAccount.matches("^\\d{16,22}$");
    }

    public static boolean isValidDomain(String domain) {
        if (StringUtils.isBlank(domain)) {
            return false;
        }
        return domain.matches("^[a-zA-Z0-9\u4e00-\u9fa5][-a-zA-Z0-9\u4e00-\u9fa5]{0,62}(\\.[a-zA-Z0-9\u4e00-\u9fa5][-a-zA-Z0-9\u4e00-\u9fa5]{0,62})+\\.?$");
    }

    /**
     * 是否是qq号码
     */
    public static boolean isQQnumber(String qq) {
        if (StringUtils.isBlank(qq)) {
            return false;
        }
        return qq.matches("^[1-9][0-9]{4,11}?$");
    }


    public static String fuzzyPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.replaceAll("^(\\w{3})\\w+(\\w{4})$", "$1****$2");
    }
}