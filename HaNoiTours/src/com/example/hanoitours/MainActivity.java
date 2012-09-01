package com.example.hanoitours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MainActivity extends MapActivity {
	
	private final static String URl_LIST_PLACE = "http://hanoitour.herokuapp.com/places.json";
	
	static MarkedList listGeo;
	static PlaceList placeList;

	static MapView map;

	private MyLocationOverlay location;
	private MapOverlay mapDirection;
	private ProgressDialog progDialog;

	
	private class TrackLocation implements Runnable{
		public TrackLocation() {
		}
		
		public void run(){
			map.getController().animateTo(location.getMyLocation());
		}
    }
	
	public void changeMap(String area){
		MapView mapView =(MapView) findViewById(R.id.mapview);
		MapController mc = mapView.getController();
		
		GeoPoint myLocation = null;
		double lat = 21027395;
		double lng = 105835143;
		try{
			Geocoder g = new Geocoder(this, Locale.getDefault());
			java.util.List<android.location.Address> result=g.getFromLocationName(area , 1);
			if(result.size() > 0){
				Toast.makeText(MainActivity.this, "Country: " + String.valueOf(result.get(0).getCountryName()) , Toast.LENGTH_SHORT).show();
				lat= result.get(0).getLatitude();
				lng= result.get(0).getLongitude();
			}
			else{
				Toast.makeText(MainActivity.this, "not found", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		catch(IOException io){
			Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
		}
		myLocation = new GeoPoint(
				(int) (lat * 1E6),
				(int) (lng * 1E6)
				);
		mc.animateTo(myLocation);
		mc.setZoom(12);
		mapView.invalidate();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        map = (MapView) findViewById(R.id.mapview);
        map.setBuiltInZoomControls(true);
        configMap(map);
            
        Drawable drawable = this.getResources().getDrawable(R.drawable.mapflag);
        placeList = new PlaceList(drawable, this);
        map.getOverlays().add(placeList);

        Drawable drawable1 = this.getResources().getDrawable(R.drawable.marker);
        listGeo = new MarkedList(drawable1, this);
        map.getOverlays().add(listGeo);

        location = new MyLocationOverlay(this, map);
        map.getOverlays().add(location);
                
        GetPlaceListTask getPlaceListTask = new GetPlaceListTask(placeList, map);
        getPlaceListTask.execute(URl_LIST_PLACE);

        Button SearchButton = (Button) findViewById(R.id.search_button);

        SearchButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				EditText textSearch = (EditText) findViewById(R.id.editText1);
				String area = textSearch.getText().toString();
				MainActivity.this.changeMap(area);
			}
		});
        GeoPoint point2 = new GeoPoint(21027395,105835143);
        map.getController().animateTo(point2);
	}
    
    public void draw(ArrayList<GeoPoint> listGeo){
    	dismissDialog(1);
    	mapDirection = new MapOverlay(listGeo);
        map.getOverlays().add(mapDirection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.menu_switch_view_mode:
        	map.setSatellite(!map.isSatellite());
            return true;
        case R.id.menu_direction:
        	suggestDirection();
        	showDialog(1);
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
    
    private void suggestDirection(){
    	 if(listGeo.size()<=1){
    		    Toast.makeText(MainActivity.this, "You need chose at least 2 place !!!!", Toast.LENGTH_SHORT).show();
    		    return;
    	 }
    	 map.getOverlays().remove(mapDirection);
        (new GetDirectionsTask(this)).execute(listGeo.getListGeoPoint());
    	
    }
    private void configMap(MapView map) {
		map.setSatellite(Setting.satelline);
		map.setTraffic(Setting.traffic);		
	}
    
    public void getLocation(View view){
        location.runOnFirstFix(new TrackLocation());
    }
    
	@Override
    protected Dialog onCreateDialog(int id) {
		progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setMessage("Loading...");
		return progDialog;
	}
	
}