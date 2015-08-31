package net.gringrid.pedal.db.vo;

import android.util.Log;

public class DisplayVO {
	public String itemName;
	public int minIndex;
	public int maxIndex;
	public int titleFontSize;
	public int itemFontSize;
	public int unitFontSize;
	
	public int top;
	public int left;
	public int right;
	public int bottom;

	public void debug(){
		Log.d("jiho", itemName+" : ("+minIndex+", "+maxIndex+")");
		Log.d("jiho", itemName+" : ("+left+", "+top+", "+right+", "+bottom+")");
		Log.d("jiho", itemName+" : ("+titleFontSize+", "+itemFontSize+", "+unitFontSize+")");
	}
}
