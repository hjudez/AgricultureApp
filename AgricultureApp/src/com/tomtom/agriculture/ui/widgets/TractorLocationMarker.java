package com.tomtom.agriculture.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.tomtom.agriculture.R;
import com.tomtom.lbs.sdk.TTMapView;
import com.tomtom.lbs.sdk.TTMarker;
import com.tomtom.lbs.sdk.util.SDKUtils.PointDouble;

public class TractorLocationMarker extends TTMarker {

	private ChevronDrawable drawable;
	private TTMapView mapView;

	public TractorLocationMarker(Context context, TTMapView mapView) {
		super(null, new PointDouble(0.5, 0.5));
		this.mapView = mapView;
		drawable = new ChevronDrawable(context);
		setMarkerDrawable(drawable);
	}

	public void setDegrees(float degrees) {
		drawable.degrees = degrees;
		if(mapView != null){
			mapView.invalidate();
		}
	}

	public void removeParent() {
		mapView = null;
	}

	public class ChevronDrawable extends Drawable {

		private Bitmap chevron;
		private float degrees;

		public ChevronDrawable(Context context) {
			chevron = BitmapFactory.decodeResource(context.getResources(), R.drawable.ui_tractor);
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.save();
			Rect bounds = getBounds();
			canvas.rotate(degrees, bounds.centerX(), bounds.centerY());
			canvas.drawBitmap(chevron, bounds.centerX() - (chevron.getWidth() / 2), bounds.centerY() - (chevron.getHeight() / 2), null);
			canvas.restore();
		}

		@Override
		public int getOpacity() {
			return 0;
		}

		@Override
		public void setAlpha(int arg0) {}

		@Override
		public void setColorFilter(ColorFilter arg0) {}

		public void setDegrees(float d) {
			degrees = d;
		}
	}
}