package com.haipeng.libraryforandroid.safe;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Base64Util {
	private static byte[] iv = {1,2,3,4,5,6,7,8};
	/**
	  *  加密内容 
	  * @param encryptString 需要加密的字符串
	  * @param encryptKey    加密密码
	  * @return
	  */
	 public static String encryptDES(String encryptString, String encryptKey) {
	  //IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
	  try{
	   IvParameterSpec zeroIv = new IvParameterSpec(iv);  
	         SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");  
	         Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");  
	         cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);  
	         String tmp = Base64.encode(encryptString.getBytes("UTF-8"));  
	         byte[] encryptedData = cipher.doFinal(tmp.getBytes("UTF-8"));  
	        /* Base64 base64encoder = new Base64(); 
	         String encode=base64encoder.encode(encryptedData); */
	         return Base64.encode(encryptedData); 
	   
	  } catch (Exception e) {
	   e.printStackTrace();
	  } 
	  return "";
	  
	 }
}
