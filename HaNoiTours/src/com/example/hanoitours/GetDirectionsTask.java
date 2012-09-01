package com.example.hanoitours;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class GetDirectionsTask extends AsyncTask <ArrayList<GeoPoint>, Integer, ArrayList<GeoPoint>>{

	private String TAG ="GetDirectionsTask";
	
	private String URL = "http://maps.googleapis.com/maps/api/directions/json?origin=";
	private String ROUTES = "routes";
	private int BASE = 1000000;
	
	private MainActivity currentActivity;
	
	public GetDirectionsTask(MainActivity activity) {
		super();
		this.currentActivity = activity;
	}

	@Override
	protected ArrayList<GeoPoint> doInBackground(ArrayList<GeoPoint>... arg0) {
		return decodePoly(getDirections(requestDirections(makeUrl(arg0[0]))));
	}
	
	@Override
	protected void onPostExecute(ArrayList<GeoPoint> result) {
		currentActivity.draw(result);
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
	
	private Double reFloatE6(int x){
		return ((double)x)/BASE;
	}
	
	private String pointToString(GeoPoint point){
		String result = "";
		result = result + reFloatE6(point.getLatitudeE6()).toString()
				+ "," + reFloatE6(point.getLongitudeE6()).toString();
		return result;
	}
	
	private String makeUrl(ArrayList<GeoPoint> arg0){
		if (arg0.size() < 2)
				return null;
		String url = URL + pointToString(arg0.get(0))
				+ "&destination=" + pointToString(arg0.get(arg0.size()-1))
				+ "&waypoints=optimize:false";
		for(int i = 1; i < (arg0.size() - 1); i++)
			url = url + "%7C" + pointToString(arg0.get(i));
		url = url + "&sensor=false&mode=driving";
		return url;
	}
	
	private JSONObject requestDirections(String url){
		if(url == null){
			return null;
		}
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		try{
			url = new String(url.getBytes(),"UTF-8");
			Log.e(TAG, url);
			httpget = new HttpGet(url);
		}catch(Exception e){
			
		}
		HttpResponse response;
		try{
			response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			JSONObject directions = new JSONObject(
					this.streamToString(entity.getContent()));
			return directions;
		}catch(JSONException e){
			Log.e(TAG, "JSONException at requestDirections" + e);
		}catch(IOException e){
			Log.e(TAG, "IOException at requestDirections" + e);
			return requestDirections(url);
		}
		return null;
	}
	
	private String getDirections(JSONObject direction){
		if(direction == null)
			return null;
		try{
			JSONObject Polyline = direction.getJSONArray(ROUTES).getJSONObject(0).getJSONObject("overview_polyline");
			return Polyline.getString("points");
		}catch(JSONException e){
			Log.e(TAG, "JSONException " + e);
		}
		return null;
	}

	private ArrayList<GeoPoint> decodePoly(String encoded) {

		if(encoded == null){
			return null;
		}
	    ArrayList<GeoPoint> poly = new ArrayList<GeoPoint>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
	             (int) (((double) lng / 1E5) * 1E6));
	        poly.add(p);
	    }

	    return poly;
	}
}
