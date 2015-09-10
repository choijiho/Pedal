package net.gringrid.pedal.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import net.gringrid.pedal.R;
import net.gringrid.pedal.RidingInfoUtility;
import net.gringrid.pedal.StravaTasks;
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
import android.text.TextUtils;
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
		
		for ( RideVO vo : mRideVOList ){
//			if ( TextUtils.isEmpty(vo.maxSpeed) || vo.maxSpeed.equals("null") ){
				ArrayList<String> detailInfo = new ArrayList<String>();
				RidingInfoUtility ridingInfoUtility = new RidingInfoUtility(this);
				detailInfo = ridingInfoUtility.calculateRideInfo(vo.primaryKey);
				
				vo.avgSpeed = detailInfo.get(RidingInfoUtility.INDEX_AVG_SPEED);
				vo.distance = detailInfo.get(RidingInfoUtility.INDEX_DISTANCE);
				vo.maxSpeed = detailInfo.get(RidingInfoUtility.INDEX_MAX_SPEED);
				vo.ridingTime = detailInfo.get(RidingInfoUtility.INDEX_RIDING_TIME);
				int result = mRideDao.update(vo);
				Log.d("jiho", "update result = "+result);
			}
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
				// TODO 
				// 1. strava 인증여부 체크
				// 2. 해당 스트라바 ID가 DB에 있는경우 스트라바 상태 체크
				RideVO vo = (RideVO)mAdapter.getGroup(groupPosition);
				if ( !TextUtils.isEmpty(vo.stravaId) && !vo.stravaId.equals("null") ){
					RidingDetailTask task = new RidingDetailTask();
					task.execute(groupPosition, Integer.parseInt(vo.stravaId));
				}
//				showDetailInfo(groupPosition);
//				id_lv_list.setSelectedGroup(groupPosition);
			}
		});
	}
	
	class RidingDetailTask extends AsyncTask<Integer, String, Integer>{

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(mContext.getResources().getString(R.string.progress_loading));
			mProgressDialog.show();
			mProgressDialog.setMax(100);
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			StravaTasks stravaTasks = new StravaTasks(mContext);
			JSONObject jsonObject = stravaTasks.checkUploadStatus(String.valueOf(params[1]));
			try {
				String error = jsonObject.getString("error");
				String status = jsonObject.getString("status");
				String[] values = {String.valueOf(params[0]), String.valueOf(params[1]), error, status};
				publishProgress(values);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			
//			
//			publishProgress("30", "Loading VO VO");
//			
//			String detailInfo[] = new String[INDEX_LENGTH];
//			RidingInfoUtility ridingInfoUtility = new RidingInfoUtility(ExpandableRidingListActivity.this);
//			ridingInfoUtility.calculateRideInfo(vo.primaryKey, detailInfo);
//			publishProgress("80", "Calculate Riding Info");
//			
//			vo.distance = detailInfo[INDEX_DISTANCE]+"Km";
//			vo.ridingTime = Utility.getInstance().convertSecondsToHours(Long.parseLong(detailInfo[INDEX_TIME]));
//			vo.avgSpeed = detailInfo[INDEX_AVG_SPEED]+"Km/h";
//			vo.maxSpeed = detailInfo[INDEX_MAX_SPEED]+"Km/h";
//			vo.isShowDetail = true;
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			RideVO vo = (RideVO)mAdapter.getGroup(Integer.parseInt(values[0]));
			vo.stravaStatus = values[3];
			Log.d("jiho", "vo.stravaStatus : "+vo.stravaStatus);
			mAdapter.notifyDataSetChanged();
//			mProgressDialog.setProgress(Integer.parseInt(values[0]));
//			mProgressDialog.setMessage(values[1]);
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
