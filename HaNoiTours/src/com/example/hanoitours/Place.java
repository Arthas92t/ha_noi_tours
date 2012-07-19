package com.example.hanoitours;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Place extends OverlayItem {

	public Place(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}
}
