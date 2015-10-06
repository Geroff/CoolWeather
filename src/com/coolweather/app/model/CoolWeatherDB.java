package com.coolweather.app.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.db.CoolWeatherOpenHelper;

public class CoolWeatherDB {
	/**
	 *  数据库名
	 */
	private static final String DB_NAME = "cool_weather";
	
	/**
	 *  数据库版本
	 */
	private static final int VERSION = 1;
	
	private SQLiteDatabase db ;
	
	private static CoolWeatherDB coolWeatherDB;
	
	/**
	 * 将构造方法私有化
	 * @param context
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = helper.getWritableDatabase();
	}
	/**
	 * 获取CoolWeatherDB实例
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB == null){
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	/**
	 * 将province 实例存储到province数据表中
	 * @param province
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvince_name());
			values.put("province_code", province.getProvince_code());
			db.insert("province", null, values);
		}
	}
	/**
	 * 从数据库中读取全国所有省份信息
	 * @return
	 */
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvince_code(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
	/**
	 * 将City实例存储到数据库中
	 * @param city
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCity_name());
			values.put("city_code", city.getCity_code());
			values.put("province_id", city.getProvince_id());
			db.insert("city", null, values);
		}
	}
	/**
	 * 从数据库中获取某省份的所有市
	 * @param provinceId
	 * @return
	 */
	public List<City> loadCity(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor  = db.query("city", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null,null, null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvince_id(cursor.getInt(cursor.getColumnIndex("province_id")));
				list.add(city);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
	
	/**
	 * 将County实例存储到数据库
	 * @param county
	 */
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCounty_name());
			values.put("county_code", county.getCounty_code());
			values.put("city_id", county.getCity_id());
			db.insert("county", null, values);
		}
	}
	/**
	 * 从数据库中读取将某个城市的所有县
	 * @param cityId
	 * @return
	 */
	public List<County> loadCounty(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("county", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCounty_name(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCounty_code(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCity_id(cursor.getInt(cursor.getColumnIndex("city_id")));
				list.add(county);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return list;
	}
	
}
