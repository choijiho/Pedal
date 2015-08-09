package net.gringrid.pedal.activity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.gringrid.pedal.GPXMaker;
import net.gringrid.pedal.R;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.SharedData;
import net.gringrid.pedal.R.array;
import net.gringrid.pedal.R.drawable;
import net.gringrid.pedal.R.id;
import net.gringrid.pedal.R.layout;
import net.gringrid.pedal.R.raw;
import net.gringrid.pedal.R.string;
import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.RideDao;
import net.gringrid.pedal.db.vo.GpsLogVO;
import net.gringrid.pedal.db.vo.RideVO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StravaAPITestActivity extends Activity implements OnClickListener{

	HttpClient httpClient;
	HttpGet httpGet;
	HttpPost httpPost;
	StringBuffer stringBuffer = null;
	BufferedReader bufferedReader = null;
	
	String user_id = SharedData.getInstance(this).getGlobalDataString(Setting.SHARED_KEY_STRAVA_USER_ID);
	String access_token = SharedData.getInstance(this).getGlobalDataString(Setting.SHARED_KEY_STRAVA_ACCESS_TOKEN);
	
	TextView id_tv_response;
	private String mActivityId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_strava_api_test);
		initView();
		registEvent();
	}
	
	private void initView() {
		id_tv_response = (TextView)findViewById(R.id.id_tv_response);
	}

	private void registEvent() {
		View view = findViewById(R.id.id_bt_get);
		view.setOnClickListener(this);
		view = findViewById(R.id.id_bt_post);
		view.setOnClickListener(this);
		view = findViewById(R.id.id_bt_post_with_file);
		view.setOnClickListener(this);
		view = findViewById(R.id.id_bt_check_upload_status);
		view.setOnClickListener(this);
	}
	

	private void internalFileTest() {
		String fileName = "MyFile";
		String content = "hello world";
		 
		FileOutputStream outputStream = null;
		try {
		    outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
		    outputStream.write(content.getBytes());
		    outputStream.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	
		Log.d("jiho", "file path : "+getFilesDir().getPath());
		File file = new File(getFilesDir().getPath()+"/MyFile");
	}

	private String cURLGetTest() {
		try {
			Log.d("jiho", "user_id : "+user_id);
			Log.d("jiho", "access_token : "+access_token);
			URI uri = new URI("https://www.strava.com/api/v3/athletes/"+user_id);
			httpGet.setHeader("Authorization", "Bearer "+access_token);
			httpGet.setURI(uri);
			//TODO
			
			HttpResponse httpResponse = httpClient.execute(httpGet);
			InputStream inputStream = httpResponse.getEntity().getContent();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
			String readLine = bufferedReader.readLine();
			while(readLine != null){
				stringBuffer.append(readLine);
				stringBuffer.append("\n");
				readLine = bufferedReader.readLine();
			}
			
		} catch (Exception e) {
		} finally {
			if ( bufferedReader != null ){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d("jiho", "return stringBuffer.toString(); : "+stringBuffer.toString());
		
		return stringBuffer.toString();
		
	}
	
	private String checkUploadStatus() {
		String result = "";
		try {
			URI uri = new URI("https://www.strava.com/api/v3/uploads/"+mActivityId);
			httpGet.setHeader("Authorization", "Bearer "+access_token);
			httpGet.setURI(uri);
			
	        HttpResponse httpResponse = httpClient.execute(httpGet);  
	        HttpEntity resEntity = httpResponse.getEntity();  

	        if (resEntity != null) {    
	        	result = EntityUtils.toString(resEntity);
	        	Log.i("RESPONSE",result);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }	
		return result;
		
	}

	private String cURLPostTest(){
		String result = "";
		try {
			URI uri = new URI("https://www.strava.com/api/v3/activities");
			httpPost.setHeader("Authorization", "Bearer "+access_token);
			httpPost.setURI(uri);
			
			MultipartEntityBuilder meb = MultipartEntityBuilder.create(); 	
			meb.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);  	

			meb.addTextBody("name", "Test Ride From SEOMIN");
			meb.addTextBody("elapsed_time", "18373");
			meb.addTextBody("distance", "1557840");
			meb.addTextBody("start_date_local", "T2013-10-23T10:02:13Z");
			meb.addTextBody("type", "Ride");

			//HttpEntity를 빌드하고 HttpPost 객체에 삽입한다.
			HttpEntity entity = meb.build();  
			httpPost.setEntity(entity);  
			
	        HttpResponse responsePOST = httpClient.execute(httpPost);  
	        HttpEntity resEntity = responsePOST.getEntity();  
	        if (resEntity != null) {    
	        	result = EntityUtils.toString(resEntity);
	        	Log.i("RESPONSE",result);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }	
		return result;
	}

	private String cURLPostTesta() {
		
		try {
			URI uri = new URI("https://www.strava.com/api/v3/activities");
			httpPost.setHeader("Authorization", "Bearer "+access_token);
			httpPost.setURI(uri);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", "Test Ride From SEOMIN"));
			params.add(new BasicNameValuePair("elapsed_time", "18373"));
			params.add(new BasicNameValuePair("distance", "1557840"));
			params.add(new BasicNameValuePair("start_date_local", "2013-10-23T10:02:13Z"));
			params.add(new BasicNameValuePair("type", "Ride")); 
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			InputStream inputStream = httpResponse.getEntity().getContent();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
			String readLine = bufferedReader.readLine();
			while(readLine != null){
				stringBuffer.append(readLine);
				stringBuffer.append("\n");
				readLine = bufferedReader.readLine();
			}
			
		} catch (Exception e) {
		} finally {
			if ( bufferedReader != null ){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d("jiho", "return stringBuffer.toString(); : "+stringBuffer.toString());
		
		return stringBuffer.toString();
		
	}

	private File readAssetSampleFile(){
		File file = null;
		AssetManager assetManager = getAssets();
		ByteArrayOutputStream outputStream = null;
	    InputStream inputStream = null;
	    try {
	        inputStream = assetManager.open("gpxsample.gpx");
	        outputStream = new ByteArrayOutputStream();
	        byte buf[] = new byte[1024];
	        int len;
	        try {
	            while ((len = inputStream.read(buf)) != -1) {
	                outputStream.write(buf, 0, len);
	            }
//	            file = FileOutputStream(outputStream);
	            outputStream.close();
	            inputStream.close();
	        } catch (IOException e) {
	        }
	    } catch (IOException e) {
	    }
		return file;
	}
	private String cURLPostWithFileTest() {
		String result = "";
		try {
			URI uri = new URI("https://www.strava.com/api/v3/uploads");
			httpPost.setHeader("Authorization", "Bearer "+access_token);
			httpPost.setURI(uri);
			
			GPXMaker gpxMaker = new GPXMaker(this);
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
		} finally {
			if ( bufferedReader != null ){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d("jiho", "return stringBuffer.toString(); : "+result);
		
		return result;
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_bt_get:
			reset();
			id_tv_response.setText(cURLGetTest());
			break;

		case R.id.id_bt_post:
			reset();
			id_tv_response.setText(cURLPostTest());
			break;

		case R.id.id_bt_post_with_file:
			reset();
			id_tv_response.setText(cURLPostWithFileTest());
			break;

		case R.id.id_bt_check_upload_status:
			reset();
			id_tv_response.setText(checkUploadStatus());
			break;
			
		default:
			break;
		}
	}

	private void reset() {
		httpClient = new DefaultHttpClient();
		httpGet = new HttpGet();
		httpPost = new HttpPost();
		stringBuffer = new StringBuffer("");
		bufferedReader = null;
	}
}




