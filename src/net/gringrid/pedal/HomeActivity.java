package net.gringrid.pedal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
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

public class HomeActivity extends Activity implements OnClickListener, LocationListener{

	final boolean DEBUG = false;

	/**
	 * 현재속도, 평균속도, 누적거리, 누적시간, 현재시간, 배터리 상태
	 */
	private Context context;
	private TextView id_tv_current_speed;
	private TextView id_tv_avg_speed;
	private TextView id_tv_current_altitude;
	private TextView id_tv_distance;
	private TextView id_tv_battery_status;
	private Chronometer id_cm;
	private LocationManager mLocationManager;
	private MockLocationProvider mock;
	private long mTravelTime;
	private long mLastLocationTime;
	private long mMoveTime;
	private Location mLastLocation;
	private float mAvgSpeed;
	private float mTotalDistance;
	private long mFirstBackButtonPressedTime;
	private final int EXIT_TIME_INTERVAL = 2000;
	
	private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra("level", 0);
			id_tv_battery_status.setText(String.valueOf(level) + "%");
		}
	};
	
	private BroadcastReceiver mMusicReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String artist = intent.getStringExtra("artist");
			String track = intent.getStringExtra("track");
			((TextView)findViewById(R.id.id_tv_music_artist)).setText(artist);
			((TextView)findViewById(R.id.id_tv_music_title)).setText(track);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_home);
		registEvent();

		this.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.android.music.playstatechanged");
		iF.addAction("com.android.music.playbackcomplete");
		iF.addAction("com.android.music.queuechanged");
		registerReceiver(mMusicReceiver, iF);
		initView();
		
	}
	
	@Override
	protected void onResume() {
		initLocation();
		if ( DEBUG ) startSimulation();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TRAVEL TIME, DISTANCE, AVG SPEED
		
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		reset();
		this.unregisterReceiver(mBatteryReceiver);
		this.unregisterReceiver(mMusicReceiver);
		super.onDestroy();
	}
	
	private void registEvent() {
		View view = findViewById(R.id.id_iv_play_stop);
		view.setOnClickListener(this);
		view = findViewById(R.id.id_iv_delete);
		view.setOnClickListener(this);
		view = findViewById(R.id.id_iv_setting);
		view.setOnClickListener(this);
		view = findViewById(R.id.id_iv_bike);
		view.setOnClickListener(this);
	}

	private void initView(){
		context = getApplicationContext();
		id_tv_current_speed = (TextView)findViewById(R.id.id_tv_current_speed);
		id_tv_avg_speed = (TextView)findViewById(R.id.id_tv_avg_speed);
		id_tv_current_altitude = (TextView)findViewById(R.id.id_tv_current_altitude);
		id_tv_distance = (TextView)findViewById(R.id.id_tv_distance);
		id_tv_battery_status = (TextView)findViewById(R.id.id_tv_battery_status);
		id_cm = (Chronometer)findViewById(R.id.id_cm);
		
		// Toggle 기본값
		findViewById(R.id.id_iv_play_stop).setTag(R.drawable.ic_play_circle_outline_white_48dp);
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float)scale;
		id_tv_battery_status.setText(String.format("%.0f", batteryPct));
	}
	
	private void initLocation(){
		mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		checkGPS(mLocationManager);
	}

	final Handler handler = new Handler();
    
	private void startSimulation(){
		mock = new MockLocationProvider(LocationManager.GPS_PROVIDER, this);
		final List<Location> gpxList = readTextGPXFile();	
		handler.postDelayed(new Runnable() {
		      @Override
		      public void run() {
		    	  handler.postDelayed(this, 1000);
		    	  executeSimilation(gpxList);
		      }
		    }, 1500);
	}
	
	int mIdx = 0;

	private void executeSimilation(List<Location> list){
	    mock.pushLocation(list.get(mIdx++));
	}
	
	private List<Location> readTextGPXFile(){
		List<Location> list = new ArrayList<Location>();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(getAssets().open("gps_test.gpx"));
			Element elementRoot = document.getDocumentElement();
		
			NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");
		
			for(int i = 0; i < nodelist_trkpt.getLength(); i++){
		
				Node node = nodelist_trkpt.item(i);
				NamedNodeMap attributes = node.getAttributes();
			
				String newLatitude = attributes.getNamedItem("lat").getTextContent();
				Double newLatitude_double = Double.parseDouble(newLatitude);
				
				String newLongitude = attributes.getNamedItem("lon").getTextContent();
				Double newLongitude_double = Double.parseDouble(newLongitude);
				
				String newLocationName = newLatitude + ":" + newLongitude;
				Location newLocation = new Location("Test");
				newLocation.setLatitude(newLatitude_double);
				newLocation.setLongitude(newLongitude_double);
				
				list.add(newLocation);
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return list;
	}

	private void checkGPS(LocationManager locationManager){
		ImageView id_iv_gps_enable = (ImageView)findViewById(R.id.id_iv_gps_enable);
		if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ){
			id_iv_gps_enable.setVisibility(View.GONE);
		}else{
			id_iv_gps_enable.setVisibility(View.VISIBLE);
			id_iv_gps_enable.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                startActivity(callGPSSettingIntent);
				}
			});
		}
	}
	
	public class SimulationTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			List<Location> gpxList = readTextGPXFile();	
			for ( Location location : gpxList ){
				try {
					Thread.sleep(1000);
					onLocationChanged(location);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
	

	private void playPedal(){
		View v = findViewById(R.id.id_iv_play_stop);
		v.setTag(R.drawable.ic_pause_circle_outline_white_48dp);
		((ImageView)v).setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
		id_cm.setBase(SystemClock.elapsedRealtime() + mTravelTime);
		id_cm.start();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
	}
	
	private void pausePedal(){
		View v = findViewById(R.id.id_iv_play_stop);
		v.setTag(R.drawable.ic_play_circle_outline_white_48dp);
		((ImageView)v).setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
		mTravelTime = id_cm.getBase() - SystemClock.elapsedRealtime();
		id_cm.stop();
		mLocationManager.removeUpdates(this);
	}

	private void resetPedal(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_title);
		builder.setMessage(R.string.alert_reset);
		builder.setPositiveButton(R.string.alert_confirm,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						reset();
					}
				});
		builder.setNegativeButton(R.string.alert_cancel,
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
		builder.show();
	}
	
	private void reset(){
		pausePedal();
		mTravelTime = 0;
		mLastLocationTime = 0;
		id_cm.setBase(SystemClock.elapsedRealtime());
		id_tv_current_speed.setText("00.0");
		id_tv_avg_speed.setText("00.0");
		id_tv_current_altitude.setText("000");
		id_tv_distance.setText("000");
	}
	
	@Override
	public void onBackPressed() {
	    if (mFirstBackButtonPressedTime + EXIT_TIME_INTERVAL > System.currentTimeMillis()){ 
	        super.onBackPressed(); 
	        return;
	    } else { 
	    	Toast.makeText(getBaseContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show(); 
	    }
	   	mFirstBackButtonPressedTime = System.currentTimeMillis();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_iv_play_stop:
			// TRAVEL TIME, DISTANCE, AVG SPEED
			// 현재 Play일경우 
			if ( (Integer)v.getTag() == R.drawable.ic_play_circle_outline_white_48dp ){
				playPedal();

			// 현재 Pause인경우
			}else if ( (Integer)v.getTag() == R.drawable.ic_pause_circle_outline_white_48dp){
				pausePedal();
			}
			break;

		case R.id.id_iv_delete:
			resetPedal();
			break;

		case R.id.id_iv_setting:
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
			
		case R.id.id_iv_bike:
			if ( findViewById(R.id.id_iv_play_stop).getVisibility() == View.INVISIBLE){
				findViewById(R.id.id_iv_play_stop).setVisibility(View.VISIBLE);
				findViewById(R.id.id_iv_delete).setVisibility(View.VISIBLE);
				findViewById(R.id.id_iv_setting).setVisibility(View.VISIBLE);
			}else{
				findViewById(R.id.id_iv_play_stop).setVisibility(View.INVISIBLE);
				findViewById(R.id.id_iv_delete).setVisibility(View.INVISIBLE);
				findViewById(R.id.id_iv_setting).setVisibility(View.INVISIBLE);
			}
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// 누적거리
		// 	마지막 Location 저장
		// 	저장된 Location과 변경된 location 거리계산
		// 	누적거리에 add
		// 평균속도
		//  속도가 2 이상인 경우만 계산
		//	마지막 Location과 변경된 Location 거리계산
		//  변경된 Location 시간 - 마지막 저장된 시간
		long locationTime = location.getTime();
		String currentStatus = null;
		
		// 최초실행인경우 필터링
		if ( mLastLocationTime != 0 ){
			float distanceFromLastLocation = location.distanceTo(mLastLocation);
			float speedFromLastLocation = distanceFromLastLocation / (locationTime - mLastLocationTime) * 3600;
		
			// 평균속도를 계산하기 위해 멈춰있는경우는 제외한다. 
			if ( speedFromLastLocation <= 3 ){
				currentStatus = "STOP";
			// 속도가 100km 가 넘는경우는 GPS가 튄것으로 판단하여 제외한다.
			}else if ( speedFromLastLocation > 150 ){
				currentStatus = "GPS ERROR";
			}else{
				currentStatus = "Riding";
				mMoveTime++;// += locationTime - mLastLocationTime;
				mTotalDistance += distanceFromLastLocation;
				mAvgSpeed = mTotalDistance / mMoveTime * 3600 / 1000;
				id_tv_distance.setText(String.format("%.1f", mTotalDistance));
				id_tv_avg_speed.setText(String.format("%.1f", mAvgSpeed));
			}
		
			((TextView)findViewById(R.id.id_tv_accuracy)).setText(String.valueOf(location.getAccuracy()));
			((TextView)findViewById(R.id.id_tv_move_time)).setText(String.valueOf(mMoveTime));
			((TextView)findViewById(R.id.id_tv_speed_from_last_location)).setText(String.valueOf(speedFromLastLocation));
			((TextView)findViewById(R.id.id_tv_location_speed)).setText(String.valueOf(location.getSpeed()*3.6f));
			((TextView)findViewById(R.id.id_tv_last_distance)).setText(String.format("%.1f", distanceFromLastLocation));
			((TextView)findViewById(R.id.id_tv_latitude)).setText(String.valueOf(location.getLatitude()));
			((TextView)findViewById(R.id.id_tv_longitude)).setText(String.valueOf(location.getLongitude()));
			((TextView)findViewById(R.id.id_tv_current_status)).setText(currentStatus);
		}
		
		mLastLocationTime = locationTime;
		mLastLocation = location;

		Log.d("jiho", "onLocationChanged speed : "+location.getSpeed()+", altitude : "+location.getAltitude()+", lati : "+location.getLatitude()+", long : "+location.getLongitude());
		id_tv_current_speed.setText(String.format("%.1f", location.getSpeed()*3.6f));
		id_tv_current_altitude.setText(String.valueOf(location.getAltitude()));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onProviderDisabled(String provider) { }
}
