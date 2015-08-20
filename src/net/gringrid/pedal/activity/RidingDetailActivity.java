package net.gringrid.pedal.activity;

import java.text.DecimalFormat;
import java.util.List;

import net.gringrid.pedal.R;
import net.gringrid.pedal.Setting;
import net.gringrid.pedal.SharedData;
import net.gringrid.pedal.Utility;
import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.RideDao;
import net.gringrid.pedal.db.vo.GpsLogVO;
import net.gringrid.pedal.db.vo.RideVO;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class RidingDetailActivity extends Activity {

	int mRidingId;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_riding_detail);
	    mRidingId = getIntent().getExtras().getInt("RIDING_ID");
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	private RideVO getRideInfo(){
		RideDao rideDao = RideDao.getInstance(DBHelper.getInstance(this));
		RideVO rideVO = rideDao.find(mRidingId);
		RideVO detailVO = getRidingDetailInfo();

		rideVO.ridingTime = detailVO.ridingTime;
		rideVO.distance = detailVO.distance;
		rideVO.avgSpeed = detailVO.avgSpeed;
		rideVO.maxSpeed = detailVO.maxSpeed;
		rideVO.altitude = detailVO.altitude;
		
		return rideVO;
		
	}
	/**
	 * Riding Time
	 */
	private RideVO getRidingDetailInfo(){
		RideVO detailVO = new RideVO();
		GpsLogDao gpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(this));
		List<GpsLogVO> gpsLogVOList = gpsLogDao.findWithParentId(mRidingId);
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
		totalTime = totalTime / 1000;
		avgSpeed = totalDistance / totalTime * 3.6f;
		Log.d("jiho", "avgSpeed : "+avgSpeed);
		Log.d("jiho", "totalTime : "+totalTime);
		Log.d("jiho", "totalDistance : "+totalDistance);

		String printSpeed = new DecimalFormat("0.0").format(avgSpeed)+"km/h";
		String printTime = Utility.getInstance().convertSecondsToHours(totalTime);
		String printDistance = String.format("%.1f", totalDistance / 1000)+"km";
		
		detailVO.ridingTime= printTime;
		detailVO.avgSpeed = printSpeed;
		detailVO.distance = printDistance;
		
		return detailVO;
	}	
}
