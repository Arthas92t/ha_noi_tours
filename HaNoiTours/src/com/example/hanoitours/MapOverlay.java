package com.example.hanoitours;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class MapOverlay extends com.google.android.maps.Overlay{
	private ArrayList<GeoPoint> myGeoPoint;

	public MapOverlay(ArrayList<GeoPoint> listGeoPoints){
		myGeoPoint = listGeoPoints;
	}
		
	public boolean draw(Canvas canvas, MapView mapView,boolean shadow,long when){
		super.draw(canvas, mapView, shadow);
		if(myGeoPoint == null)
			return true;
		Paint paint;
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(3);
		
		//Point pt1 = new Point();
		//Point pt2 = new Point();
		
		Point listPoints [] = new Point[myGeoPoint.size()];
		for(int i=0;i<myGeoPoint.size();i++){
			listPoints[i] = new Point();
		}
		Projection projection = mapView.getProjection();
		//projection.toPixels(mGpt1, pt1);
		//projection.toPixels(mgpt2, pt2);
		for(int i=0;i<myGeoPoint.size();i++){
			projection.toPixels(myGeoPoint.get(i), listPoints[i]);
		}
		for(int i=0;i<listPoints.length-1;i++){
			canvas.drawLine(listPoints[i].x, listPoints[i].y, listPoints[i+1].x, listPoints[i+1].y, paint);
		}
		//canvas.drawLine(pt1.x, pt1.y, pt2.x, pt2.y, paint);
		return true;
	}

}
