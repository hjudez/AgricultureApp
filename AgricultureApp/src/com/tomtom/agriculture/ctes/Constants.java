package com.tomtom.agriculture.ctes;

public class Constants {

	public static final int POINTS_TO_APARATO = 5;
	public static final int POINTS_HALF_WIDTH = 5;
	public static final int POINTS_HYPOTENUSE = (int) Math.sqrt(POINTS_TO_APARATO * POINTS_TO_APARATO + POINTS_HALF_WIDTH * POINTS_HALF_WIDTH);

	public static final double APARATO_ANGLE = Math.atan(POINTS_HALF_WIDTH / POINTS_TO_APARATO);

}
