package net.gringrid.pedal.view;

import net.gringrid.pedal.R;
import net.gringrid.pedal.activity.ExpandableRidingListActivity;
import net.gringrid.pedal.activity.RidingActivity;
import net.gringrid.pedal.activity.SettingActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;

public class HeaderView extends FrameLayout implements OnClickListener{

	private Context mContext;
	private View mView;
	private RelativeLayout id_ll_menu;
	private ImageView id_iv_back;
	private ImageView id_iv_menu;
	
	
	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setLayout(attrs);
		registEvent();
	}

	private void setLayout(AttributeSet attrs) {
		TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.HeaderView);
		String id_iv_back_visibility = array.getString(R.styleable.HeaderView_id_iv_back_visibility);
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.header_view, this);
		id_ll_menu = (RelativeLayout)findViewById(R.id.id_ll_menu);
		id_iv_back = (ImageView)findViewById(R.id.id_iv_back);
		id_iv_menu = (ImageView)findViewById(R.id.id_iv_menu);
		if ( id_iv_back_visibility != null && id_iv_back_visibility.equals("VISIBLE") ){
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
				};
		for ( int viewId : setClickEventViewsList ){
			View view = findViewById( viewId );
			view.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		
		switch (v.getId()) {
		case R.id.id_iv_back:			
			((Activity)mContext).onBackPressed();
			break;

		case R.id.id_iv_menu:
			toggleMenu();
			break;

		case R.id.id_tv_riding:
			intent = new Intent(mContext, RidingActivity.class);
			mContext.startActivity(intent);
			break;

		case R.id.id_tv_riding_list:
			intent = new Intent(mContext, ExpandableRidingListActivity.class);
			mContext.startActivity(intent);
			break;

		case R.id.id_tv_setting:
			intent = new Intent(mContext, SettingActivity.class);
			mContext.startActivity(intent);
			break;
			
		default:
			break;
		}
	}

	private void toggleMenu() {
		id_ll_menu.setVisibility( id_ll_menu.getVisibility()==View.GONE ? View.VISIBLE : View.GONE );
	}



	

}
