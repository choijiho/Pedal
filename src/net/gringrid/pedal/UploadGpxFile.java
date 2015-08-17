package net.gringrid.pedal;

import java.io.File;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

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
	
	public void createActivity(){
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
}
