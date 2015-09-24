package net.gringrid.pedal.db.vo;

import net.gringrid.pedal.R;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DisplayVO {
	public String itemName;
	public int minIndex;
	public int maxIndex;
	public int titleFontSize;
	public int contentFontSize;
	public int unitFontSize;
	
	public int top;
	public int left;
	public int right;
	public int bottom;
	
	public boolean isSelected;
	public boolean isUsed;
	
	// Draw Information
	public String viewType;
	public String unit;
	public FrameLayout.LayoutParams params;

	public void debug(){
		Log.d("jiho", itemName+" : ("+minIndex+", "+maxIndex+", "+viewType+")");
		Log.d("jiho", itemName+" : ("+left+", "+top+", "+right+", "+bottom+")");
		Log.d("jiho", itemName+" : ("+titleFontSize+", "+contentFontSize+", "+unitFontSize+","+isUsed+")");
	}
}
