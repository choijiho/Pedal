package net.gringrid.pedal.activity;

import java.util.List;

import net.gringrid.pedal.R;
import net.gringrid.pedal.adapter.RideListAdapter;
import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.RideDao;
import net.gringrid.pedal.db.vo.RideVO;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class RideListActivity extends Activity implements OnItemClickListener{

	RideDao mRideDao;
	List<RideVO>	mRideVOList;
	ListView id_lv_list;
	RideListAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_ride_list);
		initView();
		mRideDao = RideDao.getInstance(DBHelper.getInstance(this));
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		mRideVOList = mRideDao.findAll();
		Log.d("jiho", "mRideVOList : "+mRideVOList.size());
		mAdapter = new RideListAdapter(this, R.layout.row_ride, mRideVOList);
		id_lv_list.setAdapter(mAdapter);
		super.onResume();
	}
	
	private void initView(){
		id_lv_list = (ListView)findViewById(R.id.id_lv_list);
		id_lv_list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		RideVO vo = mAdapter.getItem(position);
		Intent intent = new Intent(this, GpsLogListActivity.class);
		intent.putExtra("PARENT_ID", vo.primaryKey);
		startActivity(intent);
	}

	
}
