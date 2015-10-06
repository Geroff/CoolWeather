package com.coolweather.app.model;

public class County {
	private int id;
	private String countyName;
	private String countyCode;
	private int cityId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCounty_name() {
		return countyName;
	}
	public void setCounty_name(String county_name) {
		this.countyName = county_name;
	}
	public String getCounty_code() {
		return countyCode;
	}
	public void setCounty_code(String county_code) {
		this.countyCode = county_code;
	}
	public int getCity_id() {
		return cityId;
	}
	public void setCity_id(int city_id) {
		this.cityId = city_id;
	}
	
}
