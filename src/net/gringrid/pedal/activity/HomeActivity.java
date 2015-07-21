package net.gringrid.pedal.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

public class HomeActivity extends Activity implements OnClickListener, LocationListener{

	final boolean DEBUG = false;
	final boolean IS_LOG_PRINT = true;

	/**
	 * 현재속도, 평균속도, 누적거리, 누적시간, 현재시간, 배터리 상태
	 */
	private Context context;
	
	/**
	 * View
	 */
	private TextView id_tv_current_speed;
	private TextView id_tv_avg_speed;
	private TextView id_tv_current_altitude;
	private TextView id_tv_distance;
	private TextView id_tv_battery_status;
	private Chronometer id_cm;

	/**
	 * GPS
	 */
	private LocationManager mLocationManager;
	private long mTravelTime;
	private long mLastLocationTime;
	private long mMoveTime;
	private Location mLastLocation;
	private float mAvgSpeed;
	private float mTotalDistance;

	/**
	 * Activity
	 */
	private final int EXIT_TIME_INTERVAL = 2000;
	private long mFirstBackButtonPressedTime;
	
	/**
	 * Sound
	 */
	private MediaPlayer mMPCadence;
	private MediaPlayer mMPPassing;
	
	/**
	 * Setting
	 */
	private Setting mSetting;
	private boolean mIsSaveGps;

	/**
	 * DB
	 */
	private RideDao mRideDao;
	private GpsLogDao mGpsLogDao;
	private long mRideId;
	
	private BroadcastReceiver mBR = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if ( action.equals(Intent.ACTION_BATTERY_CHANGED) ){
				int level = intent.getIntExtra("level", 0);
				id_tv_battery_status.setText(String.valueOf(level) + "%");
			}else if( action.equals(Intent.ACTION_DATE_CHANGED) ){
				setDayInfo();	
			}else if ( action.equals("com.android.music.metachanged") ||
					action.equals("com.android.music.playstatechanged") ||
					action.equals("com.android.music.playbackcomplete") ||
					action.equals("com.android.music.queuechanged") 
					){
				String artist = intent.getStringExtra("artist");
				String track = intent.getStringExtra("track");
				((TextView)findViewById(R.id.id_tv_music_artist)).setText(artist);
				((TextView)findViewById(R.id.id_tv_music_title)).setText(track);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_home);
		loadSetting();
		initView();
		registEvent();

		this.registerReceiver(mBR, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		this.registerReceiver(mBR, new IntentFilter(Intent.ACTION_DATE_CHANGED));

		if ( mSetting.IS_ENABLE_MUSIC ){
			IntentFilter iF = new IntentFilter();
			iF.addAction("com.android.music.metachanged");
			iF.addAction("com.android.music.playstatechanged");
			iF.addAction("com.android.music.playbackcomplete");
			iF.addAction("com.android.music.queuechanged");
			this.registerReceiver(mBR, iF);
		}
	}
	
	@Override
	protected void onResume() {
		initLocation();
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
		this.unregisterReceiver(mBR);
		super.onDestroy();
	}
	
