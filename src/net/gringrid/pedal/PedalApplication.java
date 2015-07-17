package net.gringrid.pedal;

import android.app.Application;

public class PedalApplication extends Application{
	private static PedalApplication instance;
	
	public PedalApplication() {
		instance = this;
	}

	public static PedalApplication getInstance(){
		return instance;
	}
}
