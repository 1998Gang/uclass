package cqupt.jyxxh.uclass.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 加密工具类
 * 使用AES加密方法
 * @author 彭渝刚
 * @version 1.0.0
 * @date created in 20:33 2020/1/5
 */
@Component
public class EncryptionUtil {

    Logger logger= LoggerFactory.getLogger(EncryptionUtil.class);

    @Value("${AES.SECRET_KEY}")
    private String SECRET_KEY;                  //加密密钥

    /**
     * 将字符串进行加密处理
     * @param str 需要加密的字符串
     * @return 加密之后的数据，若出错返回“encrypt_false”
     */
    public String encrypt(String str){
        try {
            // 1.选择加密方法
            KeyGenerator aes = KeyGenerator.getInstance("AES");
            // 2.设置密钥的长度
            aes.init(128);
            // 3.创建一个加密器
            Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // 4.加载密钥
            SecretKeySpec secretKeySpec=new SecretKeySpec(SECRET_KEY.getBytes(),"AES");
            // 5.选择加密
            instance.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            // 6.加密数据
            byte[] bytes = instance.doFinal(str.getBytes(StandardCharsets.UTF_8));
            // 7.使用Base64将得到的字节数组转为字符串
            Base64.Encoder encoder = Base64.getEncoder();
            // 8.返回加密后的数据
            return encoder.encodeToString(bytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            logger.error("加密出错！错误信息：[{}]",e.getMessage());
        }
        // 失败返回“false”
        return "encrypt_false";
    }

    /**
     * 将加密的字符串解密
     * @param str 需要被解密的字符串
     * @return 解密后的数据（字符串），若出错返回“decrypt_false”！
     */
    public String decrypt(String str){
        try {
            // 1.选择解密方法
            KeyGenerator aes = KeyGenerator.getInstance("AES");
            // 2.选择密钥长度
            aes.init(128);
            // 3.创建一个加密器
            Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // 4.加载密钥
            SecretKeySpec secretKeySpec=new SecretKeySpec(SECRET_KEY.getBytes(),"AES");
            // 5.选择解密
            instance.init(Cipher.DECRYPT_MODE,secretKeySpec);
            // 6.使用Base64将字符串转为字节数组。
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decode = decoder.decode(str);
            // 7.解密
            byte[] bytes = instance.doFinal(decode);
            // 8.将解密得到的字节数组在转为字符串
            Base64.Encoder encoder = Base64.getEncoder();
            // 9.返回解密后的字符串
            return encoder.encodeToString(bytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            logger.error("解密出现未知错误！错误信息：[{}]",e.getMessage());
        }

        return "decrypt_false";
    }

}
