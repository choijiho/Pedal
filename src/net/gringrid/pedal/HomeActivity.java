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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity implements OnClickListener{

	final boolean DEBUG = false;

	/**
	 * 현재속도, 평균속도, 누적거리, 누적시간, 현재시간, 배터리 상태
	 */
	Context context;
	TextView id_tv_current_speed;
	TextView id_tv_latitude;
	TextView id_tv_longitude;
	TextView id_tv_current_altitude;
	TextView id_tv_battery_status;
	Chronometer id_cm;
	DigitalClock id_dc;
	LocationManager mLocationManager;
	LocationListener mLocationListener;
	MockLocationProvider mock;
	
	
	private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra("level", 0);
			id_tv_battery_status.setText(String.valueOf(level) + "%");
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
		View view = findViewById(R.id.iv_play_stop);
		view.setOnClickListener(this);
		view = findViewById(R.id.iv_pause);
		view.setOnClickListener(this);
	}

	private void initView(){
		context = getApplicationContext();
		id_tv_current_speed = (TextView)findViewById(R.id.id_tv_current_speed);
		id_tv_latitude = (TextView)findViewById(R.id.id_tv_latitude);
		id_tv_longitude = (TextView)findViewById(R.id.id_tv_longitude);
		id_tv_current_altitude = (TextView)findViewById(R.id.id_tv_current_altitude);
		id_tv_battery_status = (TextView)findViewById(R.id.id_tv_battery_status);
		id_cm = (Chronometer)findViewById(R.id.id_cm);
		id_dc = (DigitalClock)findViewById(R.id.id_dc);

		
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
		mLocationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) { 
				Log.d("jiho", "onStatusChanged");
			}
			
			@Override
			public void onProviderEnabled(String provider) { 
				Log.d("jiho", "onProviderEnabled");
			}
			
			@Override
			public void onProviderDisabled(String provider) { 
				Log.d("jiho", "onProviderDisabled");
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				Log.d("jiho", "onLocationChanged speed : "+location.getSpeed()+", altitude : "+location.getAltitude()+", lati : "+location.getLatitude()+", long : "+location.getLongitude());
				id_tv_current_speed.setText(String.format("%.1f", location.getSpeed()*3.6f));
				id_tv_latitude.setText(String.valueOf(location.getLatitude()));
				id_tv_longitude.setText(String.valueOf(location.getLongitude()));
				id_tv_current_altitude.setText(String.valueOf(location.getAltitude()));
			}
		};
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
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
					mLocationListener.onLocationChanged(location);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_play_stop:
			// TRAVEL TIME, DISTANCE, AVG SPEED
			id_cm.start();
			break;

		default:
			break;
		}
		// TODO Auto-generated method stub
		
	}
}
