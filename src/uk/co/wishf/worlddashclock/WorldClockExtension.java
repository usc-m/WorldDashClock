package uk.co.wishf.worlddashclock;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class WorldClockExtension extends DashClockExtension {
	
	static final String TZ_PREF = "timezone";
	static final String DATE_FORMAT_PREF = "dateFmt";
	static final String TWENTYFOUR_HOUR = "24hr";
	static final String DATE_AT_TIME = "dat";
	
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
		
		this.packFormatter(sp.getString(DATE_FORMAT_PREF, "DAY_DATE_MONTH"), 
				           sp.getBoolean(TWENTYFOUR_HOUR, true), 
				           sp.getBoolean(DATE_AT_TIME, true));
		
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
	
	private synchronized void packFormatter(String fmtDesc, boolean is24Hour, boolean dateBeforeTime) {
		DateFormatter.DateFormatAction dFmt = DateFormatter.SelectorConstants.valueOf(fmtDesc).formatter;
		DateFormatter.TimeFormatAction tFmt = (is24Hour) ? DateFormatter.TWENTYFOUR_HOUR : DateFormatter.TWELEVE_HOUR;
		DateFormatter.DateTimeFormatAction dtFmt = (dateBeforeTime) ? DateFormatter.DATE_AT_TIME : DateFormatter.TIME_ON_DATE;
		
		formatter = new DateFormatter(dFmt, tFmt, dtFmt);
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
