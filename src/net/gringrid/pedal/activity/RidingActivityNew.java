package net.gringrid.pedal.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import net.gringrid.pedal.DisplayInfoManager;
import net.gringrid.pedal.ItemFactory;
import net.gringrid.pedal.R;
import net.gringrid.pedal.RidingInfoUtility;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.SharedData;
import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.RideDao;
import net.gringrid.pedal.db.vo.DisplayVO;
import net.gringrid.pedal.db.vo.GpsLogVO;
import net.gringrid.pedal.db.vo.RideVO;
import net.gringrid.pedal.view.ItemView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RidingActivityNew extends Activity implements OnClickListener, LocationListener{

	final boolean DEBUG = false;
	final boolean IS_LOG_PRINT = false;

	/**
	 * Riding information items
	 */
	private Vector<View> mRidingInfoViews;
	/**
	 * Riding information display area
	 */
	private FrameLayout id_fl_riding_info_area;
	
	private String[] mRidingInfoList;
	private String[] mRidingInfoViewTypeList;
	
	
	final int STATE_RIDING = 0x00;
	final int STATE_STOP = 0x01;
	int STATE;
	
	/**
	 * View
	 */
	private ItemView mItemViewCurrentSpeed;
	private ItemView mItemViewAvgSpeed;
	private ItemView mItemViewMaxSpeed;
	private ItemView mItemViewDistance;
	private ItemView mItemViewAltitude;
	private ItemView mItemViewTravleTime;
	private ItemView mItemViewRidingTime;
	private ItemView mItemViewPresentTime;
	private ItemView mItemViewDate;
	private ItemView mItemViewBattery;
	
	private TextView id_tv_battery_status;
	private Chronometer id_cm;

	/**
	 * GPS
	 */
	private LocationManager mLocationManager;
	private long mTravelTime;
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
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_riding_new);
		initView();
		registEvent();
		initLocation();
		loadSetting();
