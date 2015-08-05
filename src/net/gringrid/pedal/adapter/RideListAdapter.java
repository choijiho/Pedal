package net.gringrid.pedal.adapter;

import java.util.List;

import net.gringrid.pedal.GPXMaker;
import net.gringrid.pedal.R;
import net.gringrid.pedal.R.id;
import net.gringrid.pedal.R.layout;
import net.gringrid.pedal.db.vo.RideVO;
import android.content.Context;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if ( view == null ){
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.row_ride, null);
		}
		
		final RideVO vo = data.get(position);
		if ( vo != null ){
			TextView id_tv_name = (TextView)view.findViewById(R.id.id_tv_name);
			TextView id_tv_start_time = (TextView)view.findViewById(R.id.id_tv_start_time);
			Button bt_create_gpx = (Button)view.findViewById(R.id.bt_create_gpx);
			bt_create_gpx.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					GPXMaker gpxMaker = new GPXMaker(mContext);
					gpxMaker.createGPXFile(vo.primaryKey);
				}
			});
			id_tv_name.setText(vo.name);
			id_tv_start_time.setText(String.valueOf(vo.startTime));
		}

		return view;
	}
}
