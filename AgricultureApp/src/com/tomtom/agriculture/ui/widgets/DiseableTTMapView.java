package com.tomtom.agriculture.ui.widgets;

import android.content.Context;
import android.view.MotionEvent;

import com.tomtom.lbs.sdk.TTMapView;

public class DiseableTTMapView extends TTMapView {

	private boolean enabled;

	public DiseableTTMapView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(this.enabled){
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if(this.enabled){
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	public void setTTMapViewEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
