package com.tomtom.agriculture;

import android.app.Application;
import android.content.Context;

import com.tomtom.lbs.sdk.util.SDKContext;

public class TTAgriculture extends Application {

	private static TTAgriculture instance;

	public TTAgriculture() {
		TTAgriculture.instance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SDKContext.setDeveloperKey("2rqzba9uj99pxazbyatpz6xc");
	}

	public static Context getContext() {
		return TTAgriculture.instance;
	}
}