package com.example.hanoitours;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class GetDataWebTask extends AsyncTask <String, Integer, String>{

	private String TAG ="GetRateTask";
	
	private PlaceDetail currentActivity;
	public GetDataWebTask(PlaceDetail activity) {
		super();
		this.currentActivity = activity;
	}

	@Override
	protected String doInBackground(String... arg0) {
		return getRate(arg0[0]);
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(currentActivity.work == "rate")
			currentActivity.updateRate(result);
		if(currentActivity.work == "comment")
			currentActivity.showComment(result);
		
//		(new LoadImageTask(currentActivity)).execute(result.image);
	}
	
	private String streamToString(InputStream input){
		BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
        String content = "";
        String temp = null;
        try {
            while ((temp = buffer.readLine()) != null) {
                content = content + temp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
	}
	
	private String getRate(String id){
		HttpClient client = new DefaultHttpClient();
		id = "http://hanoitour.herokuapp.com/places/" +  id;
		HttpGet httpget = new HttpGet(id);
		HttpResponse response;
		try{
			response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			String source = streamToString(entity.getContent());
			//need to be edited
			return(source);
		}catch(Exception e){
			return getRate(id);						
		}
	}
}
