package com.coolweather.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {
	
	/**
	 * 解析和处理服务器返回的省级数据
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0){
				for(String p: allProvinces){
					String []array = p.split("\\|");
					Province province = new Province();
					province.setProvince_code(array[0]);
					province.setProvince_name(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				 
			}
			return true;
		}
		return false;	
	}
	
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String []allCities = response.split(",");
			if(allCities != null && allCities.length > 0){
				for(String c: allCities){
					String []array = c.split("\\|");
					City city = new City();
					city.setCity_code(array[0]);
					city.setCity_name(array[1]);
					city.setProvince_id(provinceId);
					coolWeatherDB.saveCity(city);
				}
			}
			return true;
		}
		
		return false;
	}
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String []allCounties = response.split(",");
			if(allCounties != null && allCounties.length > 0){
				for(String c: allCounties){
					String []array = c.split("\\|");
					County county = new County();
					county.setCounty_code(array[0]);
					county.setCounty_name(array[1]);
					county.setCity_id(cityId);
					 
					coolWeatherDB.saveCounty(county);
				}
			}
			return true;
		}
		
		return false;
	}
	
	
	public  static void handleWeatherResponse(Context context, String response){
		try {
			JSONObject jsonObect = new JSONObject(response);
			JSONObject weatherInfo = jsonObect.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
		 SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		 edit.putString("city_name", cityName);
		 edit.putString("weather_code", weatherCode);
		 edit.putString("temp1", temp1);
		 edit.putString("temp2", temp2);
		 edit.putString("weather_desp", weatherDesp);
		 edit.putString("publish_time", publishTime);
		 edit.putString("current_date", sdf.format(new Date()));
		edit.commit();
	}
}
