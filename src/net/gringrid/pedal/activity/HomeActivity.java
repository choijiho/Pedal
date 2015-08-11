package net.gringrid.pedal.activity;

import net.gringrid.pedal.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class HomeActivity extends Activity implements OnClickListener{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		registEvent();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private void registEvent() {
		int [] setClickEventViewsList = {
				R.id.id_iv_riding
				,R.id.id_iv_riding_list
				,R.id.id_iv_setting
				};
		for ( int viewId : setClickEventViewsList ){
			View view = findViewById( viewId );
			view.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.id_iv_riding:
			intent = new Intent(this, RidingActivity.class);
			startActivity(intent);
			break;

		case R.id.id_iv_riding_list:
			intent = new Intent(this, RidingListActivity.class);
			startActivity(intent);
			break;

		case R.id.id_iv_setting:
			intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}




