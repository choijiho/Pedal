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

	public ExpandableRideListAdapter(Context context, List<RideVO> objects) {
		mContext = context;
		data = objects;
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
//			viewHolder.id_tv_name.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					showDetailInfo(finalPosition);
//				}
//			});
		
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
		Log.d("jiho", "getChildView");
		if ( view == null ){
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.row_ride_child, null);
			viewHolder = new ChildViewHolder();
			viewHolder.id_tv_distance = (TextView)view.findViewById(R.id.id_tv_distance);
			viewHolder.id_tv_time = (TextView)view.findViewById(R.id.id_tv_time);
			viewHolder.id_tv_avg_speed = (TextView)view.findViewById(R.id.id_tv_avg_speed);
			viewHolder.id_tv_max_speed = (TextView)view.findViewById(R.id.id_tv_max_speed);
			viewHolder.id_iv_upload_strava = (ImageView)view.findViewById(R.id.id_iv_upload_strava);
			
			view.setTag(viewHolder);
		}else{
			viewHolder = (ChildViewHolder)view.getTag();
		}
		Log.d("jiho", "DEBUG1");
		final RideVO vo = data.get(groupPosition);

		if ( vo != null ){
			if ( vo.isShowDetail ){
				viewHolder.id_tv_distance.setText(vo.detailDistance);
				viewHolder.id_tv_time.setText(vo.detailTime);
				viewHolder.id_tv_avg_speed.setText(vo.detailAvgSpeed);
				viewHolder.id_tv_max_speed.setText(vo.detailMaxSpeed);
			}else{
				return view;
			}
				
			viewHolder.id_iv_upload_strava.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UploadGpxFile uploadGpxFile = new UploadGpxFile(mContext);
					uploadGpxFile.excute(vo.primaryKey);
				}
			});
		}
		Log.d("jiho", "DEBUG2");

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
		TextView id_tv_distance;
		TextView id_tv_time;
		TextView id_tv_avg_speed;
		TextView id_tv_max_speed;
		ImageView id_iv_upload_strava;
	}
}
