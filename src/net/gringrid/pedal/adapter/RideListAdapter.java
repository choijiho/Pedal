package net.gringrid.pedal.adapter;

import java.text.DecimalFormat;
import java.util.List;

import net.gringrid.pedal.GPXMaker;
import net.gringrid.pedal.R;
import net.gringrid.pedal.Utility;
import net.gringrid.pedal.R.id;
import net.gringrid.pedal.R.layout;
import net.gringrid.pedal.StravaTasks;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RideListAdapter extends ArrayAdapter<RideVO>{

	Context mContext;
	List<RideVO> data;

	final int INDEX_LENGTH = 4;
	final int INDEX_DISTANCE = 0;
	final int INDEX_TIME = 1;
	final int INDEX_AVG_SPEED = 2;
	final int INDEX_MAX_SPEED = 3;
	
	
	
	public RideListAdapter(Context context, int resource, List<RideVO> objects) {
		super(context, resource, objects);
		mContext = context;
		data = objects;
	}
	
	/**
	 * Riding Time
	 */
	private void calculateRideInfo(int parentId, String results[]){
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
//		return printSpeed+", "+printTime+", "+printDistance;
	}

	private void showDetailInfo(int position){
		
		RideVO vo = data.get(position);
		String detailInfo[] = new String[INDEX_LENGTH];
		calculateRideInfo(vo.primaryKey, detailInfo);
		
		vo.distance = detailInfo[INDEX_DISTANCE];
		vo.ridingTime = detailInfo[INDEX_TIME];
		vo.avgSpeed = detailInfo[INDEX_AVG_SPEED];
		vo.maxSpeed = detailInfo[INDEX_MAX_SPEED];
		vo.isShowDetail = true;
		
		notifyDataSetChanged();
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
			viewHolder.id_ll_detail_info = (LinearLayout)view.findViewById(R.id.id_ll_detail_info);
			viewHolder.id_tv_distance = (TextView)view.findViewById(R.id.id_tv_distance);
			viewHolder.id_tv_time = (TextView)view.findViewById(R.id.id_tv_time);
			viewHolder.id_tv_avg_speed = (TextView)view.findViewById(R.id.id_tv_avg_speed);
			viewHolder.id_tv_max_speed = (TextView)view.findViewById(R.id.id_tv_max_speed);
			viewHolder.id_iv_upload_strava = (ImageView)view.findViewById(R.id.id_iv_upload_strava);
			
			view.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder)view.getTag();
		}

		final RideVO vo = data.get(position);
		final int finalPosition = position;

		if ( vo != null ){
			viewHolder.id_tv_name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDetailInfo(finalPosition);
//					Intent intent = new Intent(mContext, GpsLogListActivity.class);
//					intent.putExtra("PARENT_ID", vo.primaryKey);
//					mContext.startActivity(intent);
					
				}
			});
			
//			viewHolder.bt_show_info.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					notifyDataSetChanged();
////					final_id_tv_ride_info.setText(calculateRideInfo(vo.primaryKey));
//				}
//			});

//			viewHolder.bt_del.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					RideDao rideDao = RideDao.getInstance(DBHelper.getInstance(mContext));
//					rideDao.delete(vo.primaryKey);
//					data.remove(finalPosition);
//					notifyDataSetChanged();
//				}
//			});

			viewHolder.id_iv_upload_strava.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					StravaTasks uploadGpxFile = new StravaTasks(mContext);
					uploadGpxFile.excute(vo.primaryKey);
				}
			});
			
			viewHolder.id_tv_name.setText(vo.name);
			viewHolder.id_tv_start_time.setText(String.valueOf(vo.startTime));
			if ( vo.isShowDetail ){
				viewHolder.id_ll_detail_info.setVisibility(View.VISIBLE);
				viewHolder.id_tv_distance.setText(vo.distance);
				viewHolder.id_tv_time.setText(vo.ridingTime);
				viewHolder.id_tv_avg_speed.setText(vo.avgSpeed);
				viewHolder.id_tv_max_speed.setText(vo.maxSpeed);
			}else{
				viewHolder.id_ll_detail_info.setVisibility(View.GONE);
			}
		}

		return view;
	}
	
	class ViewHolder{
		TextView id_tv_name;
		TextView id_tv_start_time;
		LinearLayout id_ll_detail_info;
		TextView id_tv_distance;
		TextView id_tv_time;
		TextView id_tv_avg_speed;
		TextView id_tv_max_speed;
		ImageView id_iv_upload_strava;
	}
}
