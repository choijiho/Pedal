package net.gringrid.pedal;

import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;

public class GPXMaker {

	final String HEADER_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	final String HEADER_CREATOR = "<gpx creator=\"strava.com Android\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">";
	private Context mContext;
	
// <metadata>
//  <time>2015-07-19T11:15:21Z</time>
// </metadata>

	public GPXMaker(Context context) {
		mContext = context;
	}
 	private void createGPXFile(){
// 		OutputStream fout = getFileOutputStream("gpxFile.gpx");
// 		fout.write("<gpx>");
// 		for (GeoPoint gp : listOfGeoPoints) {
// 			fout.write("<gpxPoint>" + getGeoPointAsStringForFile(gp) + "</gpxPoint>"); 
// 		}
// 		fout.write("</gpx>");
 	}

 	private OutputStream getFileOutputStream(String filename) {
// 		FileOutputStream fileOutputStream;
// 		fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
// 		// TODO Auto-generated method stub
 		return null;
 	}
}
