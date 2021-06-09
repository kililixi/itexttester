package com.tsuki.tester.newtype.sign;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-07 14:23
 **/
public class SignUtil {

    /**
     *
     * @return
     */
    public static byte[] P7DetachSigned(InputStream stream, PrivateKey privateKey) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        int nRead;
        byte[] temp = new byte[8192];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while( (nRead = stream.read(temp)) != -1 ) {
            buffer.write(temp, 0, nRead);
        }

        buffer.flush();
        byte[] byteArray = buffer.toByteArray();

        for(int i = byteArray.length - 30; i<byteArray.length; i++) {
            System.out.print(byteArray[i] + "|");
        }

        // 先hash再签名
        // TODO sha-256每次的hash值都不一样
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] encodedhash = digest.digest(byteArray);
//        System.out.println(new String(Hex.encode(encodedhash)));

        Signature signature = Signature.getInstance(
                GMObjectIdentifiers.sm2sign_with_sm3.toString()
                , new BouncyCastleProvider());
        // 签名
        signature.initSign(privateKey);
        // 写入签名原文到算法中
        signature.update(byteArray);
        // 计算签名值
        byte[] signatureValue = signature.sign();
        System.out.println("签名长度： " + signatureValue.length + "---- 原文长度： " + byteArray.length);
        System.out.println(new String(Base64.getEncoder().encode(signatureValue)));
        return signatureValue;
    }

    public static boolean verifyP7DetachData(byte[] originData, byte[] signedData, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        for(int i = originData.length - 30; i<originData.length; i++) {
            System.out.print(originData[i] + "|");
        }
        System.out.println("");

        Signature signature = Signature.getInstance(
                GMObjectIdentifiers.sm2sign_with_sm3.toString()
                , new BouncyCastleProvider());
        byte[] b64 = Base64.getEncoder().encode(signedData);
        System.out.println("再次获取签名的长度： " + signedData.length + "---- 原文长度： " + originData.length);
        System.out.println(new String(b64));
        signature.initVerify(publicKey);
        // 写入待验签的签名原文到算法中

        // 先hash再签名
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] encodedhash = digest.digest(originData);
//        System.out.println(new String(Hex.encode(encodedhash)));

        signature.update(originData);

        return signature.verify(signedData);

    }
}
