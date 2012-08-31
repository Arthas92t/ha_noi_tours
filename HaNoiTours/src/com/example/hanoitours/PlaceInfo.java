package com.example.hanoitours;

import org.json.JSONArray;

import android.util.Log;

public class PlaceInfo {
	
	private String TAG ="PlaceInfo";
	public String name;
	public String address;
	public String image;
	public String info;
	public JSONArray comment;
	public double rate;
	public PlaceInfo(String name, String address, String image, String info, JSONArray comment, double rate){
		if(name == null){
			Log.w(TAG, "name is null");
			name = "";
		}
		if(address == null){
			Log.w(TAG, "address is null");
			address = "";
		}
		if(image == null){
			Log.w(TAG, "image is null");
			image = "";
		}
		if(info == null){
			Log.w(TAG, "info is null");
			info = "";
		}
		info = info.replaceAll("\"/IMGUPLOADS","\"/imguploads");
		info = info.replaceAll("\"/imguploads", "\"http://www.vietnamtourism.com/imguploads");
		info = info.replaceAll("\"../imguploads", "\"http://www.vietnamtourism.com/imguploads");
		info = info.replaceAll("\"../../imguploads", "\"http://www.vietnamtourism.com/imguploads");
		this.name = name;
		this.address = address;
		this.image = image;
		this.info = info;
		this.comment = comment;
		this.rate = rate;
	}
}
