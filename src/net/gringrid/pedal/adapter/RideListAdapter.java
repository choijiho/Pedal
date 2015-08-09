package net.gringrid.pedal.adapter;

import java.text.DecimalFormat;
import java.util.List;

import net.gringrid.pedal.GPXMaker;
import net.gringrid.pedal.R;
import net.gringrid.pedal.Utility;
import net.gringrid.pedal.R.id;
import net.gringrid.pedal.R.layout;
import net.gringrid.pedal.UploadGpxFile;
import net.gringrid.pedal.activity.GpsLogListActivity;
import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.RideDao;
import net.gringrid.pedal.db.vo.GpsLogVO;
import net.gringrid.pedal.db.vo.RideVO;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class RideListAdapter extends ArrayAdapter<RideVO>{

	Context mContext;
	List<RideVO> data;
	
	public RideListAdapter(Context context, int resource, List<RideVO> objects) {
		super(context, resource, objects);
		mContext = context;
		data = objects;
	}
	
	/**
	 * Riding Time
	 */
	private String calculateRideInfo(int parentId){
		GpsLogDao gpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(mContext));
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
				Log.d("jiho", "Speed : "+tmpSpeed+", distanceResult[0] : "+distanceResult[0]+", time : "+(vo.gpsTime - preVo.gpsTime));
			}
			preVo = vo;
		}
		
		avgSpeed = totalDistance / totalTime / 1000 * 3.6f;
		Log.d("jiho", "avgSpeed : "+avgSpeed);
		Log.d("jiho", "totalTime : "+totalTime / 1000);
		Log.d("jiho", "totalDistance : "+totalDistance);

		String printSpeed = new DecimalFormat("0.00").format(avgSpeed);
		String printTime = Utility.getInstance().convertSecondsToHours(totalTime);
		String printDistance = String.format("%.1f", totalDistance / 1000);
		return printSpeed+", "+printTime+", "+printDistance;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder viewHolder = null;
		
		if ( view == null ){
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.row_ride, null);
			viewHolder = new ViewHolder();
			viewHolder.id_tv_name = (TextView)view.findViewById(R.id.id_tv_name);
			viewHolder.id_tv_start_time = (TextView)view.findViewById(R.id.id_tv_start_time);
			viewHolder.id_tv_ride_info = (TextView)view.findViewById(R.id.id_tv_ride_info);
			viewHolder.bt_show_info = (Button)view.findViewById(R.id.bt_show_info);
			viewHolder.bt_create_gpx = (Button)view.findViewById(R.id.bt_create_gpx);
			viewHolder.bt_del = (Button)view.findViewById(R.id.bt_del);
			view.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)view.getTag();
		}

		final RideVO vo = data.get(position);
		final int finalPosition = position;
		final TextView final_id_tv_ride_info = viewHolder.id_tv_ride_info;

		if ( vo != null ){
			viewHolder.id_tv_name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, GpsLogListActivity.class);
					intent.putExtra("PARENT_ID", vo.primaryKey);
					mContext.startActivity(intent);
				}
			});
			
			viewHolder.bt_show_info.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					notifyDataSetChanged();
					final_id_tv_ride_info.setText(calculateRideInfo(vo.primaryKey));
				}
			});

			viewHolder.bt_del.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					RideDao rideDao = RideDao.getInstance(DBHelper.getInstance(mContext));
					rideDao.delete(vo.primaryKey);
					data.remove(finalPosition);
					notifyDataSetChanged();
				}
			});

			viewHolder.bt_create_gpx.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UploadGpxFile uploadGpxFile = new UploadGpxFile(mContext);
					uploadGpxFile.excute(vo.primaryKey);
				}
			});
			viewHolder.id_tv_name.setText(vo.name);
			viewHolder.id_tv_start_time.setText(String.valueOf(vo.startTime));
		}

		return view;
	}
	
	class ViewHolder{
		TextView id_tv_name;
		TextView id_tv_start_time;
		TextView id_tv_ride_info;
		Button bt_show_info;
		Button bt_create_gpx;
		Button bt_del;
	}
}
