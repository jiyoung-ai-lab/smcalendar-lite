package com.waveapp.smcalendarlite;

import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.common.VersionConstant;
import com.waveapp.smcalendarlite.util.ComUtil;
import com.waveapp.smcalendarlite.util.ViewUtil;

public class SMApp extends Application { 
	
	@Override     
	public void onCreate()     {         
		super.onCreate(); 

		//언어값/기념일 국가설정 설정
        //선택언어에 따른 변수정보 setting (string.xml에서 comconstant value setting)
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();

        //1)언어값:설정 불러오기(설정값 없으면 단말기내 언어설정값 )  --> 다음 activity 에서 안되네..
        String language = sharedPref.getString("lang_pref","");
        String phoneLocale = ComUtil.getPhoneLanguage(this);
         
        //preference 에 언어설정이 안되어있는 경우 단말기 locale 정보로 setting
        if(language == null ||( language != null && language.trim().equals(""))){
        	language = phoneLocale;
        	sharedEditor.putString("lang_pref", language.trim());
        	sharedEditor.commit();
        }
        
        
        //2)기념일 국가설정 불러오기(설정값 없으면 단말기내 설정값 )
        String nationalholiday = sharedPref.getString("holiday_pref","");
        String phoneCountry = ComUtil.getPhoneCountry(this);
         
        //preference 에 언어설정이 안되어있는 경우 단말기 locale 정보로 setting
        if(nationalholiday == null ||( nationalholiday != null && nationalholiday.trim().equals(""))){
        	sharedEditor = sharedPref.edit();
        	nationalholiday = phoneCountry;
        	sharedEditor.putString("holiday_pref", nationalholiday.trim());
        	sharedEditor.commit();
        }      
        ComConstant.setConstantStringForCountry(this, nationalholiday);   
        
        //3)언어값에 따라 기본언어 refresh
        Locale cfgLocale = this.getResources().getConfiguration().locale; 
        if ( cfgLocale != null && !cfgLocale.getLanguage().equals(language)) {
        	
        	Configuration config = getApplicationContext().getResources().getConfiguration();
        	Locale nLocale = new Locale(language);        	
        	Locale.setDefault(nLocale);
        	config.locale = nLocale;
        	getApplicationContext().getResources().updateConfiguration(config, getApplicationContext().getResources().getDisplayMetrics()); 
        	getApplicationContext().getResources().flushLayoutCache();
        }

    	//locale setting and value(전역변수)
        ComConstant.setConstantStringForLocale(this, language);        
  
        //4)알람기본시간가져오기
        String alamrtime = sharedPref.getString("alarmtime_pref","");
        if(alamrtime == null ||( alamrtime != null && alamrtime.trim().equals(""))){
        	sharedEditor = sharedPref.edit();
        	sharedEditor.putString("alarmtime_pref", ComConstant.ALARM_DEFAULTTIME);
        	sharedEditor.commit();
        } else {
        	ComConstant.ALARM_DEFAULTTIME = alamrtime;
        }
        
        //widget style setting(초기화)
        ViewUtil.setWidgetStyleFromPreference(this);
        
        //버전 setting(정식버전)
        VersionConstant.APPID = VersionConstant.APP_NORMAL;
     
	} 
  

} 
