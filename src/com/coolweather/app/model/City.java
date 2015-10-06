package com.coolweather.app.model;

public class City {
	private int id;
	private String cityName;
	private String cityCode;
	private int provinceId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCity_name() {
		return cityName;
	}
	public void setCity_name(String city_name) {
		this.cityName = city_name;
	}
	public String getCity_code() {
		return cityCode;
	}
	public void setCity_code(String city_code) {
		this.cityCode = city_code;
	}
	public int getProvince_id() {
		return provinceId;
	}
	public void setProvince_id(int province_id) {
		this.provinceId = province_id;
	}
	
}
