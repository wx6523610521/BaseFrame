package work.chncyl.base.global.utils;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密解密工具类
 *
 * @author chncyl
 */
public class EncryDecryUtils {
    /**
     * ================================== 非对称加解密 ====================================
     */
    private static final Map<String, RSAKeyPair> keyPairMap = new ConcurrentHashMap<>();

    private static final String DEFAULT_PUBLIC_KEY = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA0FO9q7j7hMgpqApNELtXSOVlcEyPArY815QOWcANmwkhJaesdyz5H9k7gOBamOhP70dPyS1OQ78Auvb0/n6GfGP/wffPN/AiY2v2XKeh6zZ6q7JuW57BmQyE9yvKAC/cIO4orv9lThLTktcuy/bjaU94RL4K+8XO8yOa27DCDHtf91X94ty5Xg30AbkCXB+PAoloS78W4ZbSHdPg/agt8DJzOIPLToP1PGr+Bhq7qijxq1AV4TzwVwZ4r4j+x75r6Iun3t47IOH2JCzidKjNf6g4H33Jmd6w33xlORWn2dhqCtb1M6O8/fxFjsvSBoYYPe4t6epBHp4jQf4RnwYJ2URCfGGCjOwhdJyPushYO6YG6YuSo4bQHbQvq6hnc7kqk/oOUlGaKseoPmDIUy3M6o5Qg69euIgDVU59zxsbkN89ie/fX1Mp45QtxgBbC0ppMzRMAw5Rqc23dbex5JtrXu0bJDkfx1HTT/fh4JpERjorncRo2351ByN8lEiaTh8Pt9J7glOHroXMeuyZej3MQSGP1z1xNqNbUPcLqcWJlSmtzZmTOsp0XB7hWHfe/bdEuJLRaocvUbTw6U0Rpj3lfia6lh6+kK17qZveubbT4apeZwxzOpCcePBxE9zIAi++g3IUdvSd2sb0ejd9E5+B0sZ54nuxp4v6nlbKaUzYHz0CAwEAAQ==";
    private static final String DEFAULT_PRIVATE_KEY = "MIIJRAIBADANBgkqhkiG9w0BAQEFAASCCS4wggkqAgEAAoICAQDQU72ruPuEyCmoCk0Qu1dI5WVwTI8CtjzXlA5ZwA2bCSElp6x3LPkf2TuA4FqY6E/vR0/JLU5DvwC69vT+foZ8Y//B98838CJja/Zcp6HrNnqrsm5bnsGZDIT3K8oAL9wg7iiu/2VOEtOS1y7L9uNpT3hEvgr7xc7zI5rbsMIMe1/3Vf3i3LleDfQBuQJcH48CiWhLvxbhltId0+D9qC3wMnM4g8tOg/U8av4GGruqKPGrUBXhPPBXBniviP7Hvmvoi6fe3jsg4fYkLOJ0qM1/qDgffcmZ3rDffGU5FafZ2GoK1vUzo7z9/EWOy9IGhhg97i3p6kEeniNB/hGfBgnZREJ8YYKM7CF0nI+6yFg7pgbpi5KjhtAdtC+rqGdzuSqT+g5SUZoqx6g+YMhTLczqjlCDr164iANVTn3PGxuQ3z2J799fUynjlC3GAFsLSmkzNEwDDlGpzbd1t7Hkm2te7RskOR/HUdNP9+HgmkRGOiudxGjbfnUHI3yUSJpOHw+30nuCU4euhcx67Jl6PcxBIY/XPXE2o1tQ9wupxYmVKa3NmZM6ynRcHuFYd979t0S4ktFqhy9RtPDpTRGmPeV+JrqWHr6QrXupm965ttPhql5nDHM6kJx48HET3MgCL76DchR29J3axvR6N30Tn4HSxnnie7Gni/qeVsppTNgfPQIDAQABAoICACvmGYfAGTAnxa9lTEwETiZMQI3jdBdMM5Hx3vxGnhdXWybGKG+MiIqPihxkKnMiMQXo7wUjiOR5sTu0onemTcyUfeZvw7iz32s36tgnctVPy5W2qc6gq4cGvlYfYheb/jTqOqR1qPFEwlSlB3VvdwhP+LXznfjj+/H8RLuaial7rgM+mZtei6koI4zhYX/sHoSYU+PyRm4PDAPBDB4nfFarKuBz1TmcUOcEXLcIlxcKPbKt0458+uA7ut6ybY3FsJN2Kk+qYKdTNjz1QFr43yTRW9I2/W5xILBXVDUtPQuIsP/LwCu7iy2yJS4sAPvi8eTK9d3ewQ4koKVdVnU+HoDKfeMC0IFEowUWrcHygP/bynTVZZcfkllZvOLyBhvDBi33VtDWbaD2xuFKE30+zs+kli+eRe0UhilGba3SmrcuFp3wrG7YkDCV5bwS0f+Fqb5CtD+5ILuDF4fBw9cxflVoIbajQHeMA9gEqgi3kZqQg9reBrVTwMv1ERqu9EwhXxjzi68eslqddGzed8+1ticSFo56WVvQvhn3xjJOSRsb4bZ/I8BsUnZYLGZrsN35Fk5klTjHnMcZYlU68UfxRMme1KE3TW6L9K9bmKU4O7DLJCETAZN9S7Gn+eLqOtbw0QJhRk9AMZq8ndQ5MiG262DhjqkoMNuRr1tbBCfu/Q9BAoIBAQD1/jZbTUqz8j80p2fNczFdfnHUPZ58eOeLnuEno2IP28PqGDkZfVAtme48o8Uh3sQ9v4syqF5uZbz9pZjkuQY55lmeLBMCDaT3Gme3gSL8QCCdLU6MR0lI9yDJFYK9ASlns1MUeA+F5All77NaF1StUELc6gd4VyohlYg36gGdBLRvbB5YSegRLot+5nLaznSJPjvFZnFeoeYzKN77rcM37hIcpQiB3RY5KqcWqA05mvJSXUqigukosjnD9Pz/3RZ4bornDiQ5SxxP8mTFq0wcMmXrUhTAtfdpeAhp9pFXZLfU/NW7e/EgTXIFLFdCxBFGcRqAQqLVGsOtakX7zmoJAoIBAQDYzUX5Y6Mvd29b6q9wcM38Q8s/dk2S5WmdyoDkoBLTpeturCGmLbJxRtm+i0OYZ6J9GJXvNQK+dpijd1TLMTyDyrZuWiSEcH5q1WrONQfJtOHds1RGHpueYzjEuVtbJH3+SPTm+smL4Y61M/Vi+47IBSZMMqblRCquZBE3rEZSmhP8BKBn6qqwsg+OPPrmwcxLP2Sn6Iw1c3sxB8qFIh/uUqblU+/rLwHHjuad/ji2h8li+ik4eGEqv84ZNh1sgsWPjr15MBx+zqaTg5444Ye/icMVJHAq5YY96EU+4ZVoM3mi7/NCiJeQUrV20pdXpEoQyC8bnTfBiOUcF+YoyyiVAoIBAQChZYxWdSzZScysiFWz16uA8zBOF6u/cThCRBjn/+ucCg3Nrtzv22datA86XB1ALr/E38K9lvh2KGxKY3cgQkFShc8w1ywVgcxFkF6SaLkCljSPy6UoprDvna02IdXomjWHY4HUAT32E/sSlaWwK3SUTf/AoBAMpevvowN/5Bnw4qBcJqz2Mm9/rnE57otXAnQ1g86FosQpWcIGuP8sVAKs1JevkZw2UE9/bHu6gsJClGX/IlSQsaG24NDDwB6q5W0T0Ttzg7TCnoRR8/CoPM6wBWUXc4BIPFu7KFUk6PDNTTjMFqNI4nX/QrDXshMCrE/FTvobQx1BAtF2Gy8VmoW5AoIBAQC28ZDofwGG3lvjzke4tBe9lV9+789J87iJ5AWb5s+8NThTrYC/f7QiDk1TkqS5iEn/Ea4DqlHkcqMe3WV6CNQtyfLjb8F82Ih0kh6MXwdxV+d4cwjcGpvqrYOLzXWflU2UM/AksN894FnR4kRhADFd66qDhgJ7WGhDvEEjH4Y/0bCnaygISGc8rySdPCvkaQiBPr8rSPp3aDk+CHsGmOMk8i3POmMrrADualGEZK9qp25yzPHXqw0jS7MYZyexFzCqkSKljsmc37iEpf3vtbAdGpy5zIOyTvhWaRd1llnDEhzEN4KoVCbBLGngGJiI5iNxwdqujJ4t+9yJNkmLhtN5AoIBAQCBhQm+R8yef6QoZ+segYSzPd+/78653fi8cbqF01MeX/Pc/bipNzgJbGl12NipZqg/dWoj5lOBYuTosXZnE+0YYWmjOmyLjYD4ooSQNtn/LIRieox22zaogjq0H9Ocdqj+7h5ToPa5JJlYQ7KP7EM4YlYMyVJHHfbIkd+88kQHvMWZDKeiNLeRqHNYhARWbqnsZhLzv6siU/nalEBRJvH+dom5OvZGmezGF6tpzWeN68ESCClXDa2Z6+ZcLu/kVgsxigE2XXwPOF0c5VcWzqJmq8UPv8y5MwJX1U6JDzbgQcLZZbxEN3fqG5QhiPeYPb48NG2xUJ5gkIl1GR6D5mAJ";

