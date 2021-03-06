package net.gringrid.pedal.adapter;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import net.gringrid.pedal.GPXMaker;
import net.gringrid.pedal.R;
import net.gringrid.pedal.Utility;
import net.gringrid.pedal.R.id;
import net.gringrid.pedal.R.layout;
import net.gringrid.pedal.StravaTasks;
import net.gringrid.pedal.activity.GpsLogListActivity;
import net.gringrid.pedal.adapter.RideListAdapter.ViewHolder;
import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.RideDao;
import net.gringrid.pedal.db.vo.GpsLogVO;
import net.gringrid.pedal.db.vo.RideVO;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.Toast;

public class ExpandableRideListAdapter extends BaseExpandableListAdapter{

	Context mContext;
	List<RideVO> data;
	ProgressDialog mProgressDialog;
	SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

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

		if ( vo != null ){
		
			Date startTime = new Date(vo.startTime);
			Date endTime = new Date(vo.endTime);

			String startDate = mSimpleDateFormat.format(startTime);
			String endDate = vo.endTime==0 ? "" : " ~ "+mSimpleDateFormat.format(endTime);
			String ridingTime = startDate + endDate;
				
			viewHolder.id_tv_name.setText(vo.name);
			viewHolder.id_tv_start_time.setText(ridingTime);
			
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
			viewHolder.id_tv_strava_status = (TextView)view.findViewById(R.id.id_tv_strava_status);
			view.setTag(viewHolder);
		}else{
			viewHolder = (ChildViewHolder)view.getTag();
		}
		Log.d("jiho", "DEBUG1");
		final RideVO vo = data.get(groupPosition);
		
		if ( vo != null ){
			viewHolder.id_tv_distance.setText(vo.distance);
			viewHolder.id_tv_time.setText(vo.ridingTime);
			viewHolder.id_tv_avg_speed.setText(vo.avgSpeed);
			viewHolder.id_tv_max_speed.setText(vo.maxSpeed);
			viewHolder.id_tv_strava_status.setText(vo.stravaStatus);
			
			viewHolder.id_iv_upload_strava.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new UploadTask().execute(vo.primaryKey);
//					UploadGpxFile uploadGpxFile = new UploadGpxFile(mContext);
//					uploadGpxFile.excute(vo.primaryKey);
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
		TextView id_tv_strava_status;
	}
	
	class UploadTask extends AsyncTask<Integer, String, Integer>{
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
			publishProgress("30", "making GPX file from riding data");	
			GPXMaker gPXMaker = new GPXMaker(mContext);

			publishProgress("80", "uploading GPX file to STRAVA. ");	
			StravaTasks uploadGpxFile = new StravaTasks(mContext);
			if ( gPXMaker.createGPXFile(params[0]) ){
				JSONObject jsonObject = uploadGpxFile.createActivity();
				try {
					RideDao dao = RideDao.getInstance(DBHelper.getInstance(mContext));
					RideVO vo = dao.find(params[0]);
					String stravaId = jsonObject.getString("id");
					String stravaError = jsonObject.getString("error");
					vo.stravaId = stravaId;
					dao.update(vo);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
			mProgressDialog.dismiss();
			Toast.makeText(mContext, "END", Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}
		
		
	}
}
