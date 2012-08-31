package com.example.hanoitours;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class PlaceDetail extends Activity {
	
	private final String TAG= "PlaceDetail";
	private final String URL = "http://hanoitour.herokuapp.com/places/";
	
	private GetPlaceInfoTask getInfoTask;
	private Place place;
	private PlaceList placeList;
	private PlaceInfo placeInfo;
	private AccountManager accountManager;
	private Account account;
	Bundle options = new Bundle();
	
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
    
    public void post(View view){
    	String comment  = ((EditText) findViewById(R.id.input_comment)).getText().toString();
    	String rate = ((EditText) findViewById(R.id.input_rate)).getText().toString();
    	
    	accountManager = AccountManager.get(this);
    	Account[] listAccount = accountManager.getAccountsByType("com.google");

    	if (listAccount.length ==0){
    		return;
    	}
    	account = listAccount[0];
    	accountManager.getAuthToken(
    			account,
    			"oauth2:https://www.googleapis.com/auth/userinfo.email",
    			options,
    			this,
    			new ResetTokenAndPost(this, comment, rate),
    			new Handler());
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
	    	(new PostCommentTask()).execute(data);
	    }		
	}
}
