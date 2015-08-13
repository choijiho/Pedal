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
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ListView;

public class ExpandableRidingListActivity extends Activity{

	RideDao mRideDao;
	GpsLogDao mGpsLogDao;
	List<RideVO>	mRideVOList;
	ExpandableListView id_lv_list;
	ExpandableRideListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_expandable_ride_list);
		initView();
		mRideDao = RideDao.getInstance(DBHelper.getInstance(this));
		mGpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(this));
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
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		id_lv_list.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
				Log.d("jiho", "onGroupCollapse");
				// TODO Auto-generated method stub
				
			}
		});
		
		id_lv_list.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				RideVO vo = (RideVO)mAdapter.getGroup(groupPosition);
				Log.d("jiho", "onGroupExpand");
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
