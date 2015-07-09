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
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity implements OnClickListener, LocationListener{

	final boolean DEBUG = false;

	/**
	 * 현재속도, 평균속도, 누적거리, 누적시간, 현재시간, 배터리 상태
	 */
	Context context;
	TextView id_tv_current_speed;
	TextView id_tv_avg_speed;
	TextView id_tv_latitude;
	TextView id_tv_longitude;
	TextView id_tv_current_altitude;
	TextView id_tv_distance;
	TextView id_tv_battery_status;
	ImageView id_iv_bike;
	Chronometer id_cm;
	DigitalClock id_dc;
	LocationManager mLocationManager;
	MockLocationProvider mock;
	private long mTravelTime;
	private long mStartTime;
	private long mLastLocationTime;
	private Location mLastLocation;
	private float mAvgSpeed;
	private float mTotalDistance;
	
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
		
	}
	
	@Override
	protected void onResume() {
		initView();
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
		this.unregisterReceiver(mBatteryReceiver);
		super.onDestroy();
	}
	
	private void registEvent() {
		View view = findViewById(R.id.id_iv_play_stop);
		view.setOnClickListener(this);
		view = findViewById(R.id.id_iv_delete);
		view.setOnClickListener(this);
	}

	private void initView(){
		context = getApplicationContext();
		id_tv_current_speed = (TextView)findViewById(R.id.id_tv_current_speed);
		id_tv_avg_speed = (TextView)findViewById(R.id.id_tv_avg_speed);
		id_tv_latitude = (TextView)findViewById(R.id.id_tv_latitude);
		id_tv_longitude = (TextView)findViewById(R.id.id_tv_longitude);
		id_tv_current_altitude = (TextView)findViewById(R.id.id_tv_current_altitude);
		id_tv_distance = (TextView)findViewById(R.id.id_tv_distance);
		id_tv_battery_status = (TextView)findViewById(R.id.id_tv_battery_status);
		id_iv_bike = (ImageView)findViewById(R.id.id_iv_bike);
		id_cm = (Chronometer)findViewById(R.id.id_cm);
		id_dc = (DigitalClock)findViewById(R.id.id_dc);
		
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			// TODO Auto-generated method stub
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
		// TODO 의사물어보기
		pausePedal();
		mTravelTime = 0;
		id_cm.setBase(SystemClock.elapsedRealtime());
		
		id_tv_current_speed.setText("00.0");
		id_tv_avg_speed.setText("00.0");
		id_tv_current_altitude.setText("000");
		id_tv_distance.setText("000");
		
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
		//  속도가 5 이상인 경우만 계산
		//	마지막 Location과 변경된 Location 거리계산
		//  변경된 Location 시간 - 마지막 저장된 시간
		long locationTime = location.getTime();
		
		// 최초실행인경우 필터링
		if ( mLastLocationTime != 0 ){
			float distanceFromLastLocation = location.distanceTo(mLastLocation);
			long elapsedTime = SystemClock.elapsedRealtime() - id_cm.getBase();

			// 속도가 100km 넘는경우는 거리에 포함시키지 않음. GPS 튀는 현상때문에 
			if ( distanceFromLastLocation / (locationTime - mLastLocationTime) * 3600 < 100000 ){
				mTotalDistance += distanceFromLastLocation;
				mAvgSpeed = mTotalDistance / (elapsedTime / 1000) * 3600 / 1000;
				id_tv_distance.setText(String.format("%.1f", mTotalDistance));
				id_tv_avg_speed.setText(String.format("%.1f", mAvgSpeed));
			}else{
			}
		}
		
		mLastLocationTime = locationTime;
		mLastLocation = location;

		Log.d("jiho", "onLocationChanged speed : "+location.getSpeed()+", altitude : "+location.getAltitude()+", lati : "+location.getLatitude()+", long : "+location.getLongitude());
		id_tv_current_speed.setText(String.format("%.1f", location.getSpeed()*3.6f));
		id_tv_latitude.setText(String.valueOf(location.getLatitude()));
		id_tv_longitude.setText(String.valueOf(location.getLongitude()));
		id_tv_current_altitude.setText(String.valueOf(location.getAltitude()));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onProviderDisabled(String provider) { }
}
