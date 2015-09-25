package net.gringrid.pedal.view;

import net.gringrid.pedal.R;
import net.gringrid.pedal.db.vo.DisplayVO;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemView extends FrameLayout{
	Context mContext;

	LinearLayout 	mLayout;
	LinearLayout 	mContentLayout;
	TextView 		mTitleView;
	TextView 		mContentView;
	TextView 		mUnitView;
	DisplayVO 		mVo;

	public ItemView(Context context, DisplayVO data) {
		super(context);
		Log.d("jiho", "ItemView ItemView");
		Log.d("jiho", "titleFontSize : "+data.titleFontSize);
		Log.d("jiho", "contentFontSize : "+data.contentFontSize);
		Log.d("jiho", "unitFontSize : "+data.unitFontSize);
		data.titleFontSize = 13;
		data.contentFontSize = 55;
		data.unitFontSize = 15;
				
		mContext = context;
		setData(data);
	}
	
	public void setContent(String content){
		mContentView.setText(content);
	}
	
	public void setData(DisplayVO data){
		mVo = data;
		drawItem();
	}

	public View getView(){
		return mLayout;
	}

	private void drawItem(){
		removeAllViews();
		drawLayout();
		drawTitleView();
		drawContentView();
		doAssemble();
	}

	private void drawLayout(){
		mLayout = new LinearLayout(mContext);
		mLayout.setOrientation(LinearLayout.VERTICAL);
	}

	private void drawTitleView(){
		mTitleView = new TextView(mContext);	
		mTitleView.setText(mVo.itemName);
		mTitleView.setTextSize(mVo.titleFontSize);
		mTitleView.setTag(mVo);
//		mTitleView.setBackgroundResource(R.drawable.item_unselected);
		mTitleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	private void drawContentView(){
		Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay(); 
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		int itemWidth = mVo.right - mVo.left;
		int itemHeight = mVo.bottom - mVo.top;
		
		float densityWidth = (float)itemWidth / screenWidth;
		float densityHeight = (float)itemHeight / screenHeight;
		float contentTextSize = densityHeight * 240;
		float unitTextSize = densityHeight * 45;
		float additionalWidth = densityWidth / 10;
		
		Log.d("jiho", "screenWidth : "+screenWidth);
		Log.d("jiho", "screenHeight: "+screenHeight);
		Log.d("jiho", "densityWidth : "+densityWidth);
		Log.d("jiho", "densityHeight : "+densityHeight);
		Log.d("jiho", "contentTextSize : "+contentTextSize);
		Log.d("jiho", "unitTextSize : "+unitTextSize);
		Log.d("jiho", "(int)(densityHeight*100) : "+(int)(densityHeight*100));
		Log.d("jiho", "additionalWidth : "+additionalWidth);
		
		mContentLayout = new LinearLayout(mContext);
		mContentLayout.setOrientation(LinearLayout.HORIZONTAL);
        mContentLayout.setBackgroundResource(R.drawable.item_unselected);
        mContentLayout.setPadding(0, 0, 0, 0);
		mContentLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mContentLayout.setGravity(Gravity.BOTTOM);
		
		mContentView = new TextView(mContext);
		mContentView.setText("00.0");
		mContentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentTextSize);
		mContentView.setBackgroundResource(R.drawable.item_selected);
		mContentView.setPadding(0, 0, 0, 0);
		mContentView.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
		mContentView.setLayoutParams(new android.widget.LinearLayout.LayoutParams((int)(itemWidth * (0.7f+additionalWidth)), LayoutParams.WRAP_CONTENT));
		
		mUnitView = new TextView(mContext);
		mUnitView.setText("km/h");
		mUnitView.setTextSize(TypedValue.COMPLEX_UNIT_SP, unitTextSize);
        mUnitView.setBackgroundResource(R.drawable.item_selected);
        mUnitView.setPadding(0, 0, 0, 0);
        mUnitView.setGravity(Gravity.BOTTOM | Gravity.LEFT);
		mUnitView.setLayoutParams(new android.widget.LinearLayout.LayoutParams((int)(itemWidth * (1-(0.7f+additionalWidth))), LayoutParams.WRAP_CONTENT));
	
		mContentLayout.addView(mContentView);
		mContentLayout.addView(mUnitView);
	}
	
	private void doAssemble(){
		
		Log.d("jiho", "doAssemble");
		int itemWidth = mVo.right - mVo.left;
		int itemHeight = mVo.bottom - mVo.top;

		mVo.params = new FrameLayout.LayoutParams(itemWidth, itemHeight);
		mVo.params.setMargins(mVo.left, mVo.top, 0, 0);
		
        Log.d("jiho", "itemwidth : " + itemWidth);
        LinearLayout marginTop = new LinearLayout(mContext);
        marginTop.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
        LinearLayout marginBottom = new LinearLayout(mContext);
        marginBottom.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
      
		mLayout.addView(mTitleView);
		mLayout.addView(mContentLayout);
		
		setLayoutParams(mVo.params);
		addView(mLayout);
	}
	
}
