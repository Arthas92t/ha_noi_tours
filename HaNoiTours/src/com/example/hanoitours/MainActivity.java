package com.example.hanoitours;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MainActivity extends MapActivity {
	List<Overlay> mapOverlays;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        MapView map = (MapView) findViewById(R.id.mapview);
        map.setBuiltInZoomControls(true);
        configMap(map);
        
        mapOverlays = map.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        PlaceList itemizedoverlay = new PlaceList(drawable, this);
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);
        
        MyLocationOverlay location = new MyLocationOverlay(this, map);
        location.enableMyLocation();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.menu_settings:
        	Intent intent = new Intent(this, SettingActivity.class);
        	startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
    	}    
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    protected void onResume (){
    	super.onResume();
    	MapView map = (MapView) findViewById(R.id.mapview);
    	configMap(map);
    }
    
    private void configMap(MapView map) {
		map.setSatellite(Setting.satelline);
		map.setTraffic(Setting.traffic);		
	}
}
