package uk.co.wishf.worlddashclock;

import java.util.Calendar;
import java.util.Locale;

/*
 * Formats
 * -------
 * - Wed, Feb 27 at 11:52
 * - Feb 27 at 11:52
 * - Wed, Feb 27 at 11:52AM
 * - Feb 27 at 11:52AM
 * - 11:52 on Wed, Feb 27
 * - 11:52 on Feb 27
 * - 11:52AM on Wed, Feb 27
 * - 11:52AM on Feb 27
 */

public class DateFormatter {
	
	public enum SelectorConstants {
		DAY_MONTH_DATE (DateFormatter.DAY_MONTH_DATE), 
		DAY_DATE_MONTH (DateFormatter.DAY_DATE_MONTH), 
		DATE_MONTH (DateFormatter.DATE_MONTH), 
		MONTH_DATE (DateFormatter.MONTH_DATE);
		
		public final DateFormatAction formatter;
		
		SelectorConstants(DateFormatAction d) { formatter = d; }
	}
	
	// DATE FORMATTERS
	public interface DateFormatAction {
		public StringPair createDateString(Calendar cal);
	}
	
	public static final DateFormatAction DAY_MONTH_DATE = new DateFormatAction() {
		@Override
		public StringPair createDateString(Calendar cal) {
			final String day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
			final String mon = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
			final int date = cal.get(Calendar.DAY_OF_MONTH);
			
			return new StringPair(
					String.format(Locale.getDefault(), "%s %d", mon, date),
					String.format(Locale.getDefault(), "%s, %s %d", day, mon, date)
					);
		}
	};
	
	public static final DateFormatAction DAY_DATE_MONTH = new DateFormatAction() {
		@Override
		public StringPair createDateString(Calendar cal) {
			final String day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
			final String mon = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
			final int date = cal.get(Calendar.DAY_OF_MONTH);
			
			return new StringPair(
					String.format(Locale.getDefault(), "%d %s", date, mon),
					String.format(Locale.getDefault(), "%s, %d %s", day, date, mon)
					);
		}
	};
	
	public static final DateFormatAction DATE_MONTH = new DateFormatAction() {
		@Override
		public StringPair createDateString(Calendar cal) {
			final String mon = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
			final int date = cal.get(Calendar.DAY_OF_MONTH);
			
			return new StringPair(
					String.format(Locale.getDefault(), "%d %s", date, mon),
					String.format(Locale.getDefault(), "%d %s", date, mon)
					);
		}
	};
	
	public static final DateFormatAction MONTH_DATE = new DateFormatAction() {
		@Override
		public StringPair createDateString(Calendar cal) {
			final String mon = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
			final int date = cal.get(Calendar.DAY_OF_MONTH);
			
			return new StringPair(
					String.format(Locale.getDefault(), "%s %d", mon, date),
					String.format(Locale.getDefault(), "%s %d", mon, date)
					);
		}
	};
	
	// TIME FORMATTERS
	
	public interface TimeFormatAction {	
		public String createTimeString(Calendar cal);
	}
	
	public static final TimeFormatAction TWELEVE_HOUR = new TimeFormatAction() {
		static final String GENERAL_FORMAT = "%d:%d%s";
		static final String SUB_10_MIN_FORMAT = "%d:0%d%s";
		
		@Override
		public String createTimeString(Calendar cal) {
			final int hr = cal.get(Calendar.HOUR);
			final String amPm = (cal.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";	
			final int minute = cal.get(Calendar.MINUTE);
			
			return String.format(Locale.getDefault(), (minute < 10) ? SUB_10_MIN_FORMAT : GENERAL_FORMAT, hr, minute, amPm);
		}
	};
	
	public static final TimeFormatAction TWENTYFOUR_HOUR = new TimeFormatAction() {
		static final String GENERAL_FORMAT = "%d:%d";
		static final String SUB_10_MIN_FORMAT = "%d:0%d";
		
		@Override
		public String createTimeString(Calendar cal) {
			final int hr = cal.get(Calendar.HOUR);
			final int minute = cal.get(Calendar.MINUTE);
			
			return String.format(Locale.getDefault(), (minute < 10) ? SUB_10_MIN_FORMAT : GENERAL_FORMAT, hr, minute);
		}
	};
	
	// DATE AND TIME COMBINED FORMATTERS
	
	public interface DateTimeFormatAction {
		public StringPair createDateTimeString(Calendar cal, DateFormatAction d, TimeFormatAction t);
	}
	
	public static final DateTimeFormatAction DATE_AT_TIME = new DateTimeFormatAction() {
		@Override
		public StringPair createDateTimeString(Calendar cal, DateFormatAction d, TimeFormatAction t) {
			final StringPair date = d.createDateString(cal);
			final String time = t.createTimeString(cal);
			
			return new StringPair(
					String.format(Locale.getDefault(), "%s\n%s", date.status, time),
					String.format(Locale.getDefault(),  "%s at %s", date.title, time)
					);
		}
	};
	
	public static final DateTimeFormatAction TIME_ON_DATE = new DateTimeFormatAction() {
		@Override
		public StringPair createDateTimeString(Calendar cal, DateFormatAction d, TimeFormatAction t) {
			final StringPair date = d.createDateString(cal);
			final String time = t.createTimeString(cal);
			
			return new StringPair(
					String.format(Locale.getDefault(), "%s\n%s", time, date.status),
					String.format(Locale.getDefault(),  "%s on %s", time, date.title)
					);
		}
	};
	
	// FORMATTER INSTANCE
	
	final DateFormatAction date;
	final TimeFormatAction time;
	final DateTimeFormatAction combined;
	
	public DateFormatter(DateFormatAction date, TimeFormatAction time, DateTimeFormatAction combined) {
		this.date = date;
		this.time = time;
		this.combined = combined;
	}
	
	public StringPair format(Calendar cal) {
		return combined.createDateTimeString(cal, date, time);
	}

}
