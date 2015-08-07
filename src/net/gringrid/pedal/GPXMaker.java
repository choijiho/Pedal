package net.gringrid.pedal;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.gringrid.pedal.db.DBHelper;
import net.gringrid.pedal.db.GpsLogDao;
import net.gringrid.pedal.db.RideDao;
import net.gringrid.pedal.db.vo.GpsLogVO;
import net.gringrid.pedal.db.vo.RideVO;
import android.content.Context;
import android.util.Log;

public class GPXMaker {

	final String HEADER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	final String HEADER_CREATOR = "<gpx creator=\"strava.com Android\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">";
	private Context mContext;
	RideDao mRideDao;
	GpsLogDao mGpsLogDao;
	
	public GPXMaker(Context context) {
		mContext = context;
	}

 	public void createGPXFile(int rideId){
 		String FILENAME = "tmp.gpx";

		mRideDao = RideDao.getInstance(DBHelper.getInstance(mContext));
		mGpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(mContext));

		RideVO rideVO = mRideDao.find(rideId);
 		OutputStreamWriter outputStreamWriter = null;
		try {
			outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE));
			outputStreamWriter.write(HEADER_XML);
 			outputStreamWriter.write(HEADER_CREATOR);
 			outputStreamWriter.write("<metadata>");
 			outputStreamWriter.write("<time>"+convertUTCtoLocal(rideVO.startTime)+"</time>");
 			outputStreamWriter.write("</metadata>");
 			outputStreamWriter.write("<trk>");
 			outputStreamWriter.write("<name>"+rideVO.name+"</name>");
 			outputStreamWriter.write("<trkseq>");

 			Log.d("jiho", "convertUTCtoLocal(rideVO.startTime) : "+convertUTCtoLocal(rideVO.startTime));

 			List<GpsLogVO> results = mGpsLogDao.findWithParentId(rideId);
 			for ( GpsLogVO gpsLogVO : results){
 				outputStreamWriter.write("<trkpt lat"+gpsLogVO.latitude+" lon="+gpsLogVO.longitude+">");
 				outputStreamWriter.write("<ele>"+gpsLogVO.elevation+"</ele>");
 				outputStreamWriter.write("<time>"+convertUTCtoLocal(gpsLogVO.gpsTime)+"</time>");
 				outputStreamWriter.write("</trkpt>");
 			}

 			outputStreamWriter.write("</trk>");
 			outputStreamWriter.write("</trkseq>");
 			outputStreamWriter.write("</gpx>");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( outputStreamWriter != null ){
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 	}
 	
 	private String convertUTCtoLocal(long utc){
 		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
 		Date date = new Date();
 		date.setTime(utc);
 		return simpleDateFormat.format(date);
 	}

}
