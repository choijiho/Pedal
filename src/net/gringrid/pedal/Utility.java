package net.gringrid.pedal;


public class Utility {
	static Utility instance;

	private Utility(){}
	public static Utility getInstance(){
		if ( instance == null ){
			instance = new Utility();
		}
		return instance;
	}

	public String convertSecondsToHours(long milliseconds){
		String result = "";
		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000*60)) % 60);
		int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
		
		if ( milliseconds > 3600000 ){
			result += hours+":";
		}
		if ( milliseconds > 60000 ){
			result += minutes+":";
		}
		result += seconds;
		
		return result;
	}
	
}