    private static KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.generateKeyPair();
    }

    public static String getDefaultPublicKey() {
        return DEFAULT_PUBLIC_KEY;
    }

    /**
     * 生成公钥和私钥
     */
    public static RSAKeyPair generateKeyPair() throws NoSuchAlgorithmException {
        // 生成公钥和私钥
        return generateKeyPair(null, 2048);
    }

    /**
     * 生成公钥和私钥
     *
     * @param keySize 密钥长度 推荐使用 2048 位 或更长（如 4096 位），1024 位已不安全
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static RSAKeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        // 生成公钥和私钥
        return generateKeyPair(null, keySize);
    }

    public static RSAKeyPair generateKeyPair(String mark) throws NoSuchAlgorithmException {
        // 生成公钥和私钥
        return generateKeyPair(mark, 2048);
    }

    /**
     * 生成临时公钥和私钥,不会缓存，不支持mark相关操作
     */
    public static RSAKeyPair generateTemporaryKeyPair(String mark) throws NoSuchAlgorithmException {
        // 生成公钥和私钥
        RSAKeyPair rsaKeyPair = generateKeyPair(mark, 2048);
        keyPairMap.remove(mark);
        return rsaKeyPair;
    }

    public static RSAKeyPair generateKeyPair(String mark, int keySize) throws NoSuchAlgorithmException {
        if (StringUtils.isBlank(mark)) {
            mark = UUID.randomUUID().toString();
        }
        RSAKeyPair rsaKeyPair = new RSAKeyPair();
        rsaKeyPair.setMark(mark);
        KeyPair keyPair = generateRSAKeyPair(keySize);
        rsaKeyPair.setKeyPair(keyPair);
        rsaKeyPair.setPublicKey(getPublicKeyAsString(keyPair));
        rsaKeyPair.setPrivateKey(getPrivateKeyAsString(keyPair));

        keyPairMap.put(mark, rsaKeyPair);
        return rsaKeyPair;
    }

    public static RSAKeyPair getKeyPair(String mark) {
        return keyPairMap.get(mark);
    }

    private static String getPublicKeyAsString(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    private static String getPrivateKeyAsString(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static String encryptByMark(String content, String mark) throws Exception {
        RSAKeyPair keyPair = getKeyPair(mark);
        return encryptByPublicKey(content, keyPair.getPublicKey());
    }

    public static String decryptByMark(String content, String mark) throws Exception {
        RSAKeyPair keyPair = getKeyPair(mark);
        return decryptByPrivateKey(content, keyPair.getPrivateKey());
    }


    public static String encryptByPublicKey(String content, String publicKey) throws Exception {
        PublicKey punKey = loadPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, punKey);
        byte[] encryptedBytes = cipher.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptByPrivateKey(String content, String privateKey) throws Exception {
        PrivateKey priKey = loadPrivateKey(privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(content);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static String encrypt(String content) throws Exception {
        return encryptByPublicKey(content, DEFAULT_PUBLIC_KEY);
    }

    public static String decrypt(String content) throws Exception {
        return decryptByPrivateKey(content, DEFAULT_PRIVATE_KEY);
    }

    private static PublicKey loadPublicKey(String publicKeyStr) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


    @Data
    public static class RSAKeyPair {
        /**
         * 标识符
         */
        private String mark;

        private String publicKey;

        private String privateKey;

        private KeyPair keyPair;
    }

    /**
     * ================================= MD5加密 ================================
     */
    public static String md5(String str) {
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(str);
    }

    public static void main(String[] args) throws Exception {
        RSAKeyPair rsaKeyPair = generateKeyPair(2048);
        System.out.println("公钥: " + rsaKeyPair.getPublicKey());
        System.out.println("私钥: " + rsaKeyPair.getPrivateKey());
    }
}
