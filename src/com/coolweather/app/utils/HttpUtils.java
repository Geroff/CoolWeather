package com.coolweather.app.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {
	public static void sendRequestWithHttpURLConnection(final String urlAddress, final HttpCallbackListener listener){
		new Thread(new Runnable(){

			@Override
			public void run() {
				 HttpURLConnection connection = null;
				 try {
					URL url = new URL(urlAddress);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);

					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
					StringBuilder response = new StringBuilder();
					String line = "";
					while((line = reader.readLine()) != null){
						response.append(line);
					}
					
					if(listener != null){
						listener.onFinish(response.toString());
					}
					
				} catch (Exception e) {
					 if(listener != null){
						 listener.onError(e);
					 }
				} finally{
					if(connection != null){
						connection.disconnect();
					}
				}
				
			}
			
		}).start();
	}
}
