package net.gringrid.pedal.activity;

import net.gringrid.pedal.DisplayInfoManager;
import net.gringrid.pedal.R;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.db.vo.DisplayVO;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayResult extends Activity implements OnClickListener{
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_display_result);	
		init();
		registEvent();
	    executeDisplay();
	}
	
	private void registEvent() {
		int [] setClickEventViewsList = {
				R.id.id_tv_add
				,R.id.id_tv_clear
				,R.id.id_tv_clear_all
				};
		for ( int viewId : setClickEventViewsList ){
			View view = findViewById( viewId );
			view.setOnClickListener(this);
		}
		
	}

	private void init() {
		FrameLayout id_fl_base = (FrameLayout)findViewById(R.id.id_fl_base);
		id_fl_base.setLayoutParams(new LinearLayout.LayoutParams(DisplayInfoManager.getInstance(this).width, DisplayInfoManager.getInstance(this).width));
	}
	
	

	private void executeDisplay(){

		DisplayVO vo = null;
		Setting setting = new Setting(this);
		String[] list = getResources().getStringArray(R.array.display_list_all);
		setting.debugDisplayInfo();
//		int cellWidth = DisplayInfoManager.getInstance(this).cellWidth;
//		int cellHeight = DisplayInfoManager.getInstance(this).cellHeight;

//		Log.d("jiho", "cellWidth : "+cellWidth+", cellHeight : "+cellHeight);

		for ( String item : list ){
			vo = setting.getDisplayInfo(item);
//			vo.left = vo.minIndex % DisplayInfoManager.CELL_COLS * cellWidth;
//			vo.top = vo.minIndex / DisplayInfoManager.CELL_ROWS * cellHeight;
//			vo.right = (vo.maxIndex % DisplayInfoManager.CELL_COLS + 1) * cellWidth;
//			vo.bottom = (vo.maxIndex / DisplayInfoManager.CELL_ROWS + 1) * cellHeight;
			drawDisplay(vo);
			vo.debug();
		}
	}
	
	private void drawDisplay(DisplayVO vo){
		if ( vo.minIndex == vo.maxIndex ){
			return;
		}
		int itemWidth = vo.right - vo.left;
		int itemHeight = vo.bottom - vo.top;
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(itemWidth, itemHeight);
		params.setMargins(vo.left, vo.top, 0, 0);

		TextView tv = new TextView(this);	
		tv.setText(vo.itemName);
		tv.setTag(vo.itemName);
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.stroke));
		tv.setLayoutParams(params);
		addContentView(tv, params);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		((TextView)findViewById(R.id.id_tv_error)).setText("");
		
		if ( isItem(v) ){
			toggleItem(v);
		}

		if ( v.getId() == R.id.id_tv_add ){
		}else if( v.getId() == R.id.id_tv_clear ){
			deleteItem();

		}else if( v.getId() == R.id.id_tv_clear_all ){
		}
	}

	private void toggleItem(View v) {
		// TODO Auto-generated method stub
		
	}

	private boolean isItem(View v) {
		String tag = (String)v.getTag();
		String[] list = getResources().getStringArray(R.array.display_list_all);
		for ( String item : list ){
			if( tag.equals(item) ){
				return true;
			}
		}
		return false;
	}

	private void deleteItem() {
		// TODO Auto-generated method stub
		
	}

}
