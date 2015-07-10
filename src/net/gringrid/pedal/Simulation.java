package net.gringrid.pedal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;

public class Simulation {

	int mIdx = 0;
	private Context mContext;
	private MockLocationProvider mock;
	final Handler handler = new Handler();
    
	public Simulation(Context context) {
		mContext = context;
	}

	private void startSimulation(){
		mock = new MockLocationProvider(LocationManager.GPS_PROVIDER, mContext);
		final List<Location> gpxList = readTextGPXFile();	
		handler.postDelayed(new Runnable() {
		      @Override
		      public void run() {
		    	  handler.postDelayed(this, 1000);
		    	  executeSimilation(gpxList);
		      }
		    }, 1500);
	}
	

	private void executeSimilation(List<Location> list){
	    mock.pushLocation(list.get(mIdx++));
	}

	private List<Location> readTextGPXFile(){
		List<Location> list = new ArrayList<Location>();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(mContext.getAssets().open("gps_test.gpx"));
			Element elementRoot = document.getDocumentElement();
		
			NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");
		
			for(int i = 0; i < nodelist_trkpt.getLength(); i++){
		
				Node node = nodelist_trkpt.item(i);
				NamedNodeMap attributes = node.getAttributes();
			
				String newLatitude = attributes.getNamedItem("lat").getTextContent();
				Double newLatitude_double = Double.parseDouble(newLatitude);
				
				String newLongitude = attributes.getNamedItem("lon").getTextContent();
				Double newLongitude_double = Double.parseDouble(newLongitude);
				
				String newLocationName = newLatitude + ":" + newLongitude;
				Location newLocation = new Location("Test");
				newLocation.setLatitude(newLatitude_double);
				newLocation.setLongitude(newLongitude_double);
				
				list.add(newLocation);
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return list;
	}
	
	
	public class SimulationTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			List<Location> gpxList = readTextGPXFile();	
			for ( Location location : gpxList ){
				try {
					Thread.sleep(1000);
//					onLocationChanged(location);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
}
