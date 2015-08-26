package net.gringrid.pedal.activity;

import net.gringrid.pedal.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_display_test);	
	    setDisplayInfo();
	    init();
	    registEvent();
	}

	private void setDisplayInfo() {
		// TODO Auto-generated method stub
		Display display = getWindowManager().getDefaultDisplay(); 
		mWidth = display.getWidth();
		mHeight = display.getHeight();
		
		Log.d("jiho", "mWidth : "+mWidth);
		Log.d("jiho", "mHeight : "+mHeight);
		
	}

	private void init() {
		int cellIdx = 0;
		for ( int i=0; i<8; i++ ){
			int id = getResources().getIdentifier("id_ll_"+i, "id", getPackageName());
			for ( int j=0; j<8; j++ ){
				TextView tv = createCell();
				tv.setText(String.valueOf(cellIdx));
				mCells[cellIdx++] = tv;
				((LinearLayout)findViewById(id)).addView(tv);
			}
		}
	}
	
	private void registEvent() {
		View view = findViewById(R.id.id_tv_add);
		view.setOnClickListener(this);
	}

	private TextView createCell(){
		TextView tv = new TextView(this);
		tv.setLayoutParams(new LayoutParams(mWidth/8, mWidth/8));
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.stroke));
		tv.setOnClickListener(this);
		tv.setTag(false);
		return tv;
	}

	@Override
	public void onClick(View v) {
		if ( v.getId() == R.id.id_tv_add ){
			if ( checkValid() ){
				
			}
		}else{
			if ( (Boolean)v.getTag() == false ){
				v.setTag(true);
				v.setBackgroundColor(Color.LTGRAY);
			}else{
				v.setTag(false);
				v.setBackgroundDrawable(getResources().getDrawable(R.drawable.stroke));
			}
		}
	}

	private boolean checkValid() {
//		세로가 가로보다 길 수 없다.
//		사각형이 되어야 한다.
//		(가장 작은 index ~ 가장 큰 index 사이에 모든 것이 선택 되어야 함.)	
		// TODO Auto-generated method stub
		
		
		int[] matrixMin = new int[3];
		int[] matrixMax = new int[3];
		
		getMatrixMin(matrixMin);
		getMatrixMax(matrixMax);
		
		for(int min : matrixMin){
			Log.d("jiho", "min : "+min);
		}
		
		for(int max : matrixMax){
			Log.d("jiho", "max : "+max);
		}
		
		
//		
//		// 사각형여부 체크
//		if ( !isSquare() ){
//			Log.d("jiho", "not square");
//		}else{
//			Log.d("jiho", "good");
//		}
		
		// 가로가 세로길이 비교
		
		Log.d("jiho", "getSelectedMinQuotient : "+getSelectedMinQuotient());
		Log.d("jiho", "getSelectedMaxQuotient : "+getSelectedMaxQuotient());
		

		
		return false;
	}
	
	private void getMatrixMin(int[] result){
		int minIdx = mCellCount;
		int minRemainder = mCols;
		int minQuotient = mCols;
		
		for ( int i=0; i<mCellCount; i++ ){
			if ( (Boolean)mCells[i].getTag() ){
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
		result[0] = minIdx;
		result[1] = minRemainder;
		result[2] = minQuotient;
	}
	
	private void getMatrixMax(int[] result){
		int maxIdx = -1;
		int maxRemainder = 0;
		int maxQuotient = 0;
		
		for ( int i=mCellCount-1; i>0; i--){
			if ( (Boolean)mCells[i].getTag() ){
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
		result[0] = maxIdx;
		result[1] = maxRemainder;
		result[2] = maxQuotient;
	}
	
	private int getSelectedMinIndex(){
		int minIdx = 0;
		for ( int i=0; i<mCellCount; i++ ){
			if ( (Boolean)mCells[i].getTag() ){
				minIdx = i;
				break;
			}
		}
		return minIdx;
	}

	private int getSelectedMaxIndex(){
		int maxIdx = 0;
		for ( int i=mCellCount-1; i>0; i--){
			if ( (Boolean)mCells[i].getTag() ){
				maxIdx = i;
				break;
			}
		}
		return maxIdx;
	}
	
	private int getSelectedMinRemainder(){
		int minRemainder = mCols;
		for ( int i=0; i<mCellCount; i++ ){
			if ( (Boolean)mCells[i].getTag() ){
if ( minRemainder > i % mCols ){
					minRemainder = i % mCols;
				}
			}
		}
		return minRemainder;
	}

	private int getSelectedMaxRemainder(){
		int maxRemainder = 0;
		for ( int i=mCellCount-1; i>0; i--){
			if ( (Boolean)mCells[i].getTag() ){
				if ( maxRemainder < i % mCols ){
					maxRemainder = i % mCols;
				}
			}
		}
		return maxRemainder; 
	}
	
	private int getSelectedMinQuotient(){
		int minQuotient = mCols;
		for ( int i=0; i<mCellCount; i++ ){
			if ( (Boolean)mCells[i].getTag() ){
				if ( minQuotient > i / mCols ){
					minQuotient = i / mCols;
				}
			}
		}
		return minQuotient;
	}

	private int getSelectedMaxQuotient(){
		int maxQuotient = 0;
		for ( int i=mCellCount-1; i>0; i--){
			if ( (Boolean)mCells[i].getTag() ){
				if ( maxQuotient < i / mCols ){
					maxQuotient = i / mCols;
				}
			}
		}
		return maxQuotient; 
	}
	
	private boolean isSquare(){
		boolean result = true;
		int minIdx = getSelectedMinIndex();
		int maxIdx = getSelectedMaxIndex();
		
		int minCol = getSelectedMinRemainder();
		int maxCol = getSelectedMaxRemainder();
		
		Log.d("jiho", "minIdx : "+minIdx);
		Log.d("jiho", "maxIdx : "+maxIdx);
		Log.d("jiho", "minCol : "+minCol);
		Log.d("jiho", "maxCol : "+maxCol);

		if ( minIdx % mCols > minCol ){
			return false;
		}
		
		if ( maxIdx % mCols < maxCol ){
			return false;
		}
		
		
		for ( int i=minIdx; i<maxIdx; i++ ){
			if ( i % mCols >= minCol && i % mCols <= maxCol ){
				if ( (Boolean)mCells[i].getTag() == false ){
					Log.d("jiho", "i : "+i);
					return false;
				}
			}
		}
		return result;
	}
}
