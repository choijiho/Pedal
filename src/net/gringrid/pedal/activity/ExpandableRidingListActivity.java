package net.gringrid.pedal.activity;

import java.text.DecimalFormat;
import java.util.List;

import net.gringrid.pedal.R;
import net.gringrid.pedal.Utility;
import net.gringrid.pedal.adapter.ExpandableRideListAdapter;
import net.gringrid.pedal.adapter.RideListAdapter;
import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.RideDao;
import net.gringrid.pedal.db.vo.GpsLogVO;
import net.gringrid.pedal.db.vo.RideVO;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListView;
import android.widget.Toast;

public class ExpandableRidingListActivity extends Activity{

	RideDao mRideDao;
	GpsLogDao mGpsLogDao;
	List<RideVO>	mRideVOList;
	ExpandableListView id_lv_list;
	ExpandableRideListAdapter mAdapter;
	ProgressDialog mProgressDialog;
	Context mContext;

	final int INDEX_LENGTH = 4;
	final int INDEX_DISTANCE = 0;
	final int INDEX_TIME = 1;
	final int INDEX_AVG_SPEED = 2;
	final int INDEX_MAX_SPEED = 3;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_expandable_ride_list);
		initView();
		mRideDao = RideDao.getInstance(DBHelper.getInstance(this));
		mGpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(this));
		mContext = this;
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		mRideVOList = mRideDao.findAll();
		Log.d("jiho", "mRideVOList : "+mRideVOList.size());
//		for ( RideVO vo : mRideVOList ){
//			calculateRideInfo(vo.primaryKey);
//		}
		mAdapter = new ExpandableRideListAdapter(this, mRideVOList);
		id_lv_list.setAdapter(mAdapter);
		super.onResume();
	}
	
	private void initView(){
		id_lv_list = (ExpandableListView)findViewById(R.id.id_lv_list);
		id_lv_list.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				Log.d("jiho", "onGroupClick");
				return false;
			}
		});
		
		id_lv_list.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
				Log.d("jiho", "onGroupCollapse");
				
			}
		});
		
		id_lv_list.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				showDetailInfo(groupPosition);
				id_lv_list.setSelectedGroup(groupPosition);
			}
		});
	}
	
	/**
	 * Riding Time
	 */
	private void calculateRideInfo(int parentId, String results[]){
		GpsLogDao gpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(this));
		List<GpsLogVO> gpsLogVOList = gpsLogDao.findWithParentId(parentId);
		GpsLogVO preVo = null;

		float avgSpeed = 0;
		long totalTime = 0;
		float totalDistance = 0;
		float[] distanceResult = new float[3];
		float tmpSpeed = 0;
		
		for ( GpsLogVO vo : gpsLogVOList){
			if ( preVo != null ){
				Location.distanceBetween(preVo.latitude, preVo.longitude, vo.latitude, vo.longitude, distanceResult);
				tmpSpeed = distanceResult[0] / (vo.gpsTime - preVo.gpsTime) * 1000;
				if ( tmpSpeed > 0.2f ) {
					totalTime += vo.gpsTime - preVo.gpsTime;
				}
				totalDistance += distanceResult[0];
//				Log.d("jiho", "Speed : "+tmpSpeed+", distanceResult[0] : "+distanceResult[0]+", time : "+(vo.gpsTime - preVo.gpsTime));
			}
			preVo = vo;
		}
		totalTime = totalTime / 1000;
		avgSpeed = totalDistance / totalTime * 3.6f;
		Log.d("jiho", "avgSpeed : "+avgSpeed);
		Log.d("jiho", "totalTime : "+totalTime);
		Log.d("jiho", "totalDistance : "+totalDistance);

		String printAvgSpeed = new DecimalFormat("0.0").format(avgSpeed)+"km/h";
		String printTime = Utility.getInstance().convertSecondsToHours(totalTime);
		String printDistance = String.format("%.1f", totalDistance / 1000)+"km";
		String printMaxSpeed = "";
		results[INDEX_DISTANCE] = printDistance;
		results[INDEX_TIME] = printTime;
		results[INDEX_AVG_SPEED] = printAvgSpeed;
		results[INDEX_MAX_SPEED] = printMaxSpeed;
	}

	private void showDetailInfo(int position){
		RidingDetailTask ridingDetailTask = new RidingDetailTask();
		ridingDetailTask.execute(position);
	}
	
	
	class RidingDetailTask extends AsyncTask<Integer, String, Integer>{

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMessage(mContext.getResources().getString(R.string.progress_loading));
			mProgressDialog.show();
			mProgressDialog.setMax(100);
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			RideVO vo = (RideVO)mAdapter.getGroup(params[0]);
			
			Log.d("jiho", "distance : "+vo.distance);
			Log.d("jiho", "ridingTime : "+vo.ridingTime);
			
			
			publishProgress("30", "Loading VO VO");
			
			String detailInfo[] = new String[INDEX_LENGTH];
			calculateRideInfo(vo.primaryKey, detailInfo);
			publishProgress("80", "Calculate Riding Info");
			
			vo.distance = detailInfo[INDEX_DISTANCE];
			vo.ridingTime = detailInfo[INDEX_TIME];
			vo.avgSpeed = detailInfo[INDEX_AVG_SPEED];
			vo.maxSpeed = detailInfo[INDEX_MAX_SPEED];
			vo.isShowDetail = true;
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			mProgressDialog.setProgress(Integer.parseInt(values[0]));
			mProgressDialog.setMessage(values[1]);
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			mAdapter.notifyDataSetChanged();
			mProgressDialog.dismiss();
			Toast.makeText(mContext, "END", Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}
		
	}
}
