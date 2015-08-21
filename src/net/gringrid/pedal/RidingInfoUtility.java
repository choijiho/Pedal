package net.gringrid.pedal;

import java.text.DecimalFormat;
import java.util.List;

import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.vo.GpsLogVO;
import android.content.Context;
import android.location.Location;
import android.util.Log;

public class RidingInfoUtility {

	Context mContext;
	public static final int INDEX_LENGTH = 4;
	public static final int INDEX_DISTANCE = 0;
	public static final int INDEX_TIME = 1;
	public static final int INDEX_AVG_SPEED = 2;
	public static final int INDEX_MAX_SPEED = 3;	
	
	public RidingInfoUtility( Context context ) {
		mContext = context;
	}

	/**
	 * Riding Time
	 */
	public void calculateRideInfo(long parentId, String results[]){
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

		String printAvgSpeed = String.format("%.1f", avgSpeed);
		String printTime = String.valueOf(totalTime);
		String printDistance = String.format("%.2f", totalDistance / 1000);
		String printMaxSpeed = "";

		results[INDEX_DISTANCE] = printDistance;
		results[INDEX_TIME] = printTime;
		results[INDEX_AVG_SPEED] = printAvgSpeed;
		results[INDEX_MAX_SPEED] = printMaxSpeed;
	}
}
