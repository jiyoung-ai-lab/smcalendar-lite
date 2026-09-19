package com.waveapp.smcalendarlite.link;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.waveapp.smcalendarlite.R;
import com.waveapp.smcalendarlite.SMActivity;
import com.waveapp.smcalendarlite.common.ComConstant;
import com.waveapp.smcalendarlite.common.VersionConstant;
import com.waveapp.smcalendarlite.util.RecycleUtil;

public class BannerLink  extends SMActivity{

	LinearLayout  lin_banner ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
 
	/*
	 * Addmob 광고 
	 */
	public void callBannerLink( final Context ctx ) { 
		
		lin_banner = (LinearLayout)((Activity) ctx).findViewById( R.id.lin_banner ) ;
		
		//lite 버전만 (정식:01 lite:02)
		if ( VersionConstant.APPID.equals(VersionConstant.APP_NORMAL)) {

			lin_banner.removeAllViews();
			
		} else {
			lin_banner.setVisibility(View.VISIBLE);
			com.cauly.android.ad.AdView  	
					caulyview = (com.cauly.android.ad.AdView)((Activity) ctx).findViewById( R.id.caulyView ) ;
			com.google.ads.AdView  			
					addmobview = (com.google.ads.AdView)((Activity) ctx).findViewById( R.id.addmobView ) ;
			
			//한국은 카울리 나머지는 애드몹
			if ( ComConstant.LOCALE_KO.equals(ComConstant.LOCALE)) {
				
				caulyview.setVisibility(View.VISIBLE);
				addmobview.setVisibility(View.GONE);
				addmobview = null;
				
				if ( caulyview != null ) {	
					
					caulyview.setAdListener( new com.cauly.android.ad.AdListener() {
						@Override
						public void onFailedToReceiveAd(boolean arg0) {
							// TODO Auto-generated method stub						
						}
						@Override
						public void onReceiveAd() {
							// TODO Auto-generated method stub						
						}
					});
				}
				
					
			} else {
				//Add Mob Link
				caulyview.setVisibility(View.GONE);
				caulyview.destroyDrawingCache();
				caulyview = null;
				addmobview.setVisibility(View.VISIBLE);
				
				
				
				if ( addmobview != null ) {	
					
					AdRequest re = new AdRequest(); 
					
//					re.addTestDevice(AdRequest.TEST_EMULATOR); 
					re.addTestDevice("304D1913DF39594E");    // My T-Mobile G1 test phone
//					re.setTesting(true);
//					request.setGender(AdRequest.Gender.FEMALE);
//					re.setLocation(location); 				
					addmobview.loadAd(re);

					
				}				
			}			
		}
	}
    @Override
    protected void onDestroy() { 
    	super.onDestroy();
        
        RecycleUtil.recursiveRecycle(lin_banner);
        
    }	
}