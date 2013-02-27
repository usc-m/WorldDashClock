package uk.co.wishf.worlddashclock;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.wishf.worlddashclock.DateFormatter.DateTimeFormatAction;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class WorldClockExtension extends DashClockExtension {
	
	static final String TZ_PREF = "timezone";
	
	Timer timer;
	
	String userTz;
	Calendar tzTime;
	DateFormatter formatter;
	String placeName;
	
	private synchronized void setTz(String tz) {
		userTz = tz;
		tzTime = Calendar.getInstance(TimeZone.getTimeZone(tz));
		
		String[] nameComponents = tz.replace('_', ' ').split("/");
		placeName = String.format("%s, %s", nameComponents[1], nameComponents[0]);
	}
	
	@Override
	protected void onInitialize(boolean isReconnect) {
		this.setUpdateWhenScreenOn(true);
		
		if(timer == null) {
			timer = new Timer("World Clock Dashclock Extension Updater");
			Calendar now = Calendar.getInstance();
			long dly = (1000 - now.get(Calendar.MILLISECOND)) + (1000 * (60 - now.get(Calendar.SECOND)));
			timer.scheduleAtFixedRate(new ClockUpdater(this), dly, 60*1000);
		}
		
		super.onInitialize(isReconnect);
	}
	
	@Override
	protected void onUpdateData(int reason) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		this.setTz(sp.getString(TZ_PREF, "Australia/Sydney"));
		
		this.pushNewData();
	}
	
	private synchronized void pushNewData() {	
		final StringPair composedDateTime = formatter.format(tzTime);
		
		this.publishUpdate(new ExtensionData()
				   		   .visible(true)
				   		   .icon(R.drawable.clock)
				   		   .status(composedDateTime.status)
				   		   .expandedTitle(composedDateTime.title)
				   		   .expandedBody(placeName));
	}
	
	class ClockUpdater extends TimerTask {
		
		WorldClockExtension parent;
		
		public ClockUpdater(WorldClockExtension instance) {
			parent = instance;
		}
		
		@Override
		public void run() {
			parent.pushNewData();
		}
		
	}

}
