package net.gringrid.pedal.activity;

import net.gringrid.pedal.R;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.SharedData;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class DisplayResult extends Activity {
	
	private int mWidth;
	private int mHeight;
	private int mCellWidth;
	private int mCellHeight;
	final int mCols = 8;
	final int mRows = 8;
	
	
	int display_cur_speed_min 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_CUR_SPEED_MIN);
	int display_cur_speed_max 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_CUR_SPEED_MAX);
	int display_avg_speed_min 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_AVG_SPEED_MIN);
	int display_avg_speed_max 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_AVG_SPEED_MAX);
	int display_max_speed_min 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_CUR_SPEED_MIN);
	int display_max_speed_max 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_CUR_SPEED_MIN);
	int display_altitude_min 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_ALTITUDE_MIN);
	int display_altitude_max 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_ALTITUDE_MAX);
	int display_distance_min 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_DISTANCE_MIN);
	int display_distance_max 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_DISTANCE_MAX);
	int display_travel_time_min 	= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_TRAVEL_TIME_MIN);
	int display_travel_time_max 	= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_TRAVEL_TIME_MAX);
	int display_riding_time_min 	= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_RIDING_TIME_MIN);
	int display_riding_time_max 	= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_RIDING_TIME_MAX);
	int display_present_time_min 	= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_DATE_MIN);
	int display_present_time_max 	= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_DATE_MAX);
	int display_date_min 			= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_DATE_MIN);
	int display_date_max 			= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_DATE_MAX);
	int display_battery_min 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_BATTERY_MIN);
	int display_battery_max 		= SharedData.getInstance(this).getGlobalDataInt(Setting.SHARED_KEY_DISPLAY_BATTERY_MAX);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_display_result);	
	    // TODO Auto-generated method stub
	    setDisplayInfo();
	    init();
	}

	private void init() {
		FrameLayout id_fl_base = (FrameLayout)findViewById(R.id.id_fl_base);
		id_fl_base.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mWidth));
		
	}

	private void setDisplayInfo() {
		Display display = getWindowManager().getDefaultDisplay(); 
		mWidth = display.getWidth();
		mHeight = display.getHeight();
		mCellWidth = mWidth / mCols;
		mCellHeight = mWidth / mRows;
	}

}
