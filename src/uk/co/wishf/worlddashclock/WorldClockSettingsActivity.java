package uk.co.wishf.worlddashclock;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import android.view.MenuItem;

import java.util.TimeZone;

public class WorldClockSettingsActivity extends PreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.clock);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		addPreferencesFromResource(R.xml.pref_general);
		
		PreferenceScreen screen = this.getPreferenceScreen();
		
		// Timezone category
		PreferenceCategory tzCat = new PreferenceCategory(this);
		tzCat.setTitle("Timezone");
		
		screen.addPreference(tzCat);
		
		String[] timezones = TimeZone.getAvailableIDs();
		
		ListPreference listPref = new ListPreference(this);
		listPref.setKey(WorldClockExtension.TZ_PREF);
		listPref.setEntries(timezones);
		listPref.setEntryValues(timezones);
		listPref.setDialogTitle("Select Timezone");
		listPref.setTitle("Timezone");
		
		bindPreferenceSummaryToValue(listPref);
		
		tzCat.addPreference(listPref);
		
		// Formatting category
		PreferenceCategory fmtCat = new PreferenceCategory(this);
		fmtCat.setTitle("Formatting");
		
		screen.addPreference(fmtCat);
		
		CheckBoxPreference twentyFourHourClock = new CheckBoxPreference(this);
		twentyFourHourClock.setTitle("Display times in 24 hour format");
		twentyFourHourClock.setChecked(true);
		twentyFourHourClock.setKey(WorldClockExtension.TWENTYFOUR_HOUR);
		
		fmtCat.addPreference(twentyFourHourClock);
		
		DateFormatter.SelectorConstants[] vals = DateFormatter.SelectorConstants.values();
		String[] names = new String[vals.length];
		
		for(int i = 0; i < vals.length; i++) {
			names[i] = vals[i].toString();
		}
		
		ListPreference dateFormat = new ListPreference(this);
		dateFormat.setKey(WorldClockExtension.DATE_FORMAT_PREF);
		dateFormat.setEntries(new String[] {"Wed, Feb 27", "Wed, 27 Feb", "Feb 27", "27 Feb"});
		dateFormat.setEntryValues(names);
		dateFormat.setDialogTitle("Select Date Format");
		dateFormat.setTitle("Date Format");
		
		bindPreferenceSummaryToValue(dateFormat);
		
		fmtCat.addPreference(dateFormat);
		
		CheckBoxPreference dateBeforeTime = new CheckBoxPreference(this);
		dateBeforeTime.setTitle("Display date in front of time");
		dateBeforeTime.setSummary("Toggles between \"DATE at TIME\" and \"TIME on DATE\" display");
		dateBeforeTime.setChecked(true);
		dateBeforeTime.setKey(WorldClockExtension.DATE_AT_TIME);
		
		fmtCat.addPreference(dateBeforeTime);
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);
			}
			
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	//TODO: Preserve stack
	            finish();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}

