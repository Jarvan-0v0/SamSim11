package org.blkj.encryption;


import org.apache.commons.codec.binary.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.logicalcobwebs.proxool.util.Decryptool;

import blkjweb.utils.Console;
 
public class DBDecrypt implements Decryptool {  
  
	private static final String ALGO = "AES";
	private static final byte[] keyValue = 
			new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't',	'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };


	public String encrypt(String Data) {
		String encryptedValue = "";
		try {
			Key key = generateKey();
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encVal = c.doFinal(Data.getBytes());
			// String encryptedValue = new BASE64Encoder().encode(encVal);
			byte[] b = Base64.encodeBase64(encVal, true);
			encryptedValue = new String (b);
		} catch (Exception e) {
			Console.showMessage(DBDecrypt.class.getSimpleName(),e.getMessage(), e);
		}

		return encryptedValue;
	}

	public String decrypt(String encryptedData){
		String decryptedValue = "";
		try {
			Key key = generateKey();
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.DECRYPT_MODE, key);
			//byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
			byte[] decordedValue = Base64.decodeBase64(encryptedData.getBytes());
			byte[] decValue = c.doFinal(decordedValue);

			decryptedValue = new String(decValue);
		} catch (Exception e) {
			Console.showMessage(DBDecrypt.class.getSimpleName(),e.getMessage(), e);
		}
		return decryptedValue;
	}
	
	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, ALGO);
		return key;
	}
	
	public static void main(String[] args) throws Exception {

		/*String password = "123456";
		String passwordEnc = new DBDecrypt().encrypt(password);
		String passwordDec = new DBDecrypt().decrypt(passwordEnc);

		Log.debug("Plain Text : " + password);
		Log.debug("Encrypted Text : " + passwordEnc);
		Log.debug("Decrypted Text : " + passwordDec);
		*/
	}
      
}  

/*未加密：123456
加密后：oJfTtchpM9WC/4Oqpu7FZQ==

未加密：root  
加密后：Wz8gvD7s9Skl8un0iNCmZg==
*/