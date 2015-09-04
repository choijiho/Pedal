package net.gringrid.pedal.activity;

import net.gringrid.pedal.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class GearActivity extends Activity implements OnSeekBarChangeListener {

	TextView id_tv_tires;
	TextView id_tv_tire_circumference;

	TextView id_tv_chainring;
	TextView id_tv_sprocket;
	TextView id_tv_cadence;
	TextView id_tv_speed;

	SeekBar id_sb_chainring;
	SeekBar id_sb_sprocket;
	SeekBar id_sb_cadence;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gear);
		initView();
		registEvent();
	}

	private void registEvent() {
		int [] setClickEventViewsList = {
				R.id.id_sb_chainring
				,R.id.id_sb_sprocket
				,R.id.id_sb_cadence
				};
		for ( int viewId : setClickEventViewsList ){
			SeekBar view = (SeekBar)findViewById( viewId );
			view.setOnSeekBarChangeListener(this);
		}
	}

	private void initView() {
		id_tv_tires = (TextView)findViewById(R.id.id_tv_tires);
		id_tv_tire_circumference = (TextView)findViewById(R.id.id_tv_tire_circumference);
		id_tv_chainring = (TextView)findViewById(R.id.id_tv_chainring);
		id_tv_sprocket = (TextView)findViewById(R.id.id_tv_sprocket);
		id_tv_cadence = (TextView)findViewById(R.id.id_tv_cadence);
		id_tv_speed = (TextView)findViewById(R.id.id_tv_speed);

		id_sb_chainring = (SeekBar)findViewById(R.id.id_sb_chainring);
		id_sb_sprocket = (SeekBar)findViewById(R.id.id_sb_sprocket);
		id_sb_cadence = (SeekBar)findViewById(R.id.id_sb_cadence);

		id_tv_tires.setText("700 x 23c");
		id_tv_tire_circumference.setText("2096");
		id_tv_chainring.setText(String.valueOf(id_sb_chainring.getProgress()));
		id_tv_chainring.setText(String.valueOf(id_sb_chainring.getProgress()));
		id_tv_sprocket.setText(String.valueOf(id_sb_sprocket.getProgress()));
		id_tv_cadence.setText(String.valueOf(id_sb_cadence.getProgress()));
	}
	
	private float getSpeed(){
		int tireCircumference = Integer.parseInt(id_tv_tire_circumference.getText().toString());
		int chainring = Integer.parseInt(id_tv_chainring.getText().toString());
		int sprocket = Integer.parseInt(id_tv_sprocket.getText().toString());
		int cadence = Integer.parseInt(id_tv_cadence.getText().toString());
		
		Log.d("jiho", "chainring : "+chainring+", sprocket : "+sprocket+", tireCircumference : "+tireCircumference+", cadence : "+cadence);
		float result = chainring / sprocket * tireCircumference * cadence * 60 / 1000;
		
		return result;
		
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.id_sb_chainring:
			id_tv_chainring.setText(String.valueOf(progress));
			id_tv_speed.setText(String.format("%.2f", getSpeed()));
			break;
		case R.id.id_sb_sprocket:
			id_tv_sprocket.setText(String.valueOf(progress));
			id_tv_speed.setText(String.format("%.2f", getSpeed()));
			break;
		case R.id.id_sb_cadence:
			id_tv_cadence.setText(String.valueOf(progress));
			id_tv_speed.setText(String.format("%.2f", getSpeed()));
			break;

		default:
			break;
		}
		// TODO Auto-generated method stub
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}


}
