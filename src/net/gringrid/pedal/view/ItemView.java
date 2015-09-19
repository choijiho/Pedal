package net.gringrid.pedal.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

public abstract class ItemView extends FrameLayout{
	TextView mTitleView;
	TextView mContentView;

	public ItemView(Context context) {
		super(context);
		drawTitleView();
		drawContentView();
	}
	
	public void setTitle(String title){
		mTitleView.setText(title);
	}
	
	public void setContent(String content){
		mContentView.setText(content);
	}

	abstract void drawTitleView();
	abstract void drawContentView();
}
