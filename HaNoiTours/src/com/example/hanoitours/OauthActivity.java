package com.example.hanoitours;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class OauthActivity extends Activity {
	
	private Bundle options = new Bundle();
	private AccountManager accountManager;
	private String test = "";
	
	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {

		public void run(AccountManagerFuture<Bundle> result) {
			Log.e("AAAA","aaaaa");
			Bundle bundle = null;
			try{
				bundle= result.getResult();
			}catch(Exception e){
				return;
			}
	        // The token is a named value in the bundle. The name of the value
	        // is stored in the constant AccountManager.KEY_AUTHTOKEN.
	        String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
	        TextView text = (TextView) findViewById(R.id.list_account);
        	test = test + token + "\n";	   
        	text.setText(test);
	    }		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        accountManager = AccountManager.get(this);
        Account[] listAccount = accountManager.getAccountsByType("com.google");
        TextView text = (TextView) findViewById(R.id.list_account);
        for(int i = 0; i < listAccount.length; i++){
        	test = test + listAccount[i].name + "\n";
        }
        text.setText(test);
        
        requestToken(listAccount[0]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_oauth, menu);
        return true;
    }

    private void requestToken(Account account){
    	accountManager.getAuthToken(
    			account,
    			"test",
    			options,
    			this,
    			new OnTokenAcquired(),
    			new Handler());
    }
}
