package com.tomtom.agriculture.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tomtom.agriculture.R;
import com.tomtom.agriculture.ctes.Constants;
import com.tomtom.agriculture.maps.MyTTGeometricLayer;
import com.tomtom.agriculture.ui.widgets.DiseableTTMapView;
import com.tomtom.agriculture.ui.widgets.TractorLocationMarker;
import com.tomtom.lbs.sdk.TTMapListener;
import com.tomtom.lbs.sdk.TTMapView;
import com.tomtom.lbs.sdk.traffic.TrafficPoi;
import com.tomtom.lbs.sdk.util.Coordinates;
import com.tomtom.lbs.sdk.util.SDKUtils.PointDouble;

public class MapFragment extends Fragment {

	private TTMapView mMapView;
	private View m_mp_cnt_curent_location;
	private TractorLocationMarker mLocationMarker;
	private Activity mActivity;

	final private List<Point> mPathLeft = new ArrayList<Point>();
	final private List<Point> mPathRight = new ArrayList<Point>();
	final private MyTTGeometricLayer mGeometricLayer = new MyTTGeometricLayer();

	private Location previousLocation;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mGeometricLayer.setPaths(mPathLeft, mPathRight);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_map, container, false);

		m_mp_cnt_curent_location = view.findViewById(R.id.mp_cnt_curent_location);
		m_mp_cnt_curent_location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
				if(mMapView != null){
					if(mLocationMarker != null){
						if((int) (mLocationMarker.getLocation().latitude * 10000) == (int) (mMapView.getMapCenter().latitude * 10000) &&
								(int) (mLocationMarker.getLocation().longitude * 10000) == (int) (mMapView.getMapCenter().longitude * 10000)){
							return;
						}

						mMapView.setMapZoom(TTMapView.MAXIMUM_ZOOMLEVEL);
						mMapView.setMapCenter(mLocationMarker.getLocation());
					}
				}
			}
		});

		mLocationMarker = new TractorLocationMarker(mActivity, mMapView);

		if(mMapView == null){
			mMapView = new DiseableTTMapView(mActivity);
			mMapView.setClustering(false);
			mMapView.setListener(new TTMapListener() {

				@Override
				public void onDoubleTap(Coordinates arg0) {}

				@Override
				public void onLocationChanged(double latitude, double longitude, float accuracy) {
					if(accuracy > 15){
						//discard non accurate locations
						return;
					}

					if(previousLocation != null){
						final int mapZoom = mMapView.getZoom();

						final Location currentLocation = new Location(LocationManager.GPS_PROVIDER);
						currentLocation.setLatitude(latitude);
						currentLocation.setLongitude(longitude);

						final float bearing = currentLocation.bearingTo(previousLocation);
						final double bearingRadians = Math.toRadians(bearing);
						mLocationMarker.setDegrees(bearing + 180);

						final Point point = computePosition(latitude, longitude, mapZoom);
						final Point pointLeft = new Point(
								(int) Math.round(point.x + Math.sin(bearingRadians + Constants.APARATO_ANGLE) * Constants.POINTS_HYPOTENUSE),
								(int) Math.round(point.y + Math.cos(bearingRadians + Constants.APARATO_ANGLE) * -Constants.POINTS_HYPOTENUSE));
						mPathLeft.add(pointLeft);

						final Point pointRight = new Point(
								(int) Math.round(point.x + Math.sin(bearingRadians - Constants.APARATO_ANGLE) * Constants.POINTS_HYPOTENUSE),
								(int) Math.round(point.y + Math.cos(bearingRadians - Constants.APARATO_ANGLE) * -Constants.POINTS_HYPOTENUSE));
						mPathRight.add(pointRight);
					}

					mMapView.setMapZoom(TTMapView.MAXIMUM_ZOOMLEVEL);
					mMapView.setMapCenter(latitude, longitude);

					previousLocation = new Location(LocationManager.GPS_PROVIDER);
					previousLocation.setLatitude(latitude);
					previousLocation.setLongitude(longitude);
				}

				@Override
				public void onLongPress(Coordinates arg0) {}

				@Override
				public boolean onMapMove() {
					return false;
				}

				@Override
				public boolean onMapTouched(MotionEvent arg0, Point arg1) {
					return false;
				}

				@Override
				public boolean onMapZoom() {
					return false;
				}

				@Override
				public void onScroll() {}

				@Override
				public void onSingleTap(Coordinates arg0) {}

				@Override
				public View trafficBalloonCallback(TrafficPoi arg0, PointDouble arg1) {
					return null;
				}

				@Override
				public void trafficBalloonTap(TrafficPoi arg0) {}

				@Override
				public Drawable trafficIconCallBack(TrafficPoi arg0) {
					return null;
				}

				@Override
				public void trafficIncidentTap(TrafficPoi arg0) {}
			});
		}else{
			((FrameLayout) mMapView.getParent()).removeView(mMapView);
		}

		mMapView.setLocationMarker(mLocationMarker);

		final FrameLayout parentView = (FrameLayout) view.findViewById(R.id.mp_cnt_map);
		parentView.addView(mMapView);

		view.findViewById(R.id.zoomIn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
				if(mMapView != null){
					mMapView.zoomIn(false);
				}
			}
		});

		view.findViewById(R.id.zoomOut).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
				if(mMapView != null){
					mMapView.zoomOut(false);
				}
			}
		});

		mMapView.addGeometricLayer(mGeometricLayer);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		mMapView.setShowGPSLocation(true);
	}

	@Override
	public void onPause() {
		super.onPause();

		mMapView.setShowGPSLocation(false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mMapView.trimCache();
		mLocationMarker.removeParent();
	}

	//	private Coordinates getLongitudeLatitude(int x, int y, int zoom) {
	//		return new Coordinates(position2lat(y, zoom), position2lon(x, zoom));
	//	}

	private Point computePosition(double lat, double lon, int zoom) {
		int x = lon2position(lon, zoom);
		int y = lat2position(lat, zoom);
		return new Point(x, y);
	}

	//
	//	private double position2lon(int x, int z) {
	//		double xmax = 256 * (1 << z);
	//		return x / xmax * 360.0 - 180;
	//	}
	//
	//	private double position2lat(int y, int z) {
	//		double ymax = 256 * (1 << z);
	//		return Math.toDegrees(Math.atan(Math.sinh(Math.PI - (2.0 * Math.PI * y) / ymax)));
	//	}

	private int lon2position(double lon, int z) {
		double xmax = 256 * (1 << z);
		return (int) Math.floor((lon + 180) / 360 * xmax);
	}

	private int lat2position(double lat, int z) {
		double ymax = 256 * (1 << z);
		return (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1
				/ Math.cos(Math.toRadians(lat)))
				/ Math.PI)
				/ 2 * ymax);
	}
}
