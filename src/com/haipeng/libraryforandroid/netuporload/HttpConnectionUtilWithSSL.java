package com.haipeng.libraryforandroid.netuporload;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.haipeng.libraryforandroid.phoneinfo.Loger;

import android.text.TextUtils;

public class HttpConnectionUtilWithSSL {
	/** 网络连接超时值(单位ms) */
	public static final int TIMEOUT = 20000;

	/**
	 * 
	 * */
	protected String getJsonStrRequest(String strURL,
			final Map<String, String> headers, String requestMethod)
			throws TimeoutException {
	
		String urlstr = strURL.substring(0, strURL.indexOf("?"));
		String param = strURL.substring(strURL.indexOf("?") + 1);
		StringBuilder json = new StringBuilder();
		HttpsURLConnection conn = null;

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());  
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
            
			URL url = new URL(urlstr);
			BufferedReader br = null;

			conn = (HttpsURLConnection) url.openConnection();
			conn.setConnectTimeout(this.TIMEOUT);
			conn.setReadTimeout(this.TIMEOUT);
			conn.setDoOutput(true);
			conn.setRequestMethod(requestMethod);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			// add header
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> header : headers.entrySet()) {
					conn.setRequestProperty(header.getKey(), header.getValue());
				}
			}
			conn.connect();
			OutputStream outs = conn.getOutputStream();
			outs.write(param.getBytes());

			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			boolean isGzip = false;

			// 判别gzip方法一
			// isGzip = "gzip".equals(conn.getContentEncoding());

			// 判别gzip方法二
			bis.mark(2);// 取前两个字节
			byte[] header = new byte[2];
			int result = bis.read(header);
			bis.reset();// reset输入流到开始位置
			// isGzip = (result!=-1 && getShort(header)==0x8b1f);
			isGzip = (result != -1 && getShort(header) == 0x1f8b);

			// 如果是gzip的压缩流 进行解压缩处理
			if (isGzip) {
				is = new GZIPInputStream(bis);
			} else {
				is = bis;
			}
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String temp = null;
			while ((temp = br.readLine()) != null) {
				json = json.append(temp);
			}
			br.close();
			is.close();
			outs.close();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.disconnect();
		}

		String jsonStr = json.toString().replace("\\", "");

		if (!TextUtils.isEmpty(jsonStr)) {
			if (jsonStr.startsWith("\"")) {
				jsonStr = jsonStr.substring(1);
			}

			if (jsonStr.endsWith("\"")) {
				jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
			}
		}

		Loger.i("jsonStr:" + jsonStr);

		return jsonStr;
	}
	public String upImageTest(String strURL, final Map<String, String> headers,
			String requestMethod, String fileName, String filePath)
			throws TimeoutException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		// String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		// String urlstr = strURL.substring(0, strURL.indexOf("?"));
		StringBuilder json = new StringBuilder();
		HttpsURLConnection conn = null;

		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection
					.setDefaultHostnameVerifier(new MyHostnameVerifier());

			URL url = new URL(strURL);
			BufferedReader br = null;

			conn = (HttpsURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			// 允许输入
			conn.setDoInput(true);
			conn.setDoOutput(true);
			// 不允许使用缓存
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data"
					+ ";boundary=" + BOUNDARY);

			// add header
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> header : headers.entrySet()) {
					conn.setRequestProperty(header.getKey(), header.getValue());
				}
			}
			conn.connect();
			DataOutputStream outs = new DataOutputStream(conn.getOutputStream());
			String temp = null;
			// File file = new File(imagePath);

			StringBuffer sb = new StringBuffer();
			// 文件开头描述
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			/**
			 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
			 * filename是文件的名字，包含后缀名的 比如:abc.png
			 */

			sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
					+ fileName + "\"" + LINEND);
			sb.append("Content-Type: application/octet-stream; charset="
					+ CHARSET + LINEND);
			sb.append(LINEND);
			outs.write(sb.toString().getBytes());
			// 文件开头描述结束

			File file = new File(filePath);
			InputStream file_is = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = file_is.read(buffer)) != -1) {
				outs.write(buffer, 0, len);
				// Log.d("aa", "sb1-->" + len);
			}

			// 文件结束描述
			outs.write(LINEND.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outs.write(end_data);
			file_is.close();
			outs.flush();
			// 文件结束描述结束
			outs.close();

			InputStream is = conn.getInputStream();
			// BufferedInputStream bis = new BufferedInputStream(is);
			br = new BufferedReader(new InputStreamReader(is));
			while ((temp = br.readLine()) != null) {
				json.append(temp);
			}

			br.close();
			is.close();

		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} finally {

			if (conn != null)
				conn.disconnect();
		}

		String jsonStr = json.toString().replace("\\", "");

		if (!TextUtils.isEmpty(jsonStr)) {
			if (jsonStr.startsWith("\"")) {
				jsonStr = jsonStr.substring(1);
			}

			if (jsonStr.endsWith("\"")) {
				jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
			}
		}

		return jsonStr;
	}
	private int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}

	private class MyHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private class MyTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}
