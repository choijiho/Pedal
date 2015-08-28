package net.gringrid.pedal.activity;

import java.util.ArrayList;
import java.util.Vector;

import net.gringrid.pedal.R;
import net.gringrid.pedal.SharedData;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class DisplayTest extends Activity implements OnClickListener {
	// 1. 현재속도
	// 2. 최고속도
	// 3. 평균속도 
	// 4. 라이딩시간
	// 5. 경과시간
	// 6. 거리
	
	// 7. 시간
	// 8. 날짜
	// 9. 배터리
	
	// 8 * 8 = 64
	// 64 / 4 = 16

//	세로가 가로보다 길 수 없다.
//	사각형이 되어야 한다.
//	(가장 작은 index ~ 가장 큰 index 사이에 모든 것이 선택 되어야 함.)
//
//
//	선택 가능한 기능
//	항목
//		시간, 날짜 배터리는 세로 한줄 가능 나머지는 2줄 이상
//	폰트크기
//	정렬
	final int mCols = 8;
	final int mRows = 8;
	final int mCellCount = mCols*mRows;
	TextView[] mCells = new TextView[mCellCount];
	private int mWidth;
	private int mHeight;
	private int mCellWidth;
	private int mCellHeight;
	
	final int INDEX = 0;
	final int REMAINDER = 1;
	final int QUATIENT = 2;
	final int MATRIX_INFO_LENGTH = 3;

	private String[] mDisplayListRiding;
	private int[] mMatrixMin = new int[MATRIX_INFO_LENGTH];
	private int[] mMatrixMax = new int[MATRIX_INFO_LENGTH];
	
	private int mFirstCell = Integer.MAX_VALUE;
	
	final int TAG_KEY_INDEX = 0;
	final int TAG_KEY_IS_SELECTED = 1;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_display_test);	
	    setDisplayInfo();
	    init();
	    registEvent();
	}

	private void setDisplayInfo() {
		Display display = getWindowManager().getDefaultDisplay(); 
		mWidth = display.getWidth();
		mHeight = display.getHeight();
		mCellWidth = mWidth / mCols;
		mCellHeight = mWidth / mRows;
	}

	private void init() {
		int cellIdx = 0;
		for ( int i=0; i<8; i++ ){
			int id = getResources().getIdentifier("id_ll_"+i, "id", getPackageName());
			for ( int j=0; j<8; j++ ){
				TagData tagData = new TagData(cellIdx);
				TextView tv = createCell();
				tv.setText(String.valueOf(cellIdx));
				tv.setTag(tagData);
				mCells[cellIdx++] = tv;
				((LinearLayout)findViewById(id)).addView(tv);
			}
		}
		mDisplayListRiding = getResources().getStringArray(R.array.display_list);
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
	
	private void refresh(){
		finish();
		startActivity(getIntent());
	}

	private TextView createCell(){
		TextView tv = new TextView(this);
		tv.setLayoutParams(new LayoutParams(mCellWidth, mCellHeight));
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.stroke));
		tv.setOnClickListener(this);
		return tv;
	}

	@Override
	public void onClick(View v) {
		
		((TextView)findViewById(R.id.id_tv_error)).setText("");
		
		if ( v.getId() == R.id.id_tv_add ){
			if ( checkValid() ){
				showDisplayList();
			}
		}else if( v.getId() == R.id.id_tv_clear ){
			deSelectAllCell();

		}else if( v.getId() == R.id.id_tv_clear_all ){
			refresh();
		}else{
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

	private void showDisplayList() {
		Log.d("jiho", "showDisplayList");
		AlertDialog.Builder builder = new Builder(this);
		builder.setItems(mDisplayListRiding, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 입력값 저장
				String[] displayList = getResources().getStringArray(R.array.display_list_all);
				SharedData.getInstance(DisplayTest.this).insertGlobalData(displayList[which]+"_min", mMatrixMin[INDEX]);	
				SharedData.getInstance(DisplayTest.this).insertGlobalData(displayList[which]+"_max", mMatrixMax[INDEX]);	

				// TODO 해당영역 세팅
				int areaWidth = (mMatrixMax[REMAINDER] - mMatrixMin[REMAINDER] + 1) * mCellWidth;
				int areaHeight = (mMatrixMax[QUATIENT] - mMatrixMin[QUATIENT] + 1) * mCellHeight;
				int marginTop = mMatrixMin[QUATIENT] * mCellHeight;
				int marginLeft = mMatrixMin[REMAINDER] * mCellWidth;
				
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(areaWidth, areaHeight);
				params.setMargins(marginLeft, marginTop, 0, 0);

				TextView tv = new TextView(DisplayTest.this);	
				tv.setText(mDisplayListRiding[which]);
				tv.setBackgroundColor(Color.CYAN);
				tv.setLayoutParams(params);
				addContentView(tv, params);
				setUsedCell();
				deSelectAllCell();
			}
		});
		builder.show();
		
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
	
	private void setUsedCell(){
		for ( TextView tv : mCells ){
			if ( ((TagData)tv.getTag()).isSelected ){
				((TagData)tv.getTag()).isUsed = true;
			}
		}
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
		// 사각형여부 체크
		if ( !isSquare() ){
			id_tv_error.setText(R.string.error_not_square);
			return false;
		}
		
		// 가로가 세로길이 비교
		int width = mMatrixMax[REMAINDER] - mMatrixMin[REMAINDER];
		int length = mMatrixMax[QUATIENT] - mMatrixMin[QUATIENT];
		if ( width < length ){
			id_tv_error.setText(R.string.error_width_short);
			return false;
		}
		
		return true;
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

	private boolean isSquare(){
		if ( mMatrixMin[INDEX] % mCols > mMatrixMin[REMAINDER] ){
			return false;
		}
		
		if ( mMatrixMax[INDEX] % mCols < mMatrixMax[REMAINDER] ){
			return false;
		}
		
		for ( int i=mMatrixMin[INDEX]; i<mMatrixMax[INDEX]; i++ ){
			if ( i % mCols >= mMatrixMin[REMAINDER] && i % mCols <= mMatrixMax[REMAINDER] ){
				if ( ((TagData)mCells[i].getTag()).isSelected == false ){
					Log.d("jiho", "i : "+i);
					return false;
				}
			}
		}
		return true;
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
