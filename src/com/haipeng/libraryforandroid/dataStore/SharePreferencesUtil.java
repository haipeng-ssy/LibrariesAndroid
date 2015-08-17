package com.haipeng.libraryforandroid.dataStore;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePreferencesUtil {

	Context mContext;
	String  mSPName;
   //构造方法里创建SharePrefernce
	public SharePreferencesUtil(Context context,String SPName){
		mContext = context;
		mSPName  = SPName;
	}
	public SharedPreferences getSharedPreferences(){
		return mContext.getSharedPreferences(mSPName, Application.MODE_PRIVATE);
	}
	public SharedPreferences.Editor getEditor(){
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		return editor;
	}
	public void setString(String key,String value){
		getEditor().putString(key, value).commit();
	}
	public String getString(String key,String defaultValue)
	{
		return getSharedPreferences().getString(key, defaultValue);
	}
	
	public void setInt(String key,int value){
		getEditor().putInt(key, value).commit();
	}
	public int getInt(String key,int defaultValue){
		return getSharedPreferences().getInt(key, defaultValue);
	}
	
	public void setFloat(String key,float value)
	{
		getEditor().putFloat(key, value).commit();
	}
	public float getFloat(String key,float defaultValue){
		return getSharedPreferences().getFloat(key, defaultValue);
	}
	
	public void setBoolean(String key,boolean value){
		getEditor().putBoolean(key, value).commit();
	}
	public boolean getBoolean(String key,boolean defaultValue){
		return getSharedPreferences().getBoolean(key, defaultValue);
	}
	/**
	 * How to use
	 * Directly use
	 * */
	
}
