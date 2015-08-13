package net.gringrid.pedal.activity;

import java.util.List;

import net.gringrid.pedal.R;
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
import android.widget.ListView;

public class RidingListActivity extends Activity{

	RideDao mRideDao;
	GpsLogDao mGpsLogDao;
	List<RideVO>	mRideVOList;
	ListView id_lv_list;
	RideListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_ride_list);
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
		mAdapter = new RideListAdapter(this, R.layout.row_ride, mRideVOList);
		id_lv_list.setAdapter(mAdapter);
		super.onResume();
	}
	
	private void initView(){
		id_lv_list = (ListView)findViewById(R.id.id_lv_list);
	}

}
