package work.chncyl.base.global.utils;

import org.apache.commons.lang3.StringUtils;

public class GlobalUtils {
    private static final char[] NUM = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] LOWER = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] UPPER = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] LETTER = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * =================================== 随机字符串生成 ===============================
     */

    public static String randomNum(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(NUM[(int) (Math.random() * NUM.length)]);
        }
        return sb.toString();
    }

    public static String randomLower(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(LOWER[(int) (Math.random() * LOWER.length)]);
        }
        return sb.toString();
    }

    public static String randomUpper(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(UPPER[(int) (Math.random() * UPPER.length)]);
        }
        return sb.toString();
    }

    public static String randomLetter(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(LETTER[(int) (Math.random() * LETTER.length)]);
        }
        return sb.toString();
    }

    public static String randomChar(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt((int) (Math.random() * CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * =================================== 62进制转换 ==========================================
     */

    private static final int SCALE = 62;

    /**
     * 将数字转为62进制
     *
     * @param num    Long 型数字
     * @param length 转换后的字符串长度，不足则左侧补0
     * @return 62进制字符串
     */
    public static String convertTo62(long num, Integer length) {
        StringBuilder sb = new StringBuilder();
        int remainder = 0;

        while (num > SCALE - 1) {
            // 对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转（reverse）字符串
            remainder = Long.valueOf(num % SCALE).intValue();
            sb.append(CHARS.charAt(remainder));

            num = num / SCALE;
        }

        sb.append(CHARS.charAt(Long.valueOf(num).intValue()));
        String value = sb.reverse().toString();
        if (length != null && length > value.length()) {
            return StringUtils.leftPad(value, length, '0');
        }
        return value;
    }

    public static String convertTo62(long num) {
        return convertTo62(num, null);
    }

    /**
     * 62进制字符串转为数字
     *
     * @param str 编码后的62进制字符串
     * @return 解码后的 10 进制字符串
     */
    public static long deconvert62(String str) {
        // 将 0 开头的字符串进行替换
        str = str.replace("^0*", "");
        long num = 0;
        int index;
        for (int i = 0; i < str.length(); i++) {
            // 查找字符的索引位置
            index = CHARS.indexOf(str.charAt(i));
            // 索引位置代表字符的数值
            num += (long) (index * (Math.pow(SCALE, str.length() - i - 1)));
        }
        return num;
    }

    /**
     * =============================== byte[] 与 16进制字符串转换 ======================================
     */
    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7'
            , '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 常用于加密后的 byte[]转string
     * @param data byte数组
     * @return 转为16进制后的字符串
     */
    public static String bytesToHexString(byte[] data) {
        String string = "";
        try {
            char[] chars = new char[data.length << 1];
            //十六进制数一个四位，byte一个八位
            for (int i = 0, j = 0; i < data.length; i++) {
                chars[j++] = DIGITS[(data[i] >> 4) & 0x0F];
                chars[j++] = DIGITS[data[i] & 0x0F];
            }
            string = new String(chars);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }

    public static byte[] hexStringToBytes(String data) {
        //两个四位十六进制字符合成一个八位byte
        byte[] bytes = new byte[data.length() / 2];
        char[] chars = data.toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((hexCharToByte(chars[i * 2]) << 4) | hexCharToByte(chars[i * 2 + 1]));
        }
        return bytes;
    }

    private static byte hexCharToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
