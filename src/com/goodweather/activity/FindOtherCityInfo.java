package com.goodweather.activity;


import java.util.List;

import com.goodweather.adapter.QueryCityAdapter;
import com.goodweather.db.CityDataHelper;
import com.goodweather.mod.City;
import com.goodweather.utils.LocationUtils;
import com.goodweather.utils.LocationUtils.LocationListener;
import com.goodweather.utils.NetUtil;
import com.goodweather.utils.ReadWeatherTXTInfo;
import com.goodweather.utils.SettingPreferenceUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class FindOtherCityInfo extends Activity implements OnClickListener,
TextWatcher, OnItemClickListener{

	private String TAG = "UpdateCityName";
	
	public static final String CITY_EXTRA_KEY = "city";
	protected ContentResolver mContentResolver;
	private CityDataHelper dataHelper;
	private SQLiteDatabase db;
	private ImageView mBackBtn;
	private TextView mLocationTV;
	private EditText mQueryCityET;
	private ListView mQueryCityListView;
	private QueryCityAdapter mSearchCityAdapter;
	private List<City> mCities;
	private Filter mFilter;
	private ImageButton mQueryCityExitBtn;
	protected LocationUtils mLocationUtils;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.city_query_layout);	
		initData();
		initView();
	}

	private void initView(){
		mBackBtn = (ImageView) findViewById(R.id.back_image);
		mLocationTV = (TextView) findViewById(R.id.location_text);
		mQueryCityET = (EditText) findViewById(R.id.queryCityText);
		mQueryCityExitBtn = (ImageButton) findViewById(R.id.queryCityExit);
		
		mBackBtn.setOnClickListener(this);
		mLocationTV.setOnClickListener(this);
		mQueryCityET.addTextChangedListener(this);
		mQueryCityExitBtn.setOnClickListener(this);
		
		mQueryCityListView = (ListView) findViewById(R.id.cityList);
		mQueryCityListView.setOnItemClickListener(this);
		mSearchCityAdapter = new QueryCityAdapter(FindOtherCityInfo.this,
				mCities);
		mQueryCityListView.setAdapter(mSearchCityAdapter);
		mQueryCityListView.setTextFilterEnabled(true);
		mFilter = mSearchCityAdapter.getFilter();
	
		String cityName = getCityname();
		if (TextUtils.isEmpty(cityName)) {
			startLocation(mCityNameStatus);
		} else {
			mLocationTV.setText(formatBigMessage(cityName));
		}
	}
	
	private void initData(){
		
//		dataHelper = CityDataHelper.getInstance(this);
//		db = dataHelper.openDataBase();
//		String sql="SELECT * FROM city ORDER BY id";
//        Cursor cursor = db.rawQuery(sql,null);
//		mContentResolver = getContentResolver();
//		Cursor cityCursor = mContentResolver.query(
//				CityDataProvider.CITY_CONTENT_URI, null, null, null, null);
//		mCities = CityDataHelper.getAllCities(cityCursor);
		dataHelper = CityDataHelper.getInstance(this);
		db = dataHelper.openDataBase();
		mCities = CityDataHelper.getAllCities(db);
	
	}
	
	private void saveCityname(String cityname){
		/*SharedPreferences mPreferences = getSharedPreferences(MyApplication.getWeatherinfo(), MODE_PRIVATE);
		Editor mEditor = mPreferences.edit();
		mEditor.putString(MyApplication.getCityname(), cityname);
		mEditor.commit();*/
		SettingPreferenceUtils.setPreferString(this, MyApplication.getCityname(), cityname);
		
		
		
	}
	
	public String getCityname() {
//		SharedPreferences mPreferences = getSharedPreferences(MyApplication.getWeatherinfo(), MODE_PRIVATE);
//		String cityname = mPreferences.getString(MyApplication.getCityname(), null);
		String cityname = SettingPreferenceUtils.getPreferString(this, MyApplication.getCityname(), null);
		
		return cityname;
	} 
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_image:
			finish();
			break;
		case R.id.location_text:
			startLocation(mCityNameStatus);
			break;
		case R.id.queryCityExit:
			mQueryCityET.setText("");
			break;
		default:
			break;
		}
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.cityList:
			City city = mSearchCityAdapter.getItem(position);
			doSavaEditCityname(city);
			break;

		default:
			break;
		}
	};
	
	LocationListener mCityNameStatus = new LocationListener() {
		
		@Override
		public void succeed(String city) {
			// TODO Auto-generated method stub
			Log.d(TAG, "city--->"+ city);
			mLocationTV.setText(formatBigMessage(city));
			saveCityname(city);
		}
		
		@Override
		public void failed() {
			// TODO Auto-generated method stub
			Toast.makeText(FindOtherCityInfo.this,R.string.getlocation_fail, Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void detecting() {
			// TODO Auto-generated method stub
			
		}
	};
	
	public void startLocation(LocationListener cityNameStatus) {
		if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE) {
			Toast.makeText(this, R.string.net_error, Toast.LENGTH_SHORT).show();
			return;
		}
		if (mLocationUtils == null)
			mLocationUtils = new LocationUtils(this, cityNameStatus);
		if (!mLocationUtils.isStarted()) {
			mLocationUtils.startLocation();// 开始定位
		}
	}
	
	// This is the message string used in bigText and bigPicture notifications.
		public CharSequence formatBigMessage(String city) {
			final TextAppearanceSpan notificationSubjectSpan = new TextAppearanceSpan(
					this, R.style.NotificationPrimaryText);

			// Change multiple newlines (with potential white space between), into a
			// single new line
			final String message = !TextUtils.isEmpty(city) ? city : "";
			String afterStr = "(点击重新定位)";
			SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(
					message);
			if (!TextUtils.isEmpty(afterStr)) {
				spannableStringBuilder.append(afterStr);
				spannableStringBuilder.setSpan(notificationSubjectSpan,
						message.length(), message.length() + afterStr.length(), 0);
			}
			return spannableStringBuilder;
		}
		
		private void doAfterTextChanged() {
			if (enoughToFilter()) {
				if (mFilter != null) {
					mFilter.filter(mQueryCityET.getText().toString().trim());
				}
			} else {
				if (mQueryCityListView.getVisibility() == View.VISIBLE) {
					mQueryCityListView.setVisibility(View.GONE);
				}
				if (mFilter != null) {
					mFilter.filter(null);
				}
			}

		}

		//这个方法是在Text改变之前被调用，它的意思就是说在原有的文本s中，从start开始的count个字符将会被一个新的长度为after的文本替换，注意这里是将被替换，还没有被替换。
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			doBeforeTextChanged();
		}
		//这个方法是在Text改变过程中触发调用的，它的意思就是说在原有的文本s中，从start开始的count个字符替换长度为before的旧文本，注意这里没有将要之类的字眼，也就是说一句执行了替换动作。
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			if (TextUtils.isEmpty(s)) {
				mQueryCityExitBtn.setVisibility(View.GONE);
			} else {
				mQueryCityExitBtn.setVisibility(View.VISIBLE);
			}
			doAfterTextChanged();
		}
		//这个方法就是在EditText内容已经改变之后调用
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
		
		private void doBeforeTextChanged() {
			if (mQueryCityListView.getVisibility() == View.GONE) {
				mQueryCityListView.setVisibility(View.VISIBLE);
			}
		}
		
		public boolean enoughToFilter() {
			return mQueryCityET.getText().length() > 0;
		}
		
		private void doSavaEditCityname(City city){
			String cityname = city.getName().toString();
			saveCityname(cityname);
			if (ReadWeatherTXTInfo.isDeleteFolder(MyApplication.getWeatherinfotxt())){
				finish();
			}
			
		}
}
