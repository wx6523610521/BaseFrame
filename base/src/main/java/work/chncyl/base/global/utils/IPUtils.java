package work.chncyl.base.global.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * IP地址
 *
 * @Author chncyl
 */
@Slf4j
public class IPUtils {
    private static final Logger logger = LoggerFactory.getLogger(IPUtils.class);

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        String unknown = "unknown";
        if (StringUtils.isBlank(ipAddress) || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isEmpty(ipAddress) || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isEmpty(ipAddress) || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ipAddress) || unknown.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                //根据网卡取本机配置的IP
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (Exception e) {
                    log.error("获取当前网络ip:" + e.getMessage());
                }
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            int apAddressNum = ipAddress.indexOf(",");
            if (apAddressNum > 0) {
                ipAddress = ipAddress.substring(0, apAddressNum);
            }
        }
        return ipAddress;
    }

    /**
     * IPv4串转数字
     */
    public static long ipToLong(String ip) {
        long result = 0;
        String[] ipAddressInArray = ip.split("\\.");
        for (int i = 3; i >= 0; i--) {
            long ipAddress = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ipAddress << (i * 8);
        }
        return result;
    }

    /**
     * 数字转IPv4串
     */
    public static String longToIp(long ip) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.insert(0, Long.toString(ip & 0xff));
            if (i < 3) {
                sb.insert(0, ".");
            }
            ip >>= 8;
        }
        return sb.toString();
    }

    /**
     * 查看IP是否在某个IP段内
     *
     * @param ip IP地址
     * ranges IP段，格式如：“192.168.1.1-192.168.1.255”
     */
    public static boolean isIpInRanges(String ip, String[] ranges) {
        long ipNum = ipToLong(ip);
        for (String range : ranges) {
            String[] rangeArr = range.split("-");
            long startIpNum = ipToLong(rangeArr[0]);
            long endIpNum = ipToLong(rangeArr[1]);
            if (ipNum >= startIpNum && ipNum <= endIpNum) {
                return true;
            }
        }
        return false;
    }

}
