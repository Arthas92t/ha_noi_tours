package com.example.hanoitours;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;

public class PlaceDetail extends Activity {
	
	private final String TAG= "PlaceDetail";
	private final String URL = "http://hanoitour.herokuapp.com/places/";
	
	private GetPlaceInfoTask getInfoTask;
	private Place place;
	private PlaceList placeList;
	private PlaceInfo placeInfo;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        placeList = MainActivity.placeList;
        place = placeList.getPlaceList().get(getIntent().getIntExtra("TEST", 0));
        String url = URL + place.id + ".json";
        getInfoTask = new GetPlaceInfoTask(this);
        placeInfo = null;
        getInfoTask.execute(url);
        if(MainActivity.listGeo.contains(place.point)){
            CheckBox checkBox = (CheckBox) findViewById(R.id.mark_place);
            checkBox.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_place_detail, menu);
        return true;
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	getInfoTask.cancel(true);
    }
    
    public void updateUI(PlaceInfo placeInfo){
    	this.placeInfo = placeInfo;
    	String source = "<b>" + placeInfo.name + "</b><br>";
    	source = source + "<b>" + placeInfo.address + "</b><br>";
    	WebView webview = (WebView) findViewById(R.id.place_detail);
    	webview.loadData(
    			Html.toHtml(Html.fromHtml(source + placeInfo.info)),
    			"text/html; charset=utf-8",
    			null);
    }
    
    public void mark(View view){
        CheckBox checkBox = (CheckBox) findViewById(R.id.mark_place);
        if(!checkBox.isChecked()){
        	MainActivity.listGeo.remove(place.point);
        	return;
        }
        MainActivity.listGeo.add(place.point);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.menu_view_comment:
        	showComment();
        	return true;
        case R.id.menu_post_comment:
        	setContentView(R.layout.post_comment);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
    	}    
    }
    
    private void showComment(){
    	if(placeInfo == null)
    		return;
    	setContentView(R.layout.view_comment);
    	TextView comment = (TextView) findViewById(R.id.comment);
    	String allComment = 
    			placeInfo.comment.length() + 
    			" comments\n" + 
    			"Rating: " + placeInfo.rate +"\n\n";
    	for(int i = 0; i < placeInfo.comment.length(); i++){
    		try{
	    		allComment = allComment + placeInfo.comment.
	    				getJSONObject(i).
	    				getString("user_id");
	    		allComment = allComment + ": " + placeInfo.comment.
	    				getJSONObject(i).
	    				getString("content") + "\n";
    		}catch (Exception e) {
			}
    	}
    	comment.setText(allComment);
    }
}
