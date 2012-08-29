package com.example.hanoitours;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;

public class OauthActivity extends Activity {
	
	private Bundle options = new Bundle();
	private AccountManager accountManager;
	private String test = "";
	private String token = null;
	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
		OauthActivity activity;
		
		public OnTokenAcquired(OauthActivity activity){
			super();
			this.activity = activity;
		}
		public void run(AccountManagerFuture<Bundle> result) {
	    }		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        accountManager = AccountManager.get(this);
        Account[] listAccount = accountManager.getAccountsByType("com.google");
        EditText text = (EditText) findViewById(R.id.list_account);
        for(int i = 0; i < listAccount.length; i++){
        	test = test + listAccount[i].name + "\n";
        }
        text.setText(test);
        
    	accountManager.invalidateAuthToken(
    			"com.google", "ya29.AHES6ZSIDlQv1WSmo8OtubVpStLeBMQmiiqyrDWFnn60hcw");
    	requestToken(listAccount[1]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_oauth, menu);
        return true;
    }

    private void requestToken(Account account){
    	token = getToken(accountManager.getAuthToken(
    			account,
    			"oauth2:https://www.googleapis.com/auth/userinfo.email",
    			options,
    			this,
    			new OnTokenAcquired(this),
    			new Handler()));
    	ArrayList<String> data = new ArrayList<String>();
    	Log.e("zzzzzzzzzzz", token);
    	data.add(token);
    	data.add("deshrej");
    	data.add("5");    	
    	(new PostCommentTask()).execute(data);
    }
    
    public String getToken(AccountManagerFuture<Bundle> result){
		Bundle bundle = null;
		try{
			bundle= result.getResult();
		}catch(Exception e){
			return null;
		}
        // The token is a named value in the bundle. The name of the value
        // is stored in the constant AccountManager.KEY_AUTHTOKEN.
        token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
        EditText text = (EditText) findViewById(R.id.list_account);
    	test = test + token + "\n";	   
    	text.setText(test);
    	return token;
    }
}
