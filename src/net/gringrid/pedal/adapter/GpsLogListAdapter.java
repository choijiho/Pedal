package net.gringrid.pedal.adapter;

import java.util.List;

import net.gringrid.pedal.R;
import net.gringrid.pedal.db.vo.GpsLogVO;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GpsLogListAdapter extends ArrayAdapter<GpsLogVO>{

	Context mContext;
	List<GpsLogVO> data;
	
	public GpsLogListAdapter(Context context, int resource, List<GpsLogVO> objects) {
		super(context, resource, objects);
		mContext = context;
		data = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if ( view == null ){
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.row_gps_log, null);
		}
		
		GpsLogVO vo = data.get(position);
		if ( vo != null ){
			String data = String.valueOf(vo.latitude);
			data += ", "+String.valueOf(vo.longitude);
			data += ", "+String.valueOf(vo.elevation);
			data += ", "+String.valueOf(vo.gpsTime);
			data += ", "+String.valueOf(vo.parentId);
			TextView id_tv_data = (TextView)view.findViewById(R.id.id_tv_data);
			id_tv_data.setText(data);
		}
		return view;
	}
}
