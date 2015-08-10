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

	public String convertSecondsToHours(long seconds){
		String result = "";
		int minutes = (int) ((seconds / (60)) % 60);
		int hours   = (int) ((seconds / (60*60)) % 24);
		
		if ( seconds > 3600 ){
			result += hours+":";
		}
		if ( seconds > 60 ){
			result += minutes+":";
		}
		result += seconds % 60;
		
		return result;
	}
	
}
