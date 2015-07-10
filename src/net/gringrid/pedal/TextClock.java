package net.gringrid.pedal;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextClock extends TextView {
	private int hours;
	private int minutes;
	private int seconds;
	
	
	private Timer clockTimer;
	private final TimerTask clockTask = new TimerTask() {
		@Override
		public void run() {
			mHandler.post(mUpdateResults);
		}
	};
	
	final Handler mHandler = new Handler();
        final Runnable mUpdateResults = new Runnable() {
            public void run() {
                update();
            }
        };
	
	public TextClock(Context context) {
		super(context);
		init();
	}
	
	private void update() {
		seconds++;
		
		if (seconds >= 60) {
			seconds = 0;
			if (minutes < 59) {
				minutes++;
			} else if (hours < 23) {
				minutes = 0;
				hours++;
			} else {
				minutes = 0;
				hours = 0;
			}
		}
		if (seconds % 2 == 0) {
			setText(String.format("%02d:%02d", hours, minutes));
		} else {
			setText(String.format("%02d.%02d", hours, minutes));
		}
	}
	
	private void init() {		
		clockTimer = new Timer();
		
		Calendar mCalendar = Calendar.getInstance(); 
		hours = mCalendar.get(Calendar.HOUR_OF_DAY);
		minutes = mCalendar.get(Calendar.MINUTE);
		seconds = mCalendar.get(Calendar.SECOND);
		clockTimer.scheduleAtFixedRate(clockTask, 0, 1000);
	}
 
	public TextClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
 
	public TextClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
 
}