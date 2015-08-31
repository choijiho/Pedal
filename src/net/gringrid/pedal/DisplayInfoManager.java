package net.gringrid.pedal;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Display;

public class DisplayInfoManager {
	private static Context mContext;

	public int width;
	public int height;
	private int cellWidth;
	private int cellHeight;
	public static final int CELL_COLS = 8;
	public static final int CELL_ROWS = 8;
	
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
	}
	
	public int getCellWidth(){
		return cellWidth;
	}

	public int getCellHeight(){
		return cellHeight;
	}


	
}

