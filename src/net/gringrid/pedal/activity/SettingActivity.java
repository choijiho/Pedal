package net.gringrid.pedal.activity;

import net.gringrid.pedal.R;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.SharedData;
import net.gringrid.pedal.R.id;
import net.gringrid.pedal.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class SettingActivity extends Activity implements OnCheckedChangeListener, OnClickListener {
	final String STRAVA_CLIENT_ID = "7262";
	final String STRAVA_CLIENT_PASSWORD = "52b1c8ca899a6bb52c9f83d6a8541c980757432c";
	final String STRAVA_ACCESS_TOKEN = "597cdd10092f4e9d39154476f5cdcdaff3610076";

	CheckBox id_chk_current_speed;
	CheckBox id_chk_average_speed;
	CheckBox id_chk_music;
	CheckBox id_chk_save_gps;
	ImageView id_iv_strava;
	
	Setting mSetting;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);	
		initView();
		registEvent();
	}
	
	private void initView(){
		mSetting = new Setting(this);
		id_chk_current_speed = (CheckBox)findViewById(R.id.id_chk_current_speed);
		id_chk_average_speed = (CheckBox)findViewById(R.id.id_chk_average_speed);
		id_chk_music = (CheckBox)findViewById(R.id.id_chk_music);
		id_chk_save_gps = (CheckBox)findViewById(R.id.id_chk_save_gps);
		id_iv_strava = (ImageView)findViewById(R.id.id_iv_strava);

		id_chk_current_speed.setChecked(mSetting.IS_ENABLE_CURRENT_SPEED);
		id_chk_average_speed.setChecked(mSetting.IS_ENABLE_AVERAGE_SPEED);
		id_chk_music.setChecked(mSetting.IS_ENABLE_MUSIC);
		id_chk_save_gps.setChecked(mSetting.IS_ENABLE_SAVE_GPS);
		
		
	}
	
	private void registEvent(){
		id_chk_current_speed.setOnCheckedChangeListener(this);
		id_chk_average_speed.setOnCheckedChangeListener(this);
		id_chk_music.setOnCheckedChangeListener(this);
		id_iv_strava.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.id_chk_current_speed:
			SharedData.getInstance(this).insertGlobalData(Setting.SHARED_KEY_CURRENT_SPEED, isChecked);
			break;
		case R.id.id_chk_average_speed:
			SharedData.getInstance(this).insertGlobalData(Setting.SHARED_KEY_AVERAGE_SPEED, isChecked);
			break;
		case R.id.id_chk_music:
			SharedData.getInstance(this).insertGlobalData(Setting.SHARED_KEY_MUSIC, isChecked);
			break;
		case R.id.id_chk_save_gps:
			SharedData.getInstance(this).insertGlobalData(Setting.SHARED_KEY_SAVE_GPS, isChecked);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_iv_strava:
			moveToStravaAuthPage();
			break;

		default:
			break;
		}
		
	}
	
	private void moveToStravaAuthPage(){
		String url = "https://www.strava.com/oauth/authorize?"+
			  "client_id="+STRAVA_CLIENT_ID+
			  "&response_type=code"+
			  "&redirect_uri=http://gringrid.net/pedal/token_exchange.php"+
			  "&scope=write"+
			  "&state=mystate"+
			  "&approval_prompt=force";
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

}
