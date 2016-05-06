package com.goodweather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

public class Welcome extends Activity {

	private String ATG = "Welcome";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏系统状态栏
		setContentView(R.layout.activity_welcome);
		//开启线程，进行延时操作
		Runnable runnable = new Runnable() {

			@Override
			public void run() { // TODO Auto-generated method stub
				//根据判断是否第一次使用进入不同页面
				if (isFirstUse()) {
					Intent mIntent = new Intent(Welcome.this, GuideLauncherActivity.class);
					startActivity(mIntent);
					finish();
				} else {
					Intent mIntent = new Intent(Welcome.this, MainActivity.class);
					startActivity(mIntent);
					finish();
				}
			}
		};
		Handler mHandler = new Handler();
		mHandler.postDelayed(runnable, 3 * 1000);//延时3秒

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		/*
		 * if (receiver != null) { this.unregisterReceiver(receiver); }
		 */

	}
	//判断是否第一次使用
	private Boolean isFirstUse() {
		boolean is = false;
		SharedPreferences mPreferences = getSharedPreferences(MyApplication.getWeatherinfo(), Context.MODE_PRIVATE);
		if (mPreferences.getInt(MyApplication.getOne(), 0) != 1) {
			Editor mEditor = mPreferences.edit();
			mEditor.putInt(MyApplication.getOne(), 1);
			mEditor.commit();
			is = true;
		} else {
			is = false;
		}
		Log.d(ATG, "is --->" + is);
		return is;

	}
}
