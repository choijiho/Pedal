package net.gringrid.pedal.view;

import net.gringrid.pedal.R;
import net.gringrid.pedal.db.vo.DisplayVO;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
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
		data.contentFontSize = 20;
		data.unitFontSize = 11;
				
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
		mTitleView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_unselected));
		mTitleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));	
	}
	
	private void drawContentView(){
		mContentLayout = new LinearLayout(mContext);
		mContentLayout.setOrientation(LinearLayout.HORIZONTAL);
		mContentLayout.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_unselected));
		mContentLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
		
		mContentView = new TextView(mContext);
		mContentView.setText(mVo.itemName);
		mContentView.setTextSize(mVo.contentFontSize);
		mContentView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_unselected));
		mContentView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 7f));
		
		mUnitView = new TextView(mContext);
		mUnitView.setText(mVo.unit);
		mUnitView.setTextSize(mVo.unitFontSize);
		mUnitView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_unselected));
		mUnitView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 3f));
		
		mContentLayout.addView(mContentView);
		mContentLayout.addView(mUnitView);
	}
	
	private void doAssemble(){
		Log.d("jiho", "doAssemble");
		int itemWidth = mVo.right - mVo.left;
		int itemHeight = mVo.bottom - mVo.top;
		mVo.params = new FrameLayout.LayoutParams(itemWidth, itemHeight);
		mVo.params.setMargins(mVo.left, mVo.top, 0, 0);

		mLayout.addView(mTitleView);
		mLayout.addView(mContentLayout);
		
		setLayoutParams(mVo.params);
		addView(mLayout);
	}
	
}
