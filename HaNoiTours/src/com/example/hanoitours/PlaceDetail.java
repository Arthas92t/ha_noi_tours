package com.example.hanoitours;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceDetail extends Activity {
	
	private final String TAG= "PlaceDetail";
	private final String URL = "http://hanoitour.herokuapp.com/places/";
	
	private TextView text;
	private GetPlaceInfoTask getInfoTask;
	private GetImageTask getImageTask;
	private Place place;
	private PlaceList placeList;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        placeList = MainActivity.placeList;
        place = placeList.getPlaceList().get(getIntent().getIntExtra("TEST", 0));
        String url = URL + place.id + ".json";
        text = (TextView) findViewById(R.id.place_name);
        getInfoTask = new GetPlaceInfoTask(this);
        getInfoTask.execute(url);
        if(MainActivity.listGeo.contains(place.point)){
            CheckBox checkBox = (CheckBox) findViewById(R.id.mark_place);
            checkBox.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	getInfoTask.cancel(true);
    }
    
    public void updateUI(PlaceInfo placeInfo){
    	text.setText(Html.fromHtml(placeInfo.info));
    	getImageTask = new GetImageTask(this);
    	getImageTask.execute(placeInfo.image);
    }

    @SuppressWarnings("deprecation")
	public void updateImage(Drawable image){
    	ImageView imageView = (ImageView) findViewById(R.id.place_image);
    	imageView.setBackgroundDrawable(image);
    }
    
    public void mark(View view){
        CheckBox checkBox = (CheckBox) findViewById(R.id.mark_place);
        if(!checkBox.isChecked()){
        	MainActivity.listGeo.remove(place.point);
        	return;
        }
        MainActivity.listGeo.add(place.point);
    }
}
