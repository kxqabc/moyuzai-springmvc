package com.moyuzai.servlet.http;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpURLConnPost {
	private Log log = LogFactory.getLog(HttpURLConnPost.class);
	private Gson gson;
	private YunResponse yunResponse;
	public int doPost(String strUrl, String apikey,String mobile,String text){
		gson = new Gson();
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		InputStream is = null;
		try {
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			
			dos = new DataOutputStream(conn.getOutputStream());

			log.info("打印请求内容："+apikey+" "+mobile+" "+text);

			dos.writeBytes("apikey="+apikey);
			dos.writeBytes("&mobile="+mobile);
			dos.writeBytes("&text="+ URLEncoder.encode(text,"utf-8"));
			dos.flush();

			log.info("responseCode="+conn.getResponseCode());
			log.info("responseMessage="+conn.getResponseMessage());
			if(conn.getResponseMessage().equals("OK")){
				is = conn.getInputStream();

				String response = "";
				int len;
				byte[] tmp = new byte[1024];
				while((len=is.read(tmp))!=-1){
					response += new String(tmp,0,len);
				}
				is.close();
				dos.close();
				log.info("response="+response);
				yunResponse = gson.fromJson(response, YunResponse.class);
				log.info("resCode="+yunResponse.getCode());
			}else {
				dos.close();
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return yunResponse.getCode();
	}
	
}
