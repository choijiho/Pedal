package net.gringrid.pedal;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SettingActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);	
	    // TODO Auto-generated method stub
	}

}
