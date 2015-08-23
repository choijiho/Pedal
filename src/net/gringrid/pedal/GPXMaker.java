package net.gringrid.pedal;

import java.io.File;
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

public class GPXMaker {
	public static final String FILENAME = "pedal.gpx";
	private final String HEADER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private final String HEADER_CREATOR = "<gpx creator=\"strava.com Android\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">";
	private Context mContext;
	private RideDao mRideDao;
	private GpsLogDao mGpsLogDao;
	
	public GPXMaker(Context context) {
		mContext = context;
	}

	public File readGPXFile(){
		String filePath = mContext.getFilesDir().getPath()+"/"+FILENAME;
		File file = new File(filePath);
		return file;
	}
	
	private boolean deleteFile(){
		String filePath = mContext.getFilesDir().getPath()+"/"+FILENAME;
		File file = new File(filePath);
		return file.delete();
	}

 	public boolean createGPXFile(int rideId){
		mRideDao = RideDao.getInstance(DBHelper.getInstance(mContext));
		mGpsLogDao = GpsLogDao.getInstance(DBHelper.getInstance(mContext));

		RideVO rideVO = mRideDao.find(rideId);
 		OutputStreamWriter outputStreamWriter = null;
		try {
			outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE));
			outputStreamWriter.write(HEADER_XML+"\n");
 			outputStreamWriter.write(HEADER_CREATOR+"\n");
 			outputStreamWriter.write(" <metadata>"+"\n");
 			outputStreamWriter.write("  <time>"+convertUTCtoLocal(rideVO.startTime)+"</time>\n");
 			outputStreamWriter.write(" </metadata>"+"\n");
 			outputStreamWriter.write(" <trk>"+"\n");
 			outputStreamWriter.write("  <name>"+rideVO.name+"</name>"+"\n");
 			outputStreamWriter.write("  <trkseq>"+"\n");
 			
 			List<GpsLogVO> results = mGpsLogDao.findWithParentId(rideId);

 			if ( results.isEmpty() ){
 				deleteFile();
 			}else{
 				for ( GpsLogVO gpsLogVO : results){
 					outputStreamWriter.write("   <trkpt lat=\""+gpsLogVO.latitude+"\" lon=\""+gpsLogVO.longitude+"\">"+"\n");
 					outputStreamWriter.write("    <ele>"+gpsLogVO.elevation+"</ele>"+"\n");
 					outputStreamWriter.write("    <time>"+convertUTCtoLocal(gpsLogVO.gpsTime)+"</time>"+"\n");
 					outputStreamWriter.write("   </trkpt>"+"\n");
 				}
 				outputStreamWriter.write("  </trkseq>"+"\n");
 				outputStreamWriter.write(" </trk>"+"\n");
 				outputStreamWriter.write("</gpx>"+"\n");
 			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if ( outputStreamWriter != null ){
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
 	}
 	
 	private String convertUTCtoLocal(long utc){
 		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
 		Date date = new Date();
 		date.setTime(utc);
 		return simpleDateFormat.format(date);
 	}

}
