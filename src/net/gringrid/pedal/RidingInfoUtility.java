package net.gringrid.pedal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.RideDao;
import net.gringrid.pedal.db.vo.GpsLogVO;
import net.gringrid.pedal.db.vo.RideVO;
import android.content.Context;
import android.location.Location;
import android.util.Log;

public class RidingInfoUtility {

	Context mContext;

	// array.xml   riding_infomation_list 와 synch 되어야 한다.


	public RidingInfoUtility( Context context ) {
		mContext = context;
	}

	/**
	 * Riding Time
	 */
	public ArrayList<String> calculateRideInfo(long parentId){
		ArrayList<String> rideInfo = new ArrayList<String>();
		GpsLogDao gpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(mContext));
		List<GpsLogVO> gpsLogVOList = gpsLogDao.findWithParentId(parentId);
		GpsLogVO preVo = null;

		float avgSpeed = 0;
		float maxSpeed = 0;
		long totalTime = 0;
		float totalDistance = 0;
		float[] distanceResult = new float[3];
		float tmpSpeed = 0;
		
		float tmpDistance = 0;
		float tmpTime = 0;
		
		for ( GpsLogVO vo : gpsLogVOList){
			if ( preVo != null ){
				Location.distanceBetween(preVo.latitude, preVo.longitude, vo.latitude, vo.longitude, distanceResult);
//				tmpSpeed = distanceResult[0] / (vo.gpsTime - preVo.gpsTime) * 1000 * ;
//				tmpSpeed = distanceResult[0] / ((vo.gpsTime - preVo.gpsTime) / 1000) * 3.6f;
				
				tmpTime += (vo.gpsTime - preVo.gpsTime) / 1000;
				tmpDistance += distanceResult[0];
				if ( tmpTime > 3 ){
					tmpSpeed = tmpDistance / tmpTime * 3.6f;
					if ( maxSpeed < tmpSpeed ) {
						maxSpeed = tmpSpeed;
						Log.d("jiho", "Max Speed : "+String.format("%.1f",maxSpeed));
					}
					tmpTime = 0;
					tmpDistance = 0;
				}
					
					
				Log.d("jiho", "tmpSpeed : "+tmpSpeed+"distance : "+distanceResult[0]+", "+(vo.gpsTime - preVo.gpsTime));
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
		String printRidingTime = String.valueOf(totalTime);
		String printDistance = String.format("%.2f", totalDistance / 1000);
		String printMaxSpeed = String.format("%.1f", maxSpeed);

		rideInfo.add(DisplayInfoManager.INDEX_DISTANCE, printDistance);
		rideInfo.add(DisplayInfoManager.INDEX_RIDING_TIME, printRidingTime);
		rideInfo.add(DisplayInfoManager.INDEX_AVG_SPEED, printAvgSpeed);
		rideInfo.add(DisplayInfoManager.INDEX_MAX_SPEED, printMaxSpeed);

		return rideInfo;
	}
	
	public void saveRidingInfo(long parentId){
		RideDao dao = RideDao.getInstance(DBHelper.getInstance(mContext));
		RideVO vo = dao.find((int)parentId);
		ArrayList<String> detailInfo = new ArrayList<String>();
		detailInfo = calculateRideInfo(vo.primaryKey);
		vo.avgSpeed = detailInfo.get(DisplayInfoManager.INDEX_AVG_SPEED);
		vo.distance = detailInfo.get(DisplayInfoManager.INDEX_DISTANCE);
		vo.maxSpeed = detailInfo.get(DisplayInfoManager.INDEX_MAX_SPEED);
		vo.ridingTime = detailInfo.get(DisplayInfoManager.INDEX_RIDING_TIME);
		int result = dao.update(vo);	
	}

	public void saveRidingInfo(RideVO vo){
		RideDao dao = RideDao.getInstance(DBHelper.getInstance(mContext));
		String[] results = new String[4];
		ArrayList<String> detailInfo = new ArrayList<String>();
		detailInfo = calculateRideInfo(vo.primaryKey);
		vo.avgSpeed = detailInfo.get(DisplayInfoManager.INDEX_AVG_SPEED);
		vo.distance = detailInfo.get(DisplayInfoManager.INDEX_DISTANCE);
		vo.maxSpeed = detailInfo.get(DisplayInfoManager.INDEX_MAX_SPEED);
		vo.ridingTime = detailInfo.get(DisplayInfoManager.INDEX_RIDING_TIME);
		int result = dao.update(vo);	
	}
}
