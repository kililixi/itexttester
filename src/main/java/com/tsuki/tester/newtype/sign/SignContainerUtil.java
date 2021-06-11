package com.tsuki.tester.newtype.sign;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.Base64;

/**
 * @program: tester
 * @description:
 * @author: startsi
 * @create: 2021-06-07 14:23
 **/
public class SignContainerUtil {

    /**
     *
     * @return
     */
    public static byte[] P7DetachSigned(InputStream stream, PrivateKey privateKey, int estimatedSize) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        int nRead;
        byte[] temp = new byte[8192];
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while( (nRead = stream.read(temp)) != -1 ) {
            buffer.write(temp, 0, nRead);
        }

        buffer.flush();
        byte[] byteArray = buffer.toByteArray();

        Signature signature = Signature.getInstance(
                GMObjectIdentifiers.sm2sign_with_sm3.toString()
                , new BouncyCastleProvider());

        signature.initSign(privateKey);
        signature.update(byteArray);
        byte[] signatureValue = signature.sign();

        byte[] finalSignatture = new byte[estimatedSize];
        System.arraycopy(signatureValue, 0, finalSignatture, 0, signatureValue.length);

        System.out.println("签名， 签名长度为：" + signatureValue.length );
        return signatureValue;
    }

    public static boolean verifyP7DetachData(byte[] originData, byte[] signedData, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        Signature signature = Signature.getInstance(
                GMObjectIdentifiers.sm2sign_with_sm3.toString()
                , new BouncyCastleProvider());

        signature.initVerify(publicKey);

        // hash
        // MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // byte[] encodedhash = digest.digest(originData);
        // System.out.println(new String(Hex.encode(encodedhash)));
        System.out.println("验证签名， 签名长度为：" + signedData.length );
        signature.update(originData);
        return signature.verify(signedData);

    }
}
