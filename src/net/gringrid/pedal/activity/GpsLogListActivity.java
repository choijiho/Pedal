package net.gringrid.pedal.activity;

import java.util.List;

import net.gringrid.pedal.R;
import net.gringrid.pedal.adapter.GpsLogListAdapter;
import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.vo.GpsLogVO;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class GpsLogListActivity extends Activity{

	GpsLogDao mGpsLogDao;
	List<GpsLogVO>	mGpsLogVOList;
	ListView id_lv_list;
	GpsLogListAdapter mAdapter;
	int mParentId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_gps_log_list);
		initView();
		mGpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(this));
		mParentId = getIntent().getExtras().getInt("PARENT_ID");
		Log.d("jiho", "mparentId : "+mParentId);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		mGpsLogVOList = mGpsLogDao.findWithParentId(mParentId);
		Log.d("jiho", "mGpsLogVOList : "+mGpsLogVOList.size());
		mAdapter = new GpsLogListAdapter(this, R.layout.row_ride, mGpsLogVOList);
		id_lv_list.setAdapter(mAdapter);
		super.onResume();
	}
	
	private void initView(){
		id_lv_list = (ListView)findViewById(R.id.id_lv_list);
	}
}
