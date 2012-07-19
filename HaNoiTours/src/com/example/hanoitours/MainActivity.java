package com.example.hanoitours;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class MainActivity extends MapActivity {
	List<Overlay> mapOverlays;
	MapView map;
	MyLocationOverlay location;

	private class TrackLocation implements Runnable{
		public TrackLocation() {
		}
		
		public void run(){
			map.getController().animateTo(location.getMyLocation());
		}
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        map = (MapView) findViewById(R.id.mapview);
        map.setBuiltInZoomControls(true);
        configMap(map);
            
        mapOverlays = map.getOverlays();

        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        PlaceList itemizedoverlay = new PlaceList(drawable, this);
        mapOverlays.add(itemizedoverlay);

        GeoPoint point = new GeoPoint(21631899,105297546);
        Place overlayitem = new Place(point, "aaa", "bbb");
        itemizedoverlay.addOverlay(overlayitem);
        
        location = new MyLocationOverlay(this, map);
        mapOverlays.add(location);
        
        map.getController().setCenter(point);
        map.getController().setZoom(10);
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
    	location.enableCompass();
    	location.enableMyLocation();
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	location.disableCompass();
    	location.disableMyLocation();
    }
    
    private void configMap(MapView map) {
		map.setSatellite(Setting.satelline);
		map.setTraffic(Setting.traffic);		
	}
    
    public void getLocation(View view){
        location.runOnFirstFix(new TrackLocation());
    }
}
