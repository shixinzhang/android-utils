/*
 * Copyright (c) 2017. shixinzhang (shixinzhang2016@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.shixinzhang.utils.encrypt;

import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import top.shixinzhang.utils.encrypt.Base64Utils;

/**
 * The creator is Leone && E-mail: butleone@163.com
 *
 * @author Leone
 * @date 15/11/9
 * @description Edit it! Change it! Beat it! Whatever, just do it!
 */
public class RSAUtils {

	public static final String KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxUdCAlwoSPd0" +
			"fmbZAs2DveEtejVfRITWvLt9IYC4p8gmhThCbKv3L6ykgT2YwwWsiXs0YwGJF8WcTqbhagwTfGDXQlcg" +
			"TU0Cu9N1gAvymyfihXIM4ELfvvmqtC3yXOGxA0GWSlykWU01AoOZUBlloR5NBrN1DfzI038ODMcb793in" +
			"L8h4d4fuHjt6NY3A1KsOXZRvmpTcLJQxx4p99RreSnSQJ1IYf36dhV2hptisZH/Q4+cBBE5G/1sRrp" +
			"P9svQhwPxNzJnc06gd+OrPY3nTjtmEzLdBSiIh0AVprEOu57rkmgXdWgr1e//wCrrqBCbE4jJr" +
			"BDCULRBG3UaixVgzQIDAQAB";

	/**
	 * 加密算法RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 签名算法
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	/**
	 * 获取公钥的key
	 */
	private static final String PUBLIC_KEY = "RSAPublicKey";

	/**
	 * 获取私钥的key
	 */
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	/**
	 * key大小
	 */
	private static final int KEY_SIZE = 2048;

	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = KEY_SIZE / 8;

	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = MAX_DECRYPT_BLOCK - 11;

	/**
	 * <p>
	 * 生成密钥对(公钥和私钥)
	 * </p>
	 *
	 * @return Map
	 * @throws Exception
	 */
	@NonNull
	public static Map<String, Object> genKeyPair() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(2048);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	/**
	 * <p>
	 * 用私钥对信息生成数字签名
	 * </p>
	 *
	 * @param data
	 *            已加密数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 *
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = Base64Utils.decodeByte(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateK);
		signature.update(data);
		return Base64Utils.encodeByte(signature.sign());
	}

	/**
	 * <p>
	 * 校验数字签名
	 * </p>
	 *
	 * @param data
	 *            已加密数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @param sign
	 *            数字签名
	 *
	 * @return
	 * @throws Exception
	 *
	 */
	public static boolean verify(byte[] data, String publicKey, String sign)
			throws Exception {
		byte[] keyBytes = Base64Utils.decodeByte(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data);
		return signature.verify(Base64Utils.decodeByte(sign));
	}

	/**
	 * <P>
	 * 私钥解密
	 * </p>
	 *
	 * @param encryptedData
	 *            已加密数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 */
	public static byte[] decryptByPrivateKey(@NonNull byte[] encryptedData, String privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		byte[] keyBytes = Base64Utils.decodeByte(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privateK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * <p>
	 * 公钥解密
	 * </p>
	 *
	 * @param encryptedData
	 *            已加密数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(@NonNull byte[] encryptedData, String publicKey)
			throws Exception {
		byte[] keyBytes = Base64Utils.decodeByte(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, publicK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * <p>
	 * 公钥加密
	 * </p>
	 *
	 * @param data
	 *            源数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(@NonNull byte[] data, String publicKey)
			throws Exception {
		byte[] keyBytes = Base64Utils.decodeByte(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/**
	 * <p>
	 * 私钥加密
	 * </p>
	 *
	 * @param data
	 *            源数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 */
	public static byte[] encryptByPrivateKey(@NonNull byte[] data, String privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		byte[] keyBytes = Base64Utils.decodeByte(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, privateK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/**
	 * <p>
	 * 获取私钥
	 * </p>
	 *
	 * @param keyMap
	 *            密钥对
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(@NonNull Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return Base64Utils.encodeByte(key.getEncoded());
	}

	/**
	 * <p>
	 * 获取公钥
	 * </p>
	 *
	 * @param keyMap
	 *            密钥对
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(@NonNull Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return Base64Utils.encodeByte(key.getEncoded());
	}

}
