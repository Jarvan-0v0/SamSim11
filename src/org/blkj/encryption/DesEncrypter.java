package org.blkj.encryption;

/**
从JDK 1.8开始，就提供了java.util.Base64.Decoder和java.util.Base64.Encoder的JDK公共API，可代替sun.misc.BASE64Decoder和sun.misc.BASE64Encoder的JDK内部API。
  从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Encoder
  * 
 * The class is created with a key and can be used repeatedly to encrypt and decrypt strings using that key.

 * 
 * Here's an example that uses the class:
 *
 *   try {
 *       // Generate a temporary key. In practice, you would save this key.
 *       // See also e464 Encrypting with DES Using a Pass Phrase.
 *       SecretKey key = KeyGenerator.getInstance("DES").generateKey();
 *   
 *       // Create encrypter/decrypter class
 *      DesEncrypter encrypter = new DesEncrypter(key);
 *   
 *      // Encrypt
 *       String encrypted = encrypter.encrypt("Don't tell anybody!");
 *   
 *       // Decrypt
 *       String decrypted = encrypter.decrypt(encrypted);
 *   } catch (Exception e) {
 *  }
 */
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;


public class DesEncrypter {
    Cipher ecipher;
    Cipher dcipher;

    DesEncrypter(){}
    DesEncrypter(SecretKey key) {
        try {
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);

        } catch (javax.crypto.NoSuchPaddingException e) {
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch (java.security.InvalidKeyException e) {
        }
    }

    public String encrypt(String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            
            Encoder encoder = java.util.Base64.getEncoder();
            return encoder.encodeToString(enc);
            
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        } 
        return null;
    }

    public String decrypt(String str) {
        try {
            Decoder decoder = java.util.Base64.getDecoder();
            byte[] buffer = decoder.decode(str);
                // Decode using utf-8
            return new String(buffer, "UTF8");
        } catch (Exception e) {
        }
        return null;
    }
    
    public static void main(String a[]){
    	//Log.debug(new DesEncrypter().decrypt("R9idEtiO3Bo="));
    	
    }
}
