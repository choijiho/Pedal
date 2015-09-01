package net.gringrid.pedal.activity;

import java.util.Vector;

import net.gringrid.pedal.DisplayInfoManager;
import net.gringrid.pedal.R;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.activity.DisplayTest.TagData;
import net.gringrid.pedal.db.vo.DisplayVO;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class DisplayResult extends Activity implements OnClickListener{
	
	private Vector<TextView> mDisplayItems;
	private FrameLayout id_fl_base;
	private int mCellCount = DisplayInfoManager.CELL_COLS * DisplayInfoManager.CELL_ROWS;
	private TextView[] mCells;
	private OnClickListener cellListener;
	private int mFirstCell = Integer.MAX_VALUE;
	final int INDEX = 0;
	final int REMAINDER = 1;
	final int QUATIENT = 2;
	final int MATRIX_INFO_LENGTH = 3;
	private int[] mMatrixMin = new int[MATRIX_INFO_LENGTH];
	private int[] mMatrixMax = new int[MATRIX_INFO_LENGTH];
	private int mCols;
	private int mRows;
	private String[] mDisplayListRiding;
	

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
		mDisplayItems = new Vector<TextView>();
		mDisplayListRiding = getResources().getStringArray(R.array.display_list);
		id_fl_base = (FrameLayout)findViewById(R.id.id_fl_base);
		id_fl_base.setLayoutParams(new LinearLayout.LayoutParams(DisplayInfoManager.getInstance(this).width, DisplayInfoManager.getInstance(this).width));
		mCells = new TextView[mCellCount];
		mCols = DisplayInfoManager.CELL_COLS;
		mRows = DisplayInfoManager.CELL_ROWS;
		
		cellListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((TextView)findViewById(R.id.id_tv_error)).setText("");
				int idx =  ((TagData)v.getTag()).index;
				boolean isUsed = ((TagData)v.getTag()).isUsed;
				int selectedCount = getSelectedCellCount();
				// 현재 선택된 Cell이 아무것도 없으면 선택(색상은 다르게)
				// 현재 선택된 Cell이 하나일경우
				//   선택된 Cell 을 다시 선택할 경우 선택해제
				//   다른 cell을 선택한경우
				//		기존에 세팅된 영역이면 아무 반응도 하지 않음 > 에러메시지 출력
				//		기존에 선택된 cell 과 선택한 cell의 사각형 영역을 모두 선택
				// 현재 선택된 cell 이 하나 이상일경우
				// 		처음 선택한 cell을 제외하고 모두 해재 + 처음선택한 cell과 선택한 cell의 사각형 영역을 모두 선택
				
				if ( isUsed ){
					((TextView)findViewById(R.id.id_tv_error)).setText(R.string.error_used_cell);	
					return;
				}
				
				// 선택된 cell이 없는경우
				if ( selectedCount == 0 ){
					selectCell(v);
					mFirstCell = idx;

				// 선택된 cell 이 하나 있는경우
				}else if ( selectedCount == 1 ){
					if ( idx ==  mFirstCell ){
						deSelectCell(v);
						mFirstCell = Integer.MAX_VALUE;
					}else{
						selectCell(v);
						selectSquare();
					}
				// 이미 선택된된 사각영역이 있는경우
				}else if ( selectedCount > 1 ){
					if ( idx == mFirstCell ){
						deSelectAllCell();
					}else{
						deSelectAllCell();
						selectCell(mFirstCell);
						selectCell(v);
						selectSquare();
					}
				}
			}
		};
	}
	
	private void executeDisplay(){
		// Base Line을 그린다.
		drawGrid();
		
		// 이미 세팅된 Item을 그린다.
		DisplayVO vo = null;
		Setting setting = new Setting(this);
		String[] list = getResources().getStringArray(R.array.display_list_all);
		setting.debugDisplayInfo();

		for ( String item : list ){
			vo = setting.getDisplayInfo(item);
			drawItems(vo);
			vo.debug();
		}
	}
	
	private void drawGrid() {
		int cellWidth = DisplayInfoManager.getInstance(this).getCellWidth();
		int cellHeight = DisplayInfoManager.getInstance(this).getCellHeight();

		for ( int i=0; i<mCellCount; i++ ){
			TagData tagData = new TagData(i);
			TextView tv = createCell(i);
			tv.setText(String.valueOf(i));
			tv.setTag(tagData);
			tv.setOnClickListener(cellListener);
			mCells[i] = tv;
			
			int leftMargin = cellWidth * (i % DisplayInfoManager.CELL_COLS);
			int topMargin = cellHeight * (i / DisplayInfoManager.CELL_ROWS);
		
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(cellWidth, cellHeight);
			params.setMargins(leftMargin, topMargin, 0, 0);
			addContentView(tv, params);
		}
	}

	private void drawItems(DisplayVO vo){
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
		mDisplayItems.add(tv);
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

		for ( TextView innerTv : mDisplayItems ){
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
		builder.setItems(mDisplayListRiding, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 입력값 저장
				String[] displayList = getResources().getStringArray(R.array.display_list_all);
				
				DisplayVO vo = new DisplayVO();
				vo.itemName = displayList[which];
				vo.minIndex = mMatrixMin[INDEX];
				vo.maxIndex = mMatrixMax[INDEX];
				
				Setting setting = new Setting(DisplayResult.this);
				setting.setDisplayInfo(vo);
				vo = setting.getDisplayInfo(vo.itemName);
				drawItems(vo);
				setUsedValue(vo.minIndex, vo.maxIndex, true);
				mFirstCell = Integer.MAX_VALUE;
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
		
		Log.d("jiho", "width : "+(mMatrixMax[REMAINDER] - mMatrixMin[REMAINDER]));
		Log.d("jiho", "height : "+(mMatrixMax[QUATIENT] - mMatrixMin[QUATIENT]));
		
		// 가로가 세로길이 비교
		int width = mMatrixMax[REMAINDER] - mMatrixMin[REMAINDER];
		int length = mMatrixMax[QUATIENT] - mMatrixMin[QUATIENT];
		if ( width < length ){
			id_tv_error.setText(R.string.error_width_short);
			return false;
		}
		
		return true;
	}
	
	private void deleteItem() {
		for ( TextView tv : mDisplayItems ){
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
		Vector<Integer> squareTarget = new Vector<Integer>();

		getMatrixMin(mMatrixMin);
		getMatrixMax(mMatrixMax);
		
		int minIndex = mCols * mMatrixMin[QUATIENT] + mMatrixMin[REMAINDER];
		int maxIndex = mCols * mMatrixMax[QUATIENT] + mMatrixMax[REMAINDER];
		
		for ( int i=minIndex; i<=maxIndex; i++ ){
			if ( i % mCols >= mMatrixMin[REMAINDER] && i % mCols <= mMatrixMax[REMAINDER] ){
				squareTarget.add(i);
			}
		}
		
		// 선택해야 하는 영역에 이미 사용된 영역이 있는지 체크
		for ( Integer targetIdx : squareTarget ){
			if ( ((TagData)mCells[targetIdx].getTag()).isUsed ){
				deSelectAllCell();
				selectCell(mCells[mFirstCell]);
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
	
	private void getMatrixMin(int[] result){
		int minIdx = mCellCount;
		int minRemainder = mCols;
		int minQuotient = mCols;
		
		for ( int i=0; i<mCellCount; i++ ){
			if ( ((TagData)mCells[i].getTag()).isSelected ){
				if ( minIdx == mCellCount ){
					minIdx = i;
				}
				if ( minRemainder > i % mCols ){
					minRemainder = i % mCols;
				}
				if ( minQuotient > i / mCols ){
					minQuotient = i / mCols;
				}
			}
		}
		result[INDEX] = minIdx;
		result[REMAINDER] = minRemainder;
		result[QUATIENT] = minQuotient;
		Log.d("jiho", "min[INDEX] : "+result[INDEX]);
		Log.d("jiho", "min[REMAINDER] : "+result[REMAINDER]);
		Log.d("jiho", "min[QUATIENT] : "+result[QUATIENT]);
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
				if ( maxQuotient < i / mCols ){
					maxQuotient = i / mCols;
				}
			}
		}
		result[INDEX] = maxIdx;
		result[REMAINDER] = maxRemainder;
		result[QUATIENT] = maxQuotient;
		Log.d("jiho", "max[INDEX] : "+result[INDEX]);
		Log.d("jiho", "max[REMAINDER] : "+result[REMAINDER]);
		Log.d("jiho", "max[QUATIENT] : "+result[QUATIENT]);
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
