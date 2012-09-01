package com.example.hanoitours;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceDetail extends Activity {
	
	private final String TAG= "PlaceDetail";
	private final String URL = "http://hanoitour.herokuapp.com/places/";
	
	private GetPlaceInfoTask getInfoTask;
	private Place place;
	private PlaceList placeList;
	private AccountManager accountManager;
	String work;
	PlaceInfo placeInfo;
	Account account;
	Bundle options = new Bundle();
    ProgressDialog progDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        placeList = MainActivity.placeList;
        place = placeList.getPlaceList().get(getIntent().getIntExtra("TEST", 0));
        String url = URL + place.id + ".json";
        getInfoTask = new GetPlaceInfoTask(this);
        placeInfo = null;
        getInfoTask.execute(url);
        showInfo();
    }
    
    public void showInfo(){
        setContentView(R.layout.activity_place_detail);
        if(MainActivity.listGeo.mOverlays.contains(place)){
            CheckBox checkBox = (CheckBox) findViewById(R.id.mark_place);
            checkBox.setChecked(true);
        }
       	showDialog(0);
       	if(placeInfo != null){
       		updateUI(placeInfo);
       	}
    }

    public void updateUI(PlaceInfo placeInfo){
    	dismissDialog(0);
    	this.placeInfo = placeInfo;
    	String source = "<b>" + placeInfo.name + "</b><br>";
    	source = source + "<b>" + placeInfo.address + "</b><br>";
    	WebView webview = (WebView) findViewById(R.id.place_detail);
    	webview.loadData(
    			Html.toHtml(Html.fromHtml(source + placeInfo.info)),
    			"text/html; charset=utf-8",
    			null);
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
        
    public void mark(View view){
        CheckBox checkBox = (CheckBox) findViewById(R.id.mark_place);
        if(!checkBox.isChecked()){
        	MainActivity.listGeo.remove(place);
        	MainActivity.map.invalidate();
            Log.e("aaaaa",""+ MainActivity.listGeo.mOverlays.size());
            finish();
            return;
        }
        MainActivity.listGeo.addOverlay(place);
    	MainActivity.map.invalidate();
        Log.e("aaaaa",""+ MainActivity.listGeo.mOverlays.size());
        finish();
    }

    public void showComment(String source){
    	dismissDialog(2);
    	setContentView(R.layout.view_comment);
    	TextView comment = (TextView) findViewById(R.id.comment);
    	String allComment = 
    			placeInfo.comment.length() + 
    			" comments<br>" + 
    			"Rating: " + placeInfo.rate +"<br>";
    	allComment = allComment + source.substring(
    			source.lastIndexOf("Comments:"),
    			source.lastIndexOf("Rates:"));
    	allComment = allComment.replaceAll("Destroy", "");
    	comment.setText(Html.fromHtml(allComment));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.menu_show_info:
        	showInfo();
        	return true;
        case R.id.menu_view_comment:
        	showDialog(2);
        	this.work = "comment";
        	(new GetDataWebTask(this)).execute(place.id);
        	return true;
        case R.id.menu_post_comment:
        	accountManager = AccountManager.get(this);
        	Account[] listAccount = accountManager.getAccountsByType("com.google");

        	if (listAccount.length ==0){
        		Toast.makeText(PlaceDetail.this, "You must have a google account", Toast.LENGTH_SHORT).show();
        		return true;
        	}
        	account = listAccount[0];
        	setContentView(R.layout.post_comment);
        	showDialog(2);
        	this.work = "rate";
        	(new GetDataWebTask(this)).execute(place.id);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
    	}
    }
    
    public void post(View view){
    	String comment  = ((EditText) findViewById(R.id.input_comment)).getText().toString();
    	String rate = "" + ((RatingBar) findViewById(R.id.input_rate)).getRating();
    	
    	accountManager.getAuthToken(
    			account,
    			"oauth2:https://www.googleapis.com/auth/userinfo.email",
    			options,
    			this,
    			new ResetTokenAndPost(this, comment, rate),
    			new Handler());
    	showDialog(1);
    }

	@Override
    protected Dialog onCreateDialog(int id) {
		progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setMessage("Loading...");
		return progDialog;
	}
	
	public void endPost(){
		dismissDialog(1);
		Toast.makeText(PlaceDetail.this, "your comment was posted", Toast.LENGTH_SHORT).show();
		showInfo();
	}
	
	public void updateRate(String source){
		Double doubleRate = placeInfo.rate; 
		try{
			int i = source.lastIndexOf("Rates:");
			String flag_account = account.name;
			
			i = source.indexOf(flag_account, i);
			Log.e(TAG, flag_account);
			String rate = source.substring(
					i + flag_account.length(),
					source.indexOf("<",i + flag_account.length()));
			
			rate = rate.trim();
			doubleRate = new Double(rate);
		}catch(NumberFormatException e){
			Log.e(TAG, ""+e);
		}

		float floatRate = (int)(doubleRate* 10);
    	((RatingBar) findViewById(R.id.input_rate)).setRating(floatRate/10);
    	dismissDialog(2);		
	}
	
	private class ResetTokenAndPost implements AccountManagerCallback<Bundle> {
		PlaceDetail activity;
		String comment;
		String rate;
		public ResetTokenAndPost(PlaceDetail activity, String comment, String rate){
			super();
			this.activity = activity;
			this.comment = comment;
			this.rate = rate;
		}
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle = null;
			try{
				bundle= result.getResult();
			}catch(Exception e){
				return;
			}
	        // The token is a named value in the bundle. The name of the value
	        // is stored in the constant AccountManager.KEY_AUTHTOKEN.
	        String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
	        PlaceDetail.this.accountManager.invalidateAuthToken(
	    			"com.google", token);
	    	accountManager.getAuthToken(
	    			account,
	    			"oauth2:https://www.googleapis.com/auth/userinfo.email",
	    			options,
	    			activity,
	    			new GetTokenAndPost(activity, comment, rate),
	    			new Handler());
			
	    }		
	}

	private class GetTokenAndPost implements AccountManagerCallback<Bundle> {
		PlaceDetail activity;
		String comment;
		String rate;
		public GetTokenAndPost(PlaceDetail activity, String comment, String rate){
			super();
			this.activity = activity;
			this.comment = comment;
			this.rate = rate;
		}
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle = null;
			try{
				bundle= result.getResult();
			}catch(Exception e){
				return;
			}
	        // The token is a named value in the bundle. The name of the value
	        // is stored in the constant AccountManager.KEY_AUTHTOKEN.
	        String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
	    	ArrayList<String> data = new ArrayList<String>();
	    	data.add(place.id);
	    	data.add(token);
	    	data.add(comment);
	    	data.add(rate);    	
	    	(new PostCommentTask(PlaceDetail.this)).execute(data);
	    }		
	}	
}