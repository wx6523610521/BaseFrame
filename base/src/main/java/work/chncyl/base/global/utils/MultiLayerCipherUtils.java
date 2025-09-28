package work.chncyl.base.global.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * <p>Description: 多层密码器</p>
 * @author chncyl
 */
public class MultiLayerCipherUtils {

    /**
     * 加密数据
     *
     * @param data 待加密数据
     * @param key  密钥
     * @param enableObfuscation 是否启用混淆
     * @return 加密后的数据
     * @throws Exception 加密过程异常
     */
    public static byte[] encrypt(byte[] data, byte[] key, boolean enableObfuscation) throws Exception {
        byte[][] subkeys = expandKey(key);
        byte[] processed = data.clone();

        xorLayer(processed, subkeys[0]);

        if (enableObfuscation) {
            permutationEncrypt(processed, subkeys[1]);
            substitutionEncrypt(processed, subkeys[2]);
        }

        return processed;
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key, boolean enableObfuscation) throws Exception {
        byte[][] subkeys = expandKey(key);
        byte[] processed = ciphertext.clone();

        if (enableObfuscation) {
            substitutionDecrypt(processed, subkeys[2]);
            permutationDecrypt(processed, subkeys[1]);
        }

        xorLayer(processed, subkeys[0]);

        return processed;
    }

    private static byte[][] expandKey(byte[] key) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha256.digest(key);
        byte[][] subkeys = new byte[3][8];
        System.arraycopy(hash, 0, subkeys[0], 0, 8);  // XOR key
        System.arraycopy(hash, 8, subkeys[1], 0, 8);  // Permutation seed
        System.arraycopy(hash, 16, subkeys[2], 0, 8); // Substitution seed
        return subkeys;
    }

    private static void xorLayer(byte[] data, byte[] subkey) {
        int keyLen = subkey.length;
        for (int i = 0; i < data.length; i++) {
            data[i] ^= subkey[i % keyLen];
        }
    }

    private static void permutationEncrypt(byte[] data, byte[] subkey) {
        processPermutation(data, subkey, true);
    }

    private static void permutationDecrypt(byte[] data, byte[] subkey) {
        processPermutation(data, subkey, false);
    }

    private static void processPermutation(byte[] data, byte[] subkey, boolean encrypt) {
        long seed = bytesToLong(subkey);
        Random rand = new Random(seed);
        final int blockSize = 8;
        int blocks = data.length / blockSize;

        for (int block = 0; block < blocks; block++) {
            int start = block * blockSize;
            List<Integer> perm = generatePermutation(blockSize, rand);
            byte[] temp = new byte[blockSize];

            if (encrypt) {
                for (int i = 0; i < blockSize; i++) {
                    temp[i] = data[start + perm.get(i)];
                }
            } else {
                for (int i = 0; i < blockSize; i++) {
                    temp[perm.get(i)] = data[start + i];
                }
            }

            System.arraycopy(temp, 0, data, start, blockSize);
        }
    }

    private static List<Integer> generatePermutation(int size, Random rand) {
        List<Integer> permutation = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            permutation.add(i);
        }
        for (int i = size - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Collections.swap(permutation, i, j);
        }
        return permutation;
    }

    private static void substitutionEncrypt(byte[] data, byte[] subkey) {
        byte[] sBox = generateSBox(subkey);
        for (int i = 0; i < data.length; i++) {
            data[i] = sBox[data[i] & 0xFF];
        }
    }

    private static void substitutionDecrypt(byte[] data, byte[] subkey) {
        byte[] sBox = generateSBox(subkey);
        byte[] invSBox = new byte[256];
        for (int i = 0; i < 256; i++) {
            invSBox[sBox[i] & 0xFF] = (byte) i;
        }
        for (int i = 0; i < data.length; i++) {
            data[i] = invSBox[data[i] & 0xFF];
        }
    }

    private static byte[] generateSBox(byte[] subkey) {
        byte[] sBox = new byte[256];
        for (int i = 0; i < 256; i++) {
            sBox[i] = (byte) i;
        }
        Random rand = new Random(bytesToLong(subkey));
        for (int i = 255; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            byte temp = sBox[i];
            sBox[i] = sBox[j];
            sBox[j] = temp;
        }
        return sBox;
    }

    private static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    // 测试用例
    public static void main(String[] args) throws Exception {
        String original = "HelloWorld! 你好，世界！";
        byte[] data = original.getBytes(StandardCharsets.UTF_8);
        byte[] key = "MySecretKey123!".getBytes(StandardCharsets.UTF_8);

        System.out.println("原始数据 (" + data.length + " bytes):");
        System.out.println(original);
        // 带混淆的加密
        byte[] encrypted = encrypt(data, key, true);
        System.out.println("\n加密结果 (" + encrypted.length + " bytes):");
        System.out.println(GlobalUtils.bytesToHexString(encrypted));

        // 解密
        byte[] decrypted = decrypt(encrypted, key, true);
        System.out.println("\n解密结果:");
        System.out.println(new String(decrypted, StandardCharsets.UTF_8));
    }
}