package com.example.hanoitours;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Place extends OverlayItem {
	
	String name;
	String point;
	public Place(String id, String name, GeoPoint point) {
		super(point, "", "");
		
	}
}
