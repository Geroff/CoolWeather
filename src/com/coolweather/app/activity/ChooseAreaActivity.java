package com.coolweather.app.activity;

 
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.utils.HttpCallbackListener;
import com.coolweather.app.utils.HttpUtils;
import com.coolweather.app.utils.Utility;

public class ChooseAreaActivity extends Activity {
	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	private static final int LEVEL_COUNTY = 2;
	private int currentLevel = LEVEL_PROVINCE;
	
	private ProgressDialog progressDialog = null;
	private TextView title;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provincesList;
	private List<City> citiesList;
	private List<County> countiesList;
	
	private Province selectedProvince;
	private City selectedCity;
	private boolean isFromWeatherActivity = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean citySelected = prefs.getBoolean("city_selected", false);
		if(citySelected && !isFromWeatherActivity){
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return ;
		}
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.choose_area);
		
		title = (TextView) findViewById(R.id.title);
		listView = (ListView) findViewById(R.id.list);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				 if(currentLevel == LEVEL_PROVINCE){
					 selectedProvince = provincesList.get(position);
					 queryCities();
				 }else if(currentLevel == LEVEL_CITY){
					 selectedCity = citiesList.get(position);
					 queryCounties();
				 }else if(currentLevel == LEVEL_COUNTY){
					 Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					 intent.putExtra("county_code", countiesList.get(position).getCounty_code());
					 startActivity(intent);
					 finish();
				 }
				
			}
		});
		queryProvinces();
	}
	
	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	
	private void queryProvinces() {
		provincesList = coolWeatherDB.loadProvince();
		if(provincesList.size() > 0){
			dataList.clear();
			for(Province province: provincesList){
				dataList.add(province.getProvince_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	/**
	 * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCities() {
		citiesList = coolWeatherDB.loadCity(selectedProvince.getId());
		if(citiesList.size() > 0){
			dataList.clear();
			for(City city: citiesList){
				dataList.add(city.getCity_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText(selectedProvince.getProvince_name());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvince_code(),"city");
		}
	}
	/**
	 * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCounties() {
		countiesList = coolWeatherDB.loadCounty(selectedCity.getId());
		if(countiesList.size() > 0){
			dataList.clear();
			for(County county: countiesList){
				dataList.add(county.getCounty_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText(selectedCity.getCity_name());
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCity_code(),"county");
		}
	}



	private void queryFromServer(final String code, final String type) {
		 String address;
		 if(!TextUtils.isEmpty(code)){
			 address = "http://www.weather.com.cn/data/list3/city" + code +".xml";
		 }else{
			 address = "http://www.weather.com.cn/data/list3/city.xml";
		 }
		 showProgressDialog();
		 HttpUtils.sendRequestWithHttpURLConnection(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				 if("province".equals(type)){
					 result = Utility.handleProvincesResponse(coolWeatherDB, response);
				 }else if("city".equals(type)){
					 result = Utility.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				 }else if("county".equals(type)){
					 result = Utility.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
				 }
				if(result){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".endsWith(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				 runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
				
			}
		});
		
		
	}



	/**
	 * 显示对话框
	 */
	public void showProgressDialog(){
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * 关闭进度对话框
	 */
	public void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			 if(currentLevel == LEVEL_COUNTY){
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					queryProvinces();
				}else{
					if(isFromWeatherActivity){
						Intent intent = new Intent(this, WeatherActivity.class);
						startActivity(intent);
					}
					finish();
				}
			 return true;
		 }
		return super.onKeyDown(keyCode, event);
	}

}
