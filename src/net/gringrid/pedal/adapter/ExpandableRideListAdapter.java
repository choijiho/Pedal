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
import net.gringrid.pedal.adapter.RideListAdapter.ViewHolder;
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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpandableRideListAdapter extends BaseExpandableListAdapter{

	Context mContext;
	List<RideVO> data;

	final int INDEX_LENGTH = 4;
	final int INDEX_DISTANCE = 0;
	final int INDEX_TIME = 1;
	final int INDEX_AVG_SPEED = 2;
	final int INDEX_MAX_SPEED = 3;
	

	public ExpandableRideListAdapter(Context context, List<RideVO> objects) {
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
		
		Log.d("jiho", "before : "+data.get(position).toString());
		RideVO vo = data.get(position);
		String detailInfo[] = new String[INDEX_LENGTH];
		calculateRideInfo(vo.primaryKey, detailInfo);
		
		vo.detailDistance = detailInfo[INDEX_DISTANCE];
		vo.detailTime = detailInfo[INDEX_TIME];
		vo.detailAvgSpeed = detailInfo[INDEX_AVG_SPEED];
		vo.detailMaxSpeed = detailInfo[INDEX_MAX_SPEED];
		vo.isShowDetail = true;
		
		Log.d("jiho", "detailDistance: "+data.get(position).detailDistance);
		Log.d("jiho", "after : "+data.get(position).toString());
		notifyDataSetChanged();
	}

	@Override
	public int getGroupCount() {
		return data.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view = convertView;
		GroupViewHolder viewHolder = null;
		
		if ( view == null ){
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.row_ride_group, null);
			viewHolder = new GroupViewHolder();
			viewHolder.id_tv_name = (TextView)view.findViewById(R.id.id_tv_name);
			viewHolder.id_tv_start_time = (TextView)view.findViewById(R.id.id_tv_start_time);
			view.setTag(viewHolder);
		}else{
			viewHolder = (GroupViewHolder)view.getTag();
		}

		final RideVO vo = data.get(groupPosition);
		final int finalPosition = groupPosition;

		if ( vo != null ){
			viewHolder.id_tv_name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDetailInfo(finalPosition);
				}
			});
		
			viewHolder.id_tv_name.setText(vo.name);
			viewHolder.id_tv_start_time.setText(String.valueOf(vo.startTime));
			
		}

		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		ChildViewHolder viewHolder = null;
		
		if ( view == null ){
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.row_ride_child, null);
			viewHolder = new ChildViewHolder();
			viewHolder.id_ll_detail_info = (LinearLayout)view.findViewById(R.id.id_ll_detail_info);
			viewHolder.id_tv_distance = (TextView)view.findViewById(R.id.id_tv_distance);
			viewHolder.id_tv_time = (TextView)view.findViewById(R.id.id_tv_time);
			viewHolder.id_tv_avg_speed = (TextView)view.findViewById(R.id.id_tv_avg_speed);
			viewHolder.id_tv_max_speed = (TextView)view.findViewById(R.id.id_tv_max_speed);
			viewHolder.id_iv_upload_strava = (ImageView)view.findViewById(R.id.id_iv_upload_strava);
			
			view.setTag(viewHolder);
		}else{
			viewHolder = (ChildViewHolder)view.getTag();
		}

		final RideVO vo = data.get(groupPosition);

		if ( vo != null ){
			if ( vo.isShowDetail ){
				viewHolder.id_ll_detail_info.setVisibility(View.VISIBLE);
				viewHolder.id_tv_distance.setText(vo.detailDistance);
				viewHolder.id_tv_time.setText(vo.detailTime);
				viewHolder.id_tv_avg_speed.setText(vo.detailAvgSpeed);
				viewHolder.id_tv_max_speed.setText(vo.detailMaxSpeed);
			}else{
				return null;
			}
				
			viewHolder.id_iv_upload_strava.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UploadGpxFile uploadGpxFile = new UploadGpxFile(mContext);
					uploadGpxFile.excute(vo.primaryKey);
				}
			});
		}

		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}
	
	class GroupViewHolder{
		TextView id_tv_name;
		TextView id_tv_start_time;
	}
	class ChildViewHolder{
		LinearLayout id_ll_detail_info;
		TextView id_tv_distance;
		TextView id_tv_time;
		TextView id_tv_avg_speed;
		TextView id_tv_max_speed;
		ImageView id_iv_upload_strava;
	}
}
