package net.gringrid.pedal.view;

import net.gringrid.pedal.R;
import net.gringrid.pedal.activity.DisplayResult;
import net.gringrid.pedal.activity.DisplayTest;
import net.gringrid.pedal.activity.ExpandableRidingListActivity;
import net.gringrid.pedal.activity.GearActivity;
import net.gringrid.pedal.activity.RidingActivity;
import net.gringrid.pedal.activity.RidingActivityNew;
import net.gringrid.pedal.activity.SettingActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class HeaderView extends FrameLayout implements OnClickListener{

	private Context mContext;
	private RelativeLayout id_ll_menu;
	private ImageView id_iv_back;

	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setLayout(attrs);
		registEvent();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("jiho", "onTouchEvent");
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
	private void setLayout(AttributeSet attrs) {
		TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.HeaderView);
		String id_iv_back_visibility = array.getString(R.styleable.HeaderView_id_iv_back_visibility);
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.header_view, this);
		id_ll_menu = (RelativeLayout)findViewById(R.id.id_ll_menu);
		id_iv_back = (ImageView)findViewById(R.id.id_iv_back);

		if ( id_iv_back_visibility != null && id_iv_back_visibility.equals("visible") ){
			id_iv_back.setVisibility(View.VISIBLE);
		}else{
			id_iv_back.setVisibility(View.INVISIBLE);
		}
	}
	
	private void registEvent() {
		int [] setClickEventViewsList = {
				R.id.id_iv_menu
				,R.id.id_iv_back
				,R.id.id_tv_riding
				,R.id.id_tv_riding_list
				,R.id.id_tv_setting
				,R.id.id_tv_display_test
				,R.id.id_tv_display_result
				,R.id.id_tv_gear
				,R.id.id_tv_riding_new
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
		case R.id.id_iv_back:			
			((Activity)mContext).onBackPressed();
			break;

		case R.id.id_iv_menu:
			toggleMenu();
			break;

		case R.id.id_tv_riding:
			intent = new Intent(mContext, RidingActivity.class);
			break;

		case R.id.id_tv_riding_list:
			intent = new Intent(mContext, ExpandableRidingListActivity.class);
			break;

		case R.id.id_tv_setting:
			intent = new Intent(mContext, SettingActivity.class);
			break;
			
		case R.id.id_tv_display_test:
			intent = new Intent(mContext, DisplayTest.class);
			break;
			
		case R.id.id_tv_display_result:
			intent = new Intent(mContext, DisplayResult.class);
			break;
			
		case R.id.id_tv_gear:
			intent = new Intent(mContext, GearActivity.class);
			break;

		case R.id.id_tv_riding_new:
			intent = new Intent(mContext, RidingActivityNew.class);
			break;
			
		default:
			break;
		}
	
		if ( intent != null ){
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			hideMenu();
			mContext.startActivity(intent);
		}
	}

	private void toggleMenu() {
		id_ll_menu.setVisibility( id_ll_menu.getVisibility()==View.GONE ? View.VISIBLE : View.GONE );
	}
	
	private void hideMenu(){
		id_ll_menu.setVisibility(View.GONE);
	}
}
