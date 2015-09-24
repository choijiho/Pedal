package net.gringrid.pedal;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Display;

public class DisplayInfoManager {
	private static Context mContext;

	public static final int INDEX_LENGTH = 10;
	public static final int INDEX_CURRENT_SPPED = 0;
	public static final int INDEX_AVG_SPEED = 1;
	public static final int INDEX_MAX_SPEED = 2;
	public static final int INDEX_DISTANCE = 3;
	public static final int INDEX_ALTITUDE = 4;
	public static final int INDEX_TRAVEL_TIME = 5;
	public static final int INDEX_RIDING_TIME = 6;
	public static final int INDEX_PRESENT_TIME = 7;
	public static final int INDEX_DATE = 8;
	public static final int INDEX_BATTERY = 9;
	
	public static final int[] ItemTextViewList = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	public static final int[] ItemChronometerList = {7};
	
	public int width;
	public int height;
	private int cellWidth;
	private int cellHeight;
	public static final int CELL_COLS = 8;
	public static final int CELL_ROWS = 8;
	private String[] ridingInfoList;
	private static DisplayInfoManager instance;
	private DisplayInfoManager(){
		init();
	}
	
	public static DisplayInfoManager getInstance(Context context){
		mContext = context;
		if ( instance == null ){
			instance = new DisplayInfoManager();
		}
		return instance;
	}
	
	private void init() {
		Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay(); 

		width = display.getWidth();
		height = width;

		cellWidth = width / CELL_COLS;
		cellHeight = height / CELL_ROWS;
		ridingInfoList = mContext.getResources().getStringArray(R.array.riding_infomation_list);
	}
	
	public int getCellWidth(){
		return cellWidth;
	}

	public int getCellHeight(){
		return cellHeight;
	}
	
	public String getViewType(String itemName){
		// TODO
		return "";
	}
	
	public String[] getRidingInfoList(){
		return ridingInfoList;
	}

	
	public float getFontSize(float width){
		// TODO
		return 0;
	}

	
}

