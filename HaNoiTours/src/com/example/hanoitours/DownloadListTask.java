package com.example.hanoitours;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;

public class DownloadListTask extends AsyncTask <String, Place, Integer>{
	
	private final int BASE = 1000000;
	private final String ID = "ID";
	private final String NAME = "NAME";
	
	PlaceList itemizedoverlay;
	
	public DownloadListTask(PlaceList itemizedoverlay) {
		super();
		this.itemizedoverlay = itemizedoverlay;
	}

	@Override
	protected Integer doInBackground(String... url) {
		for(int i = 0; i < url.length; i++)
			download(url[i]);
		return null;
	}
	
	private void download(String url){
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;
		try{
			response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			String obj = new String(entity.toString().getBytes(), "UTF16");
			JSONArray list= new JSONArray(obj);
			for(int i = 0; i < list.length(); i++){
				JSONObject place = list.getJSONObject(i);
				GeoPoint point = new GeoPoint(
						(int)(place.getInt("latitude") * BASE),
						(int)(place.getInt("longitude") * BASE)); 
				itemizedoverlay.addOverlay(new Place(
						place.getString(ID), place.getString(NAME), point));
			}
		}catch(Exception e){
			int a = 0;
			a = a /a;
		}
	}
}
