package com.waveapp.smcalendarlite.link;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

/**
 * Copyright 2011 Kakao Corp. All rights reserved. 
 * http://developer.apple.com/library/ios/#samplecode/DocInteraction/Introduction/Intro.html 
 * @author kakaolink@kakao.com
 * @version 1.0
 *
 */
public class KakaoLink {
	static final Charset kakaoLinkCharset = Charset.forName("UTF-8");
	static final String kakaoLinkEncoding = kakaoLinkCharset.name();
	
	private Context context;
	private Intent intent;
	private String appId;
	private String version;
	//private String url;
	private String msg;
	private String encoding;
	
	private Uri data;

	/**
	 * 
	 * @param url		urlencoded url to send
	 * @param appId 	android market package name
	 * @param appVersion 	kakaolink protocol version
	 * @param msg		message to send
	 * @param encoding 	message characterset	
	 * @throws UnsupportedEncodingException 
	 */
	public KakaoLink(Context context, String url, String appId, String appVersion, String msg, String encoding) throws UnsupportedEncodingException {
		this.context = context;
		this.appId = appId;
		this.version = appVersion;
		//this.url = url;
		this.msg = msg;
		this.encoding = encoding;
		
		data = createLinkData();
		//intent = new Intent(Intent.ACTION_SEND, data);
		intent = new Intent(Intent.ACTION_SEND, data);
	}
	
	private Uri createLinkData() throws UnsupportedEncodingException {
//		if (isEmptyString(appId) || isEmptyString(version) || isEmptyString(url) || isEmptyString(encoding)) {
//			throw new IllegalArgumentException();
//		}
		if (isEmptyString(appId) || isEmptyString(version) || isEmptyString(encoding)) {
			throw new IllegalArgumentException();
		}		
		Charset charset = Charset.forName(encoding);
		
		if (!kakaoLinkCharset.equals(charset)) {
			if (!isEmptyString(msg)) {
				msg = new String(msg.getBytes(charset.name()), kakaoLinkEncoding);
			}
		}
		
		StringBuilder sb = new StringBuilder("kakaolink://sendurl?");
		sb.append("appid=").append(URLEncoder.encode(appId, kakaoLinkEncoding))
			.append("&appver=").append(URLEncoder.encode(version, kakaoLinkEncoding))
			//.append("&url=").append(URLEncoder.encode(url, kakaoLinkEncoding));
			.append("&url=").append(URLEncoder.encode("", kakaoLinkEncoding));
		if (!isEmptyString(msg)) {
			sb.append("&msg=").append(URLEncoder.encode(msg, kakaoLinkEncoding));
		}
		
		return Uri.parse(sb.toString());
	}
	
	public boolean isAvailable() {
		return isAvailableIntent(context, intent);
	}
	
	public Intent getIntent() {
		return intent;
	}
	
	public Uri getData() {
		return data;
	}
	
	private static boolean isAvailableIntent(Context context, Intent intent) {
		List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,   
	            PackageManager.MATCH_DEFAULT_ONLY);
		if (list == null) return false;
		return list.size() > 0;
	}
	
	private static boolean isEmptyString(String str) {
		return (str == null || str.trim().length() == 0);
	}
}
