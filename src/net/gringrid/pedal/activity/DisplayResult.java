package net.gringrid.pedal.activity;

import java.util.Vector;

import net.gringrid.pedal.DisplayInfoManager;
import net.gringrid.pedal.R;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.db.vo.DisplayVO;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayResult extends Activity implements OnClickListener{
	/**
	 * Riding information items
	 */
	private Vector<TextView> mRidingInfoViews;
	/**
	 * Riding information display area
	 */
	private FrameLayout id_fl_riding_info_area;

	/**************************************************************************
	 * CELL Values
	 *************************************************************************/
	private int mCellCount = DisplayInfoManager.CELL_COLS * DisplayInfoManager.CELL_ROWS;
	private TextView[] mCells;
	private OnClickListener cellOnClickListener;
	private int mFirstSelectedCell = Integer.MAX_VALUE;
	private int mCols;
	private int mRows;
	
	
	final int INDEX = 0;
	final int LEFT_MARGIN  = 1;
	final int TOP_MARGIN = 2;
	final int MATRIX_INFO_LENGTH = 3;
	private int[] mMatrixMin = new int[MATRIX_INFO_LENGTH];
	private int[] mMatrixMax = new int[MATRIX_INFO_LENGTH];
	private String[] mRidingInfoList;
	

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
	

	private void init() {
		mRidingInfoViews = new Vector<TextView>();
		mRidingInfoList = getResources().getStringArray(R.array.riding_infomation_list);
		id_fl_riding_info_area = (FrameLayout)findViewById(R.id.id_fl_riding_info_area);
		id_fl_riding_info_area.setLayoutParams(new LinearLayout.LayoutParams(DisplayInfoManager.getInstance(this).width, DisplayInfoManager.getInstance(this).width));
		mCells = new TextView[mCellCount];
		mCols = DisplayInfoManager.CELL_COLS;
		mRows = DisplayInfoManager.CELL_ROWS;
		
		cellOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				((TextView)findViewById(R.id.id_tv_error)).setText("");
				TagData tagData = (TagData)v.getTag();
				int idx =  tagData.index;
				boolean isUsed = tagData.isUsed;
				int selectedCount = getSelectedCellCount();
				
				
				// 이미 사용되고 있는 cell인경우 
				if ( isUsed ){
					((TextView)findViewById(R.id.id_tv_error)).setText(R.string.error_used_cell);	
					return;
				}

				// 선택된 cell이 없는경우
				if ( selectedCount == 0 ){
					// TODO 색상 별도 표시
					selectCell(v);
					mFirstSelectedCell = idx;

				// 선택된 cell 이 하나 있는경우
				}else if ( selectedCount == 1 ){
					if ( idx ==  mFirstSelectedCell ){
						deSelectCell(v);
						mFirstSelectedCell = Integer.MAX_VALUE;
					}else{
						// 첫번째 cell과 선택한 Cell의 사각형 영역 선택
						selectCell(v);
						selectSquare();
					}

				// 이미 선택된 사각영역이 있는경우
				}else if ( selectedCount > 1 ){
					// 첫번째 선택한 cell 일경우 전부 선택 해제
					if ( idx == mFirstSelectedCell ){
						deSelectAllCell();
					}else{
					// 첫번째 cell과 선택한 cell의 사각형 영역 선택
						deSelectAllCell();
						selectCell(mFirstSelectedCell);
						selectCell(v);
						selectSquare();
					}
				}
			}
		};
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
	
	private void executeDisplay(){
		// Base cell을 그린다.
		drawBaseCells();
		
		// Riding information을 그린다. 
		DisplayVO vo = null;
		Setting setting = new Setting(this);
		String[] list = getResources().getStringArray(R.array.riding_infomation_list);
		setting.debugDisplayInfo();

		for ( String item : list ){
			vo = setting.getDisplayInfo(item);
			drawRidingItems(vo);
			// TODO DEBUG
			vo.debug();
		}
	}
	
	private void drawBaseCells() {
		
		final int cellWidth = DisplayInfoManager.getInstance(this).getCellWidth();
		final int cellHeight = DisplayInfoManager.getInstance(this).getCellHeight();
		
		for ( int i=0; i<mCellCount; i++ ){
			TagData tagData = new TagData(i);
			TextView tv = createCell(i);
			tv.setText(String.valueOf(i));
			tv.setTag(tagData);
//			tv.setOnClickListener(cellOnClickListener);
			
			tv.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// x, y 좌표로 현재 cell의 위치를 알아내야 함
					float col = event.getRawX() / cellWidth;
					float row = event.getRawY() / cellHeight - 1;
					
					int idx = (int)row * mCols + (int)col;
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						deSelectAllCell();
						selectCell(v);
						mFirstSelectedCell = idx;	
						break;
						
					case MotionEvent.ACTION_MOVE:
						if ( idx > mCellCount ) break;
						
						Log.d("jiho", "col : "+col+", row : "+row+", idx : "+idx);
						deSelectAllCell();
						selectCell(mFirstSelectedCell);
						selectCell(mCells[idx]);
						selectSquare();
						break;

					default:
						break;
					}
					return false;
				}
			});
			mCells[i] = tv;
			
			int leftMargin = cellWidth * (i % DisplayInfoManager.CELL_COLS);
			int topMargin = cellHeight * (i / DisplayInfoManager.CELL_ROWS);
		
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(cellWidth, cellHeight);
			params.setMargins(leftMargin, topMargin, 0, 0);
			addContentView(tv, params);
		}
	}

	private void drawRidingItems(DisplayVO vo){
		if ( vo.minIndex == vo.maxIndex ){
			return;
		}
		
		int itemWidth = vo.right - vo.left;
		int itemHeight = vo.bottom - vo.top;
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(itemWidth, itemHeight);
		params.setMargins(vo.left, vo.top, 0, 0);

		TextView tv = new TextView(this);	
		tv.setText(vo.itemName);
		tv.setTag(vo);
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_unselected));
		tv.setLayoutParams(params);
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				itemToggle(v);
			}
		});
		mRidingInfoViews.add(tv);
		addContentView(tv, params);
		// 사용된 셀로 설정
		setUsedValue(vo.minIndex, vo.maxIndex, true);
	}
	
	private TextView createCell(int i){
		TextView tv = new TextView(this);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.stroke));
		tv.setOnClickListener(this);
		return tv;
	}
	
	private void itemToggle(View v){
		DisplayVO vo = (DisplayVO)v.getTag();
		boolean isActionSelect = !vo.isSelected;

		for ( TextView innerTv : mRidingInfoViews ){
			DisplayVO innerVO = (DisplayVO)innerTv.getTag();
			innerVO.isSelected = false;
			innerTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_unselected));
		}

		if ( isActionSelect ){ 
			v.setBackgroundDrawable(getResources().getDrawable(R.drawable.item_selected));
			vo.isSelected = true;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		((TextView)findViewById(R.id.id_tv_error)).setText("");
		
		if ( v.getId() == R.id.id_tv_add ){
			if ( checkValid() ){
				showDisplayList();
			}	
		}else if( v.getId() == R.id.id_tv_clear ){
			deleteItem();

		}else if( v.getId() == R.id.id_tv_clear_all ){
		}
	}
	
	private void showDisplayList() {
		Log.d("jiho", "showDisplayList");
		AlertDialog.Builder builder = new Builder(this);
		builder.setItems(mRidingInfoList, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 입력값 저장
				String[] displayList = getResources().getStringArray(R.array.riding_infomation_list);
				
				DisplayVO vo = new DisplayVO();
				vo.itemName = displayList[which];
				vo.minIndex = mMatrixMin[INDEX];
				vo.maxIndex = mMatrixMax[INDEX];
				
				Setting setting = new Setting(DisplayResult.this);
				setting.setDisplayInfo(vo);
				vo = setting.getDisplayInfo(vo.itemName);
				drawRidingItems(vo);
				setUsedValue(vo.minIndex, vo.maxIndex, true);
				mFirstSelectedCell = Integer.MAX_VALUE;
				deSelectAllCell();
			}
		});
		builder.show();
		
	}
	
	private boolean checkValid() {
		//	세로가 가로보다 길 수 없다.
		//	사각형이 되어야 한다.
		TextView id_tv_error = (TextView)findViewById(R.id.id_tv_error);
		id_tv_error.setText("");	

		getMatrixMin(mMatrixMin);
		getMatrixMax(mMatrixMax);
		
		Log.d("jiho", "width : "+(mMatrixMax[LEFT_MARGIN] - mMatrixMin[LEFT_MARGIN]));
		Log.d("jiho", "height : "+(mMatrixMax[TOP_MARGIN] - mMatrixMin[TOP_MARGIN]));
		
		// 가로가 세로길이 비교
		int width = mMatrixMax[LEFT_MARGIN] - mMatrixMin[LEFT_MARGIN];
		int length = mMatrixMax[TOP_MARGIN] - mMatrixMin[TOP_MARGIN];
		if ( width < length ){
			id_tv_error.setText(R.string.error_width_short);
			return false;
		}
		
		return true;
	}
	
	private void deleteItem() {
		for ( TextView tv : mRidingInfoViews ){
			DisplayVO vo = (DisplayVO)tv.getTag();
			if ( vo.isSelected ){
				((ViewGroup)tv.getParent()).removeView(tv);
				setUsedValue(vo.minIndex, vo.maxIndex, false);
				setSelectedValue(vo.minIndex, vo.maxIndex, false);
				Setting setting = new Setting(this);
				setting.clearDisplayInfo(vo);

				return;
			}
		}
	}

	private void deSelectAllCell(){
		for ( TextView view : mCells ){
			deSelectCell(view);
		}
	}

	private void deSelectCell(View view){
		((TagData)view.getTag()).isSelected = false;
		view.setBackgroundDrawable(getResources().getDrawable(R.drawable.stroke));
	}

	private void selectCell(View view){
		((TagData)view.getTag()).isSelected = true;
		view.setBackgroundColor(Color.LTGRAY);
	}
	
	private void selectCell(int idx){
		for ( TextView tv : mCells ){
			if ( ((TagData)tv.getTag()).index == idx ){
				selectCell(tv);
				return;
			}
		}
	}
	
	private int getSelectedCellCount(){
		int result = 0;
		for ( TextView tv : mCells ){
			if ( ((TagData)tv.getTag()).isSelected ){
				result++;
			}
		}
		return result;
	}

	private void selectSquare(){
		// TODO DEBUG
		getCellInfo();
		
		Vector<Integer> squareTarget = new Vector<Integer>();

		getMatrixMin(mMatrixMin);
		getMatrixMax(mMatrixMax);
		
		int minIndex = mCols * mMatrixMin[TOP_MARGIN] + mMatrixMin[LEFT_MARGIN];
		int maxIndex = mCols * mMatrixMax[TOP_MARGIN] + mMatrixMax[LEFT_MARGIN];
		
		for ( int i=minIndex; i<=maxIndex; i++ ){
			if ( i % mCols >= mMatrixMin[LEFT_MARGIN] && i % mCols <= mMatrixMax[LEFT_MARGIN] ){
				squareTarget.add(i);
			}
		}
		
		// 선택해야 하는 영역에 이미 사용된 영역이 있는지 체크
		for ( Integer targetIdx : squareTarget ){
			if ( ((TagData)mCells[targetIdx].getTag()).isUsed ){
				deSelectAllCell();
				selectCell(mCells[mFirstSelectedCell]);
				((TextView)findViewById(R.id.id_tv_error)).setText(R.string.error_used_cell);
				return;
			}
		}

		for ( int targetIdx : squareTarget ){
			selectCell(mCells[targetIdx]);
		}
	}
	
	
	private void setSelectedValue(int minIndex, int maxIndex, boolean isSelected) {
		for ( int i=minIndex; i<=maxIndex; i++ ){
			if ( i % mCols >= minIndex % mCols && i % mCols <= maxIndex % mCols ){
				deSelectCell(mCells[i]);
			}
		}
	}
	
	private void setUsedValue(int minIndex, int maxIndex, boolean isUsed){
		
		for ( int i=minIndex; i<=maxIndex; i++ ){
			if ( i % mCols >= minIndex % mCols && i % mCols <= maxIndex % mCols ){
				((TagData)mCells[i].getTag()).isUsed = isUsed;
			}
		}
	}

	private void getCellInfo(){

		String selectedIndex = "";
		String usedIndex = "";

		for ( TextView tv : mCells ){
			TagData tagData = (TagData)tv.getTag();
			if ( tagData.isSelected ){
				selectedIndex += "["+tagData.index+"]";
			}
			if ( tagData.isUsed){
				usedIndex += "["+tagData.index+"]";
			}
		}

		Log.d("jiho", "SELECTED CELL INDEX : "+selectedIndex);
		Log.d("jiho", "USED CELL INDEX : "+usedIndex);
	}

	private void getMatrixMin(int[] result){
		int minIdx = mCellCount;
		int minLeftMargin = mCols;
		int minTopMargin = mRows;
		
		for ( int i=0; i<mCellCount; i++ ){
			if ( ((TagData)mCells[i].getTag()).isSelected ){
				if ( minIdx == mCellCount ){
					minIdx = i;
				}
				if ( minLeftMargin > i % mCols ){
					minLeftMargin = i % mCols;
				}
				if ( minTopMargin > i / mRows){
					minTopMargin = i / mRows;
				}
			}
		}
		result[INDEX] = minIdx;
		result[LEFT_MARGIN] = minLeftMargin;
		result[TOP_MARGIN] = minTopMargin;
		Log.d("jiho", "min[INDEX] : "+result[INDEX]);
		Log.d("jiho", "min[LEFT_MARGIN] : "+result[LEFT_MARGIN]);
		Log.d("jiho", "min[TOP_MARGIN] : "+result[TOP_MARGIN]);
	}
	
	private void getMatrixMax(int[] result){
		int maxIdx = -1;
		int maxRemainder = 0;
		int maxQuotient = 0;
		
		for ( int i=mCellCount-1; i>0; i--){
			if ( ((TagData)mCells[i].getTag()).isSelected ){
				if ( maxIdx == -1 ){
					maxIdx = i;
				}
				if ( maxRemainder < i % mCols ){
					maxRemainder = i % mCols;
				}
				if ( maxQuotient < i / mRows ){
					maxQuotient = i / mRows;
				}
			}
		}
		result[INDEX] = maxIdx;
		result[LEFT_MARGIN] = maxRemainder;
		result[TOP_MARGIN] = maxQuotient;
		Log.d("jiho", "max[INDEX] : "+result[INDEX]);
		Log.d("jiho", "max[LEFT_MARGIN] : "+result[LEFT_MARGIN]);
		Log.d("jiho", "max[TOP_MARGIN] : "+result[TOP_MARGIN]);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if ( event.getAction() == MotionEvent.ACTION_MOVE ){
			
		}
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
	
	class TagData{
		public TagData(int index) {
			this.index = index;
			this.isSelected = false;
			this.isUsed = false;
		}
		public int index;
		public boolean isSelected;
		public boolean isUsed;
	}
}
