package com.waveapp.smcalendarlite.link;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.waveapp.smcalendarlite.info.GoogleCalendarInfo;

public class GoogleRetrieve {
	
//	private Context mCtx;
    private static final String mURLClientLogin = "https://www.google.com/accounts/ClientLogin";
    private static String mAuthTokenCal;

	ArrayList<GoogleCalendarInfo> mList = new ArrayList<GoogleCalendarInfo>();
	

    public ArrayList<GoogleCalendarInfo> getmList() {
		return mList;
	}
	public void setmList(ArrayList<GoogleCalendarInfo> mList) {
		this.mList = mList;
	}
	public boolean GoogleClientLogin( String userEmail , String userPasswd ) {
//        static final String mURLClientLogin = "https://www.google.com/accounts/ClientLogin";
//        static String mAuthTokenCal;

//        String userEmail = "your@google.com";
//        String userPasswd = "yourPassword";
        // Identifies your client application. Should take the form companyName-applicationName-versionID
        String userSource = "package-class-version";
        
        HttpsURLConnection uc;
		try {
			uc = (HttpsURLConnection) new URL(mURLClientLogin).openConnection();

	        uc.setDoOutput(true);
	        uc.setRequestMethod("POST");
	        uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        uc.setUseCaches(false);
	
	        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(uc.getOutputStream()));
	        bw.write(URLEncoder.encode("Email", "UTF-8") + "="
	                + URLEncoder.encode(userEmail , "UTF-8") + "&"
	                + URLEncoder.encode("Passwd", "UTF-8") + "="
	                + URLEncoder.encode(userPasswd , "UTF-8") + "&"
	                + URLEncoder.encode("source", "UTF-8") + "="
	                + URLEncoder.encode(userSource , "UTF-8") + "&"
	                + URLEncoder.encode("service", "UTF-8") + "="
	                + URLEncoder.encode("cl", "UTF-8")
	                );
	        bw.flush();
	        bw.close();
	
//	        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
	        // If the authentication request fails, you'll receive an HTTP 403 Forbidden status code.
	        if (uc.getResponseCode() == HttpsURLConnection.HTTP_FORBIDDEN) {
	            return false; 
	        }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		return false;
    	
    }
	public boolean GoogleCalendarRetrieve(){

        // 다음은 Retrieve events의 실제 내용입니다.
        // For mURLRetrieveCal, check this page - http://code.google.com/intl/ko/apis/calendar/data/2.0/reference.html#Visibility
        final String mURLRetrieveCal = "http://www.google.com/calendar/feeds/default/private/full";
        String url;
        

        try {
            HttpURLConnection uc = communicate(mURLRetrieveCal);
            if (uc != null) {
                // When you send that GET request, Calendar may return an HTTP 302 redirect
                if (uc.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    url = uc.getHeaderField("Location");                                // redirect 할 주소를 다시 가져옵니다.
                    uc = communicate(url);                                                    // 새 주소로 다시 보냅니다.
                    if (uc.getResponseCode() == HttpURLConnection.HTTP_OK) {   // 성공했다면,
                        getXMLDocument(uc.getInputStream());                     // xml 을 parsing합니다.

//                        
//                        GoogleCalendarSAXHandler.startDocument();
//                        GoogleCalendarSAXHandler.startElement(uri, lName, qName, atts);
//                        GoogleCalendarSAXHandler.endElement(uri, name, qName)
//                        GoogleCalendarSAXHandler.endDocument();
                                                
                    }
                    else {
                        return false; // failed
                    }
                }
                else if (uc.getResponseCode() == HttpURLConnection.HTTP_OK) {   // redirect할 필요가 없는 경우입니다.
                    getXMLDocument(uc.getInputStream());
                    //getXMLDocument(type, uc.getInputStream());
                }
                else {
                    return false; // failed
                }
            }
            else {
                return false; // failed
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false; // failed
        } catch (IOException e) {
            e.printStackTrace();
            return false; // failed
        }
        return true;
	}
	private void getXMLDocument(InputStream inputStream) {
        try {
        	// 이건 편의상 여기두었습니다만, 한번만 불러주면 되니까, 여기 말고 좀 더 상위에서 불러주면 좋겠습니다. 
            System.setProperty("org.xml.sax.driver","org.xmlpull.v1.sax2.Driver"); 
            XMLReader xr = XMLReaderFactory.createXMLReader();
            GoogleCalendarSAXHandler handlerCal = new GoogleCalendarSAXHandler(mList);
            xr.setContentHandler(handlerCal);
            xr.setErrorHandler(handlerCal);
            InputSource inputSource = new InputSource(inputStream);
            xr.parse(inputSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	class GoogleCalendarSAXHandler extends DefaultHandler {
	    static final String TAG_ENTRY = "entry";
	    static final String TAG_EID = "id";
	    static final String TAG_TITLE = "title";
	    static final String TAG_CONTENT = "content";
	    static final String TAG_WHERE = "gd:where";
	    static final String TAG_WHEN = "gd:when";
	    static final String ATT_VALUESTRING = "valueString";
	    static final String ATT_ENDTIME = "endTime";
	    static final String ATT_STARTTIME = "startTime";
	    
	    private int mMode = 0;
	    private GoogleCalendarInfo mEventData = new GoogleCalendarInfo();
	    private ArrayList<GoogleCalendarInfo> mEventDataList = null;
	    
	    public GoogleCalendarSAXHandler (ArrayList<GoogleCalendarInfo> list) {
	        super();
	        mEventDataList = list;
	    }
	    
	    @Override
	    public void startDocument () {
	        mEventDataList.clear();
	        // 문서를 읽기 시작합니다. 
	    }
	
	    @Override
	    public void endDocument () {
	        // 문서를 다 읽었습니다.
	    }
	
	    @Override
	    public void startElement (String uri, String lName, String qName, Attributes atts) {
	        if (TAG_ENTRY.equals(qName)) {
	        	mEventData = new GoogleCalendarInfo();
	            //mEventData.clear();
	            mMode = 1;  // mMode는 sax2 parser속성 상 parser tree를 만들지않기 때문에, 바로 읽으면서 필요한 정보의 위치를 확인하기 위해 사용하는 변수입니다.
	        }
	        else if(TAG_EID.equals(qName)) {
	            if (mMode > 0) mMode = 2;
	        }
	        else if(TAG_TITLE.equals(qName)) {
	            if (mMode > 0) mMode = 3;
	        }
	        else if(TAG_CONTENT.equals(qName)) {
	            if (mMode > 0) mMode = 4;
	        }
	        else if(TAG_WHERE.equals(qName)) {
	            if (mMode > 0) {
	                mMode = 5;
	                for (int i=0; i<atts.getLength(); ++i) {
	                    if (ATT_VALUESTRING.equals(atts.getQName(i))) {
	                        mEventData.setWhere(atts.getValue(i)); // where
	                    }
	                }
	            }
	        }
	        else if(TAG_WHEN.equals(qName)) {
	            if (mMode > 0) {
	                mMode = 6;
	                for (int i=0; i<atts.getLength(); ++i) {
	                    if (ATT_ENDTIME.equals(atts.getQName(i))) {
	                        mEventData.setEndTime(atts.getValue(i)); // endtime
	                    }
	                    else if (ATT_STARTTIME.equals(atts.getQName(i))) {
	                        mEventData.setStartTime(atts.getValue(i)); // starttime
	                    }
	                }
	            }
	        }
	    }
	
	    @Override
	    public void endElement (String uri, String name, String qName) {
	        if (TAG_ENTRY.equals(name)) {
	            mEventDataList.add(mEventData); // event들을 리스트에 넣습니다.
	            mMode = 0;
	        }
	        if (mMode > 1) mMode = 1;
	    }
	
	    @Override
	    public void characters (char ch[], int start, int length) {
	        switch (mMode) {
	        case 2:
	            mEventData.setEID(new String(ch, start, length)); // eid
	            mMode = 1;
	            break;
	        case 3:
	            mEventData.setTitle(new String(ch, start, length)); // title
	            mMode = 1;
	            break;
	        case 4:
	            mEventData.setContent(new String(ch, start, length)); // content
	            mMode = 1;
	            break;
	        }
	   
		}
	}
    // 아래 communicate()함수는 retrieve/create할때 공통적으로 쓰이는 코드입니다.
    HttpURLConnection communicate(String url) throws MalformedURLException, IOException {
        HttpURLConnection uc = (HttpURLConnection) new URL(url).openConnection();
        uc.setUseCaches(false);
        uc.setRequestMethod("GET");
        uc.setRequestProperty("Content-Type", "application/atom+xml");
        // http://code.google.com/intl/ko/apis/calendar/data/2.0/developers_guide_protocol.html#Versioning
        uc.setRequestProperty("GData-Version", "2");
        uc.setRequestProperty("Authorization", "GoogleLogin auth=" + mAuthTokenCal);
        return uc;
    }
	
}