	private void loadSetting(){
		mSetting = new Setting(this);
		if ( SharedData.getInstance(this).getGlobalDataBoolean(Setting.SHARED_KEY_INITIAL_SETTING) == false ){
			mSetting.initSetting();
		}
		if ( SharedData.getInstance(this).getGlobalDataBoolean(Setting.SHARED_KEY_SAVE_GPS) == true){
			mIsSaveGps = true;
			mGpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(this));
		}
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
		view = findViewById(R.id.id_iv_cadence_alarm);
		view.setOnClickListener(this);
		view = findViewById(R.id.id_iv_passing);
		view.setOnClickListener(this);
		view = findViewById(R.id.id_iv_list);
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
		findViewById(R.id.id_iv_cadence_alarm).setTag(R.drawable.ic_notifications_off_white_48dp);
		
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float)scale;
		id_tv_battery_status.setText(String.format("%.0f", batteryPct));
		
		setDayInfo();
		if ( IS_LOG_PRINT ){
			findViewById(R.id.id_sv_log).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.id_sv_log).setVisibility(View.GONE);
		}
	}
	
	private void setDayInfo() {
		Calendar calendar = Calendar.getInstance(); 
		int months = calendar.get(Calendar.MONTH) + 1;
		int days = calendar.get(Calendar.DAY_OF_MONTH);
		int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		String weekName = getResources().getStringArray(R.array.week)[week];
		
		TextView id_tv_day_info = (TextView)findViewById(R.id.id_tv_day_info);
		id_tv_day_info.setText(months+"."+days+" ("+weekName+")");
	}

	private void initLocation(){
		mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		checkGPS(mLocationManager);
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
	
	private void playPedal(){
		View v = findViewById(R.id.id_iv_play_stop);
		v.setTag(R.drawable.ic_pause_circle_outline_white_48dp);
		((ImageView)v).setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
		id_cm.setBase(SystemClock.elapsedRealtime() + mTravelTime);
		id_cm.start();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
		
		if ( mIsSaveGps && mRideDao == null ){
			mRideDao = RideDao.getInstance(DBHelper.getInstance(this));
			RideVO rideVo = new RideVO();
			rideVo.name = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
			rideVo.startTime = new Date().getTime();
			mRideId = mRideDao.insert(rideVo);
			Log.d("jiho", "mRideId insert : "+mRideId);
		}
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

	private void playCadenceAlarm(){
		View v = findViewById(R.id.id_iv_cadence_alarm);
		v.setTag(R.drawable.ic_notifications_active_white_48dp);
		((ImageView)v).setImageResource(R.drawable.ic_notifications_active_white_48dp);
		mMPCadence = MediaPlayer.create(this, R.raw.bs_11);
		mMPCadence.setLooping(true);
		if ( mMPCadence.isPlaying() == false ){
			mMPCadence.start();
		}
	}

	private void stopCadenceAlarm(){
		View v = findViewById(R.id.id_iv_cadence_alarm);
		v.setTag(R.drawable.ic_notifications_off_white_48dp);
		((ImageView)v).setImageResource(R.drawable.ic_notifications_off_white_48dp);

		if ( mMPCadence != null && mMPCadence.isPlaying() ){
			mMPCadence.stop();
		}
	}

	private void playPassing(){
		if ( mMPPassing == null ){
			mMPPassing = MediaPlayer.create(this, R.raw.passing);
		}
		if ( mMPPassing.isPlaying() == false ){
			mMPPassing.start();
		}
	}
	
	private void reset(){
		pausePedal();
		stopCadenceAlarm();
		mTravelTime = 0;
		mLastLocationTime = 0;
		id_cm.setBase(SystemClock.elapsedRealtime());
		id_tv_current_speed.setText("00.0");
		id_tv_avg_speed.setText("00.0");
		id_tv_current_altitude.setText("000");
		id_tv_distance.setText("000");
		mRideDao = null;
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
		Intent intent;
		
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

		case R.id.id_iv_list:
			intent = new Intent(this, RideListActivity.class);
			startActivity(intent);
			break;

		case R.id.id_iv_setting:
			intent = new Intent(this, SettingActivity.class);
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
		case R.id.id_iv_cadence_alarm:
			
			if ( (Integer)v.getTag() == R.drawable.ic_notifications_off_white_48dp ){
				playCadenceAlarm();
			}else if ( (Integer)v.getTag() == R.drawable.ic_notifications_active_white_48dp ){
				stopCadenceAlarm();
			}
			break;
			
		case R.id.id_iv_passing:
			playPassing();
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		
		final long locationTime = location.getTime();
		float locationSpeed = location.getSpeed();
		float locationSpeedKm = locationSpeed * 3.6f;
		String currentStatus = null;
		
		// 최초실행인경우 필터링
		if ( mLastLocationTime != 0 ){
			float distanceFromLastLocation = location.distanceTo(mLastLocation);
			float speedFromLastLocation = distanceFromLastLocation * 3.6f;
		
			// 평균속도를 계산하기 위해 멈춰있는경우는 제외한다. 
			if ( speedFromLastLocation <= 3 || locationSpeedKm < 2 ){
				currentStatus = "STOP";
			// 속도가 100km 가 넘는경우는 GPS가 튄것으로 판단하여 제외한다.
			}else if ( speedFromLastLocation > 150 ){
				currentStatus = "GPS ERROR(OVER 150km)";
			}else{
				currentStatus = "Riding";
				mMoveTime++;// += locationTime - mLastLocationTime;
				mTotalDistance += distanceFromLastLocation;
				mAvgSpeed = mTotalDistance / mMoveTime * 3.6f;
				id_tv_distance.setText(String.format("%.2f", mTotalDistance / 1000));
				id_tv_avg_speed.setText(String.format("%.1f", mAvgSpeed));
			}
		
			if ( IS_LOG_PRINT ){
				TextView id_tv_log = (TextView)findViewById(R.id.id_tv_log);
				String logText = "Accurace : "+String.valueOf(location.getAccuracy());
				logText += "\nMove time : "+String.valueOf(mMoveTime);
				logText += "\nSpeed from calculation : "+String.valueOf(speedFromLastLocation);
				logText += "\nSpeed from location : "+String.valueOf(locationSpeedKm);
				logText += "\nDistance from last location : "+String.format("%.1f", distanceFromLastLocation);
				logText += "\nLatitude : "+String.valueOf(location.getLatitude());
				logText += "\nLongitude : "+String.valueOf(location.getLongitude());
				logText += "\nCurrent status : "+currentStatus;
				id_tv_log.setText(logText);
				
//				((TextView)findViewById(R.id.id_tv_accuracy)).setText(String.valueOf(location.getAccuracy()));
//				((TextView)findViewById(R.id.id_tv_move_time)).setText(String.valueOf(mMoveTime));
//				((TextView)findViewById(R.id.id_tv_speed_from_last_location)).setText(String.valueOf(speedFromLastLocation));
//				((TextView)findViewById(R.id.id_tv_location_speed)).setText(String.valueOf(locationSpeedKm));
//				((TextView)findViewById(R.id.id_tv_last_distance)).setText(String.format("%.1f", distanceFromLastLocation));
//				((TextView)findViewById(R.id.id_tv_latitude)).setText(String.valueOf(location.getLatitude()));
//				((TextView)findViewById(R.id.id_tv_longitude)).setText(String.valueOf(location.getLongitude()));
//				((TextView)findViewById(R.id.id_tv_current_status)).setText(currentStatus);
			}

			if ( mIsSaveGps ){
				GpsLogVO gpsLogVo = new GpsLogVO();
				gpsLogVo.parentId = mRideId;
				gpsLogVo.latitude = location.getLatitude();
				gpsLogVo.longitude = location.getLongitude();
				gpsLogVo.elevation = location.getAltitude();
				gpsLogVo.gpsTime = locationTime;
				mGpsLogDao.insert(gpsLogVo);
			}
		}
		
		mLastLocationTime = locationTime;
		mLastLocation = location;

		id_tv_current_speed.setText(String.format("%.1f", locationSpeedKm));
		id_tv_current_altitude.setText(String.valueOf(location.getAltitude()));
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onProviderDisabled(String provider) { }
}
