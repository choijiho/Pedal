package net.gringrid.pedal.activity;

import net.gringrid.pedal.R;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.SharedData;
import net.gringrid.pedal.db.vo.DisplayVO;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class GearActivity extends Activity implements OnSeekBarChangeListener, OnClickListener {

	TextView id_tv_tires;
	TextView id_tv_tire_circumference;

	TextView id_tv_chainring;
	TextView id_tv_sprocket;
	TextView id_tv_cadence;
	TextView id_tv_speed;

	SeekBar id_sb_chainring;
	SeekBar id_sb_sprocket;
	SeekBar id_sb_cadence;
	
	final int MIN_CHAINRING = 20;
	final int MIN_SPROCKET = 8;
	
	int mChainring;
	int mSprocket;
	int mCadence;
	int mTireCircumferenceIndex;
	
	int mTireCircumference;
	String[] mTireCircumferenceListName;
	String[] mTireCircumferenceListValue;
		
	SharedData mSharedData;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gear);
		initView();
		registEvent();
	}

	@Override
	protected void onPause() {
		mSharedData.insertGlobalData(Setting.SHARED_KEY_GEAR_CHAINRING, mChainring);
		mSharedData.insertGlobalData(Setting.SHARED_KEY_GEAR_SPROCKET, mSprocket);
		mSharedData.insertGlobalData(Setting.SHARED_KEY_GEAR_CADENCE, mCadence);
		mSharedData.insertGlobalData(Setting.SHARED_KEY_GEAR_TIRES_INDEX, mTireCircumferenceIndex);
		super.onPause();
	}
	
	
	private void registEvent() {
		int [] setSeekBarChangeViewsList = {
				R.id.id_sb_chainring
				,R.id.id_sb_sprocket
				,R.id.id_sb_cadence
				};
		for ( int viewId : setSeekBarChangeViewsList ){
			SeekBar view = (SeekBar)findViewById( viewId );
			view.setOnSeekBarChangeListener(this);
		}
		
		int [] setClickEventViewsList = {
				R.id.id_tv_tires
		};
		
		for ( int viewId : setClickEventViewsList ){
			View view = findViewById( viewId );
			view.setOnClickListener(this);
		}
	}

	private void initView() {
		mSharedData = SharedData.getInstance(this);
		mTireCircumferenceListName = getResources().getStringArray(R.array.tire_circumference_name);
		mTireCircumferenceListValue = getResources().getStringArray(R.array.tire_circumference_value);
		
		id_tv_tires = (TextView)findViewById(R.id.id_tv_tires);
		id_tv_tire_circumference = (TextView)findViewById(R.id.id_tv_tire_circumference);
		id_tv_chainring = (TextView)findViewById(R.id.id_tv_chainring);
		id_tv_sprocket = (TextView)findViewById(R.id.id_tv_sprocket);
		id_tv_cadence = (TextView)findViewById(R.id.id_tv_cadence);
		id_tv_speed = (TextView)findViewById(R.id.id_tv_speed);

		id_sb_chainring = (SeekBar)findViewById(R.id.id_sb_chainring);
		id_sb_sprocket = (SeekBar)findViewById(R.id.id_sb_sprocket);
		id_sb_cadence = (SeekBar)findViewById(R.id.id_sb_cadence);

		mTireCircumferenceIndex = mSharedData.getGlobalDataInt(Setting.SHARED_KEY_GEAR_TIRES_INDEX, 54);
		mChainring = mSharedData.getGlobalDataInt(Setting.SHARED_KEY_GEAR_CHAINRING, 34);
		mSprocket = mSharedData.getGlobalDataInt(Setting.SHARED_KEY_GEAR_SPROCKET, 14);
		mCadence = mSharedData.getGlobalDataInt(Setting.SHARED_KEY_GEAR_CADENCE, 90);
		mTireCircumference = Integer.parseInt(mTireCircumferenceListValue[mTireCircumferenceIndex]);
		
		id_sb_chainring.setProgress(mChainring);
		id_sb_sprocket.setProgress(mSprocket);
		id_sb_cadence.setProgress(mCadence);
		
		id_tv_tires.setText(mTireCircumferenceListName[mTireCircumferenceIndex]);
		id_tv_tire_circumference.setText(mTireCircumferenceListValue[mTireCircumferenceIndex]);
		setChainring(mChainring);
		setSprocket(mSprocket);
		setCadence(mCadence);
		
		setSpeed();
	}
	
	private float getSpeed(){

		float chainring = mChainring;
		float sprocket = mSprocket;
		float cadence = mCadence;
		float tireCircumference = mTireCircumference;
	
		float result = chainring / sprocket * tireCircumference * cadence * 60 / 1000000; // mm > km
		return result;
		
	}
	
	private void setSpeed(){
		id_tv_speed.setText(String.format("%.1f", getSpeed()));
	}
	
	private void setChainring(int value){
		mChainring = value;
		id_tv_chainring.setText(String.format("%d", mChainring+MIN_CHAINRING));
	}

	private void setSprocket(int value){
		mSprocket = value;
		id_tv_sprocket.setText(String.format("%d", mSprocket+MIN_SPROCKET));
	}

	private void setCadence(int value){
		mCadence = value;
		id_tv_cadence.setText(String.format("%d", value));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.id_sb_chainring:
			setChainring(progress);
			setSpeed();
			break;
		case R.id.id_sb_sprocket:
			setSprocket(progress);
			setSpeed();
			break;
		case R.id.id_sb_cadence:
			setCadence(progress);
			setSpeed();
			break;

		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }

	private void showWheelList() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setItems(mTireCircumferenceListName, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTireCircumferenceIndex = which;
				id_tv_tire_circumference.setText(mTireCircumferenceListValue[mTireCircumferenceIndex]);
				mTireCircumference = Integer.parseInt(mTireCircumferenceListValue[mTireCircumferenceIndex]);
				setSpeed();
			}
		});
		builder.show();
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_tv_tires:
		case R.id.id_tv_tire_circumference:
			showWheelList();
			break;

		default:
			break;
		}
	}

}
