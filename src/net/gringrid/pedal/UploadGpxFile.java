package net.gringrid.pedal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONObject;

public class UploadGpxFile {

	private Context mContext;
	private final String STRAVA_UPLOAD_URL = "https://www.strava.com/api/v3/uploads/";
	private final String STRAVA_CREATE_ACTIVITY_URL = "https://www.strava.com/api/v3/activities/";
	private GPXMaker mGpxMaker;
	private String mActivityId;

	public UploadGpxFile(Context context) {
		mContext = context;
	}
	
	public void excute(int rideId){
		mGpxMaker = new GPXMaker(mContext);
		if ( mGpxMaker.createGPXFile(rideId) ){
			createActivity();
		}
	}
	
	private void createActivity(){
		String access_token = SharedData.getInstance(mContext).getGlobalDataString(Setting.SHARED_KEY_STRAVA_ACCESS_TOKEN);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(STRAVA_CREATE_ACTIVITY_URL);
		
		httpPost.setHeader("Authorization", "Bearer "+access_token);	
		String result = "";
		try {
			URI uri = new URI("https://www.strava.com/api/v3/uploads");
			httpPost.setHeader("Authorization", "Bearer "+access_token);
			httpPost.setURI(uri);
			
			GPXMaker gpxMaker = new GPXMaker(mContext);
			File file = gpxMaker.readGPXFile();
			MultipartEntityBuilder meb = MultipartEntityBuilder.create(); 	
			meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);  	
			
			meb.addTextBody("activity_type", "ride");
			meb.addTextBody("data_type", "gpx");
			meb.addBinaryBody("file", file, ContentType.MULTIPART_FORM_DATA, GPXMaker.FILENAME);
//			meb.addBinaryBody("file", getAssets().open("gpxsample.gpx"), ContentType.MULTIPART_FORM_DATA, "gpxsample.gpx");
		
			HttpEntity entity = meb.build();  
			httpPost.setEntity(entity); 
		
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity resEntity = httpResponse.getEntity();  

			if (resEntity != null) {    
				result = EntityUtils.toString(resEntity);
	        }	
			JSONObject jsonArray = new JSONObject(result);
			mActivityId = jsonArray.getString("id");
			Log.d("jiho", "mActivityId = "+mActivityId);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		Log.d("jiho", "return stringBuffer.toString(); : "+result);
	}
	
	private String uploadStrava() {
		StringBuffer stringBuffer = new StringBuffer("");
		BufferedReader bufferedReader = null;
		try {
			String user_id = SharedData.getInstance(mContext).getGlobalDataString(Setting.SHARED_KEY_STRAVA_USER_ID);
			String access_token = SharedData.getInstance(mContext).getGlobalDataString(Setting.SHARED_KEY_STRAVA_ACCESS_TOKEN);
			File file = mGpxMaker.readGPXFile();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(STRAVA_UPLOAD_URL+user_id);
			
			httpPost.setHeader("Authorization", "Bearer "+access_token);
			InputStreamEntity reqEntity = new InputStreamEntity(
		            new FileInputStream(file), -1);
			reqEntity.setContentType("binary/octet-stream");
		    reqEntity.setChunked(true); // Send in multiple parts if needed
		    httpPost.setEntity(reqEntity);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("activity_type", "ride"));
			params.add(new BasicNameValuePair("data_type", "gpx"));
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			httpPost.setEntity(ent);
			
			HttpResponse response = httpClient.execute(httpPost);  
		    HttpEntity resEntity = response.getEntity();  
		    if (resEntity != null) {    
		    	Log.d("jiho", EntityUtils.toString(resEntity));
		    }
//			FileBody fileBody = new FileBody(file);
//			
//			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
//			entity.addPart("file", fileBody);
//			
//			httpPost.setHeader("Authorization", "Bearer "+access_token);
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("activity_type", "ride"));
//            params.add(new BasicNameValuePair("data_type", "gpx"));
//            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
//            httpPost.setEntity(ent);
//            
//			httpPost.setEntity(entity);
//			HttpResponse response = httpClient.execute(httpPost);  
//		    HttpEntity resEntity = response.getEntity();  
//		    if (resEntity != null) {    
//		    	Log.d("jiho", EntityUtils.toString(resEntity));
//		    }
//		     
//		     
		     
			Log.d("jiho", "user_id : "+user_id);
			Log.d("jiho", "access_token : "+access_token);
			
		} catch (Exception e) {
		} finally {
			if ( bufferedReader != null ){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO: handle exception
				}
			}
		}
		Log.d("jiho", "return stringBuffer.toString(); : "+stringBuffer.toString());
		
		return stringBuffer.toString();
		
	}
}
