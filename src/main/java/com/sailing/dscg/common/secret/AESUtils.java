package com.sailing.dscg.common.secret;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description:
 * @Auther:史俊华
 * @Date:2018/9/614
 */
public class AESUtils {
    //生成AES秘钥，然后Base64编码
    public static String genKeyAES() throws Exception{
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        String base64Str = Base64Utils.encode(key.getEncoded());
        return base64Str;
    }

    //将Base64编码后的AES秘钥转换成SecretKey对象
    public static SecretKey loadKeyAES(String base64Key) throws Exception{
        byte[] bytes = Base64Utils.decode(base64Key);
        SecretKeySpec key = new SecretKeySpec(bytes, "AES");
        return key;
    }

    //加密
    public static byte[] encryptAES(byte[] source, String key) throws Exception{
        SecretKey secretKey = loadKeyAES(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(source);
    }

    //解密
    public static byte[] decryptAES(byte[] source, String key) throws Exception{
        SecretKey secretKey = loadKeyAES(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(source);
    }
}
