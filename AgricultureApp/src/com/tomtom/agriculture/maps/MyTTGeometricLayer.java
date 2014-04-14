package com.tomtom.agriculture.maps;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;

import com.tomtom.lbs.sdk.TTGeometricLayer;

public class MyTTGeometricLayer extends TTGeometricLayer {

	final private Matrix drawMatrix = new Matrix();
	final private float[] matrixValues = new float[9];

	private List<Point> pathLeft;
	private List<Point> pathRight;

	public void setPaths(List<Point> pathLeft, List<Point> pathRight) {
		this.pathLeft = pathLeft;
		this.pathRight = pathRight;
	}

	@Override
	public void draw(Canvas canvas, Point mapPosition, Matrix matrix, Paint paint) {
		if(pathLeft == null || pathRight == null){
			return;
		}

		paint.reset();
		paint.setAntiAlias(true);
		paint.setColor(Color.parseColor("#CCE5743B"));
		paint.setStyle(Style.FILL);

		for(int i = 0; i < pathLeft.size() - 1; i++){
			drawSegment(canvas, matrix, mapPosition, pathLeft.get(i), pathLeft.get(i + 1), pathRight.get(i), pathRight.get(i + 1), paint);
		}
	}

	private void drawSegment(Canvas canvas, Matrix matrix, Point mapPosition, Point fromLeft, Point toLeft, Point fromRight, Point toRight, Paint paint) {
		final Point fLeft = new Point(fromLeft.x, fromLeft.y);
		fLeft.x -= mapPosition.x;
		fLeft.y -= mapPosition.y;
		drawMatrix.reset();
		drawMatrix.postTranslate(fLeft.x, fLeft.y);
		drawMatrix.postConcat(matrix);
		drawMatrix.getValues(matrixValues);
		fLeft.x = (int) matrixValues[Matrix.MTRANS_X];
		fLeft.y = (int) matrixValues[Matrix.MTRANS_Y];

		final Point tLeft = new Point(toLeft.x, toLeft.y);
		tLeft.x -= mapPosition.x;
		tLeft.y -= mapPosition.y;
		drawMatrix.reset();
		drawMatrix.postTranslate(tLeft.x, tLeft.y);
		drawMatrix.postConcat(matrix);
		drawMatrix.getValues(matrixValues);
		tLeft.x = (int) matrixValues[Matrix.MTRANS_X];
		tLeft.y = (int) matrixValues[Matrix.MTRANS_Y];

		final Point fRight = new Point(fromRight.x, fromRight.y);
		fRight.x -= mapPosition.x;
		fRight.y -= mapPosition.y;
		drawMatrix.reset();
		drawMatrix.postTranslate(fRight.x, fRight.y);
		drawMatrix.postConcat(matrix);
		drawMatrix.getValues(matrixValues);
		fRight.x = (int) matrixValues[Matrix.MTRANS_X];
		fRight.y = (int) matrixValues[Matrix.MTRANS_Y];

		final Point tRight = new Point(toRight.x, toRight.y);
		tRight.x -= mapPosition.x;
		tRight.y -= mapPosition.y;
		drawMatrix.reset();
		drawMatrix.postTranslate(tRight.x, tRight.y);
		drawMatrix.postConcat(matrix);
		drawMatrix.getValues(matrixValues);
		tRight.x = (int) matrixValues[Matrix.MTRANS_X];
		tRight.y = (int) matrixValues[Matrix.MTRANS_Y];

		final Path path = new Path();
		path.moveTo(fLeft.x, fLeft.y);
		path.lineTo(tLeft.x, tLeft.y);
		path.lineTo(tRight.x, tRight.y);
		path.lineTo(fRight.x, fRight.y);

		canvas.drawPath(path, paint);
	}

}
