package com.tomtom.agriculture.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.tomtom.agriculture.R;

public class MainActivity extends FragmentActivity {

	private static final String MOCK_GPS_PROVIDER_INDEX = "GpsMockProviderIndex";

	private boolean mMockGpsProviderInitialized;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);

		//initMockGpsProvider();
	}

	private MockGpsProvider mMockGpsProviderTask = null;
	private Integer mMockGpsProviderIndex = 0;

	@SuppressWarnings("unused")
	private void initMockGpsProvider() {
		final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		try{
			locationManager.removeTestProvider(MockGpsProvider.GPS_MOCK_PROVIDER);
		}catch(Exception e1){
		}

		locationManager.addTestProvider(MockGpsProvider.GPS_MOCK_PROVIDER, false, false, false, false, false, false, false, android.location.Criteria.POWER_LOW, android.location.Criteria.ACCURACY_FINE);
		locationManager.setTestProviderEnabled(MockGpsProvider.GPS_MOCK_PROVIDER, true);

		if(locationManager.isProviderEnabled(MockGpsProvider.GPS_MOCK_PROVIDER)){
			/** Load mock GPS data from file and create mock GPS provider. */
			try{
				// create a list of Strings that can dynamically grow
				final List<String> data = new ArrayList<String>();

				/**
				 * read a CSV file containing WGS84 coordinates from the
				 * 'assets' folder (The website http://www.gpsies.com offers
				 * downloadable tracks. Select a track and download it as a CSV
				 * file. Then add it to your assets folder.)
				 */
				final InputStream is = getAssets().open("mock_gps_data.csv");
				final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

				// add each line in the file to the list
				String line = null;
				while((line = reader.readLine()) != null){
					data.add(line);
				}

				// convert to a simple array so we can pass it to the AsyncTask
				final String[] coordinates = new String[data.size()];
				data.toArray(coordinates);

				// create new AsyncTask and pass the list of GPS coordinates
				mMockGpsProviderTask = new MockGpsProvider();
				mMockGpsProviderTask.execute(coordinates);

				mMockGpsProviderInitialized = true;
			}catch(Exception e){
			}
		}

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// store where we are before closing the app, so we can skip to the location right away when restarting
		savedInstanceState.putInt(MOCK_GPS_PROVIDER_INDEX, mMockGpsProviderIndex);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(mMockGpsProviderInitialized){
			// stop the mock GPS provider by calling the 'cancel(true)' method
			mMockGpsProviderTask.cancel(true);
			mMockGpsProviderTask = null;

			// remove it from the location manager
			final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeTestProvider(MockGpsProvider.GPS_MOCK_PROVIDER);
		}
	}

	private class MockGpsProvider extends AsyncTask<String, Integer, Void> {
		public static final String LOG_TAG = "GpsMockProvider";
		public static final String GPS_MOCK_PROVIDER = LocationManager.GPS_PROVIDER; //"GpsMockProvider";

		/** Keeps track of the currently processed coordinate. */
		public Integer index = 0;

		@Override
		protected Void doInBackground(String... data) {
			// process data
			for(String str : data){
				// skip data if needed (see the Activity's savedInstanceState functionality)
				if(index < mMockGpsProviderIndex){
					index++;
					continue;
				}

				// let UI Thread know which coordinate we are processing
				publishProgress(index);

				// retrieve data from the current line of text
				Double latitude = null;
				Double longitude = null;
				Double altitude = null;
				try{
					String[] parts = str.split(",");
					latitude = Double.valueOf(parts[0]);
					longitude = Double.valueOf(parts[1]);
					altitude = Double.valueOf(parts[2]);
				}catch(NullPointerException e){
					break;
				} // no data available
				catch(Exception e){
					continue;
				} // empty or invalid line

				// translate to actual GPS location
				final Location location = new Location(GPS_MOCK_PROVIDER);
				location.setLatitude(latitude);
				location.setLongitude(longitude);
				location.setAltitude(altitude);
				location.setTime(System.currentTimeMillis());
				location.setAccuracy(5);
				location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

				// show debug message in log
				Log.d(LOG_TAG, location.toString());

				// provide the new location
				final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				locationManager.setTestProviderLocation(GPS_MOCK_PROVIDER, location);

				// sleep for a while before providing next location
				try{
					Thread.sleep(200);

					// gracefully handle Thread interruption (important!)
					if(Thread.currentThread().isInterrupted()){
						throw new InterruptedException("");
					}
				}catch(InterruptedException e){
					break;
				}

				// keep track of processed locations
				index++;
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.d(LOG_TAG, "onProgressUpdate():" + values[0]);
			mMockGpsProviderIndex = values[0];
		}
	}
}