//		this.registerReceiver(mBR, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//		this.registerReceiver(mBR, new IntentFilter(Intent.ACTION_DATE_CHANGED));
	}
	
	@Override
	protected void onResume() {
		initView();
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
//		this.unregisterReceiver(mBR);
		super.onDestroy();
	}
	
	private void loadSetting(){
		mSetting = new Setting(this);
		// TODO TEST 
//		mSetting.initSetting();
		
		if ( SharedData.getInstance(this).getGlobalDataBoolean(Setting.SHARED_KEY_INITIAL_SETTING) == false ){
			mSetting.initSetting();
		}
		if ( SharedData.getInstance(this).getGlobalDataBoolean(Setting.SHARED_KEY_SAVE_GPS) == true){
			mIsSaveGps = true;
			mGpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(this));
		}
		
		// Stop 버튼이 눌려서 정상종료되지 않고 재실행되는 경우
		mRideId = SharedData.getInstance(this).getGlobalDataLong(Setting.SHARED_KEY_RIDING_ID);
		if ( mRideId != Long.MAX_VALUE){
			playPedal();
		}
	}

	private void registEvent() {
		int[] setClickEventViewsList = {
				R.id.id_iv_play
				,R.id.id_iv_stop
				,R.id.id_iv_pause
				,R.id.id_iv_cadence_alarm
		};
		for ( int viewId : setClickEventViewsList ){
			View view = findViewById( viewId );
			view.setOnClickListener(this);
		}
	}

	private void initView(){
		mRidingInfoViews = new Vector<View>();
		mRidingInfoList = getResources().getStringArray(R.array.riding_infomation_list);
		mRidingInfoViewTypeList = getResources().getStringArray(R.array.riding_infomation_view_type);
		
		id_fl_riding_info_area = (FrameLayout)findViewById(R.id.id_fl_riding_info_area);
		id_fl_riding_info_area.setLayoutParams(new LinearLayout.LayoutParams(DisplayInfoManager.getInstance(this).width, DisplayInfoManager.getInstance(this).width));
		id_fl_riding_info_area.removeAllViews();
		
		// Toggle 기본값
		findViewById(R.id.id_iv_cadence_alarm).setTag(R.drawable.ic_notifications_off_white_48dp);
		
//		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//		Intent batteryStatus = this.registerReceiver(null, ifilter);
//		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//
//		float batteryPct = level / (float)scale;
//		id_tv_battery_status.setText(String.format("%.0f", batteryPct));
		
//		setDayInfo();
		
//		if ( IS_LOG_PRINT ){
//			findViewById(R.id.id_sv_log).setVisibility(View.VISIBLE);
//		}else{
//			findViewById(R.id.id_sv_log).setVisibility(View.GONE);
//		}
		
		executeDisplay();
	}
	
	private void executeDisplay(){
		
		// Riding information을 그린다. 
		DisplayVO vo = null;
		Setting setting = new Setting(this);

		for ( String item : mRidingInfoList){
			vo = setting.getDisplayInfo(item);
			if ( vo.isUsed ){
				drawRidingItems(vo);
				// TODO DEBUG
//				vo.debug();
			}
		}
	}
	
	private void drawRidingItems(DisplayVO vo){
		if ( vo.minIndex == vo.maxIndex ){
			return;
		}
		
		int listIndex = 0;
		for ( int i=0; i<mRidingInfoList.length; i++){
			if ( vo.itemName.equals(mRidingInfoList[i]) ){
				listIndex = i;
			}
		}
		vo.viewType = mRidingInfoViewTypeList[listIndex];
		View itemView = ItemFactory.createView(this, vo);
		
		if ( itemView != null ){
			mRidingInfoViews.add(itemView);
			id_fl_riding_info_area.addView(itemView, vo.params);
			setViewVariableFromRidingInvfoViews(itemView, vo);
		}
	}

	private void setViewVariableFromRidingInvfoViews(View view, DisplayVO vo) {
		if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_CURRENT_SPPED])){
			mItemViewCurrentSpeed = (ItemView)view;
		}else if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_AVG_SPEED])){
			mItemViewAvgSpeed = (ItemView)view;
		}else if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_MAX_SPEED])){
			mItemViewMaxSpeed = (ItemView)view;
		}else if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_DISTANCE])){
			mItemViewDistance = (ItemView)view;
		}else if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_ALTITUDE])){
			mItemViewAltitude = (ItemView)view;
		}else if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_TRAVEL_TIME])){
			mItemViewTravleTime = (ItemView)view;
		}else if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_RIDING_TIME])){
			mItemViewRidingTime = (ItemView)view;
		}else if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_PRESENT_TIME])){
			mItemViewTravleTime = (ItemView)view;
		}else if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_DATE])){
			mItemViewDate = (ItemView)view;
		}else if ( vo.itemName.equals(mRidingInfoList[DisplayInfoManager.INDEX_BATTERY])){
			mItemViewBattery = (ItemView)view;
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
		findViewById(R.id.id_iv_play).setVisibility(View.GONE);
		findViewById(R.id.id_iv_pause).setVisibility(View.VISIBLE);
		findViewById(R.id.id_iv_stop).setVisibility(View.VISIBLE);
		
		if ( mRideId != Long.MAX_VALUE ){
			ArrayList<String> detailInfo = new ArrayList<String>();
			RidingInfoUtility ridingInfoUtility = new RidingInfoUtility(this);
			detailInfo = ridingInfoUtility.calculateRideInfo(mRideId);
	
			mMoveTime = Long.parseLong(detailInfo.get(DisplayInfoManager.INDEX_RIDING_TIME));
			mTotalDistance = Float.parseFloat(detailInfo.get(DisplayInfoManager.INDEX_DISTANCE));
		}

		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
//		id_cm.setBase(SystemClock.elapsedRealtime() + mTravelTime);
//		id_cm.start();
		if ( mRideId == Long.MAX_VALUE && mIsSaveGps && mRideDao == null ){
			mRideDao = RideDao.getInstance(DBHelper.getInstance(this));
			RideVO rideVo = new RideVO();
			rideVo.name = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
			rideVo.startTime = System.currentTimeMillis();
			mRideId = mRideDao.insert(rideVo);
			SharedData.getInstance(this).insertGlobalData(Setting.SHARED_KEY_RIDING_ID, mRideId);
		}
	}
	
	private void pausePedal(){
		findViewById(R.id.id_iv_play).setVisibility(View.VISIBLE);
		findViewById(R.id.id_iv_pause).setVisibility(View.GONE);
		findViewById(R.id.id_iv_stop).setVisibility(View.GONE);
//		mTravelTime = id_cm.getBase() - SystemClock.elapsedRealtime();
//		id_cm.stop();
		mLocationManager.removeUpdates(this);
	}

	private void stopPedal(){
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
		if ( mRideId != Long.MAX_VALUE ){
			RidingInfoUtility ridingInfoUtility = new RidingInfoUtility(RidingActivityNew.this);
			ridingInfoUtility.saveRidingInfo(mRideId);
		}
		pausePedal();
		stopCadenceAlarm();
		mTravelTime = 0;
		mLastLocation = null;
//		id_cm.setBase(SystemClock.elapsedRealtime());
//		id_tv_current_speed.setText("00.0");
//		id_tv_avg_speed.setText("00.0");
//		id_tv_current_altitude.setText("000");
//		id_tv_distance.setText("000");
		mRideDao = null;
		mRideId = Long.MAX_VALUE;
		SharedData.getInstance(RidingActivityNew.this).insertGlobalData(Setting.SHARED_KEY_RIDING_ID, mRideId);
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
		case R.id.id_iv_play:
			playPedal();
			break;
			
		case R.id.id_iv_pause:
			pausePedal();
			break;
			
		case R.id.id_iv_stop:
			stopPedal();
			break;
			
		case R.id.id_iv_cadence_alarm:
			
			if ( (Integer)v.getTag() == R.drawable.ic_notifications_off_white_48dp ){
				playCadenceAlarm();
			}else if ( (Integer)v.getTag() == R.drawable.ic_notifications_active_white_48dp ){
				stopCadenceAlarm();
			}
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
		if ( mLastLocation != null ){
			float distanceFromLastLocation = location.distanceTo(mLastLocation);
			long gapTimeFromLastlocation = (locationTime  - mLastLocation.getTime()) / 1000;
			float speedFromLastLocation = distanceFromLastLocation / gapTimeFromLastlocation * 3.6f;
		
			// 평균속도를 계산하기 위해 멈춰있는경우는 제외한다. 
			if ( speedFromLastLocation <= 3 || locationSpeedKm < 2 ){
				currentStatus = "STOP";
			// 속도가 100km 가 넘는경우는 GPS가 튄것으로 판단하여 제외한다.
			}else if ( speedFromLastLocation > 150 ){
				currentStatus = "GPS ERROR(OVER 150km)";
			}else{
				currentStatus = "Riding";
				mMoveTime += gapTimeFromLastlocation;// += locationTime - mLastLocationTime;
				mTotalDistance += distanceFromLastLocation;
				mAvgSpeed = mTotalDistance / mMoveTime * 3.6f;
				setItemContentValue(mItemViewAvgSpeed, String.format("%.1f", mAvgSpeed));
				setItemContentValue(mItemViewDistance, String.format("%.2f", mTotalDistance / 1000));
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
		
		mLastLocation = location;
		
		setItemContentValue(mItemViewCurrentSpeed, String.format("%.1f", locationSpeedKm));
		setItemContentValue(mItemViewAltitude, String.valueOf(location.getAltitude()));
	}
	
	private void setItemContentValue(ItemView itemView, String value){
		if ( itemView != null ){
			itemView.setContent(value);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider) { }

	@Override
	public void onProviderDisabled(String provider) { }
}




