package com.example.hanoitours;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

public class PostCommentTask extends AsyncTask <ArrayList<String>, Place, String>{
	private static String TAG = "PostCommentTask"; 	
	private String url = "http://hanoitour.herokuapp.com/places/"; 	
	public PostCommentTask() {
		super();
	}

	@Override
	protected String doInBackground(ArrayList<String>... data) {
		post(data[0]);
		return null;
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

	private void post(ArrayList<String> data){
		HttpClient client = new DefaultHttpClient();
		url = url + data.get(0) + "/rate_and_comment";
		HttpPost httppost = new HttpPost(url);
		HttpResponse response = null;
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			Log.e(TAG, data.get(0));
			nameValuePairs.add(new BasicNameValuePair("place[access_token]", data.get(1)));
			nameValuePairs.add(new BasicNameValuePair("place[comment_content]", data.get(2)));
			nameValuePairs.add(new BasicNameValuePair("place[user_rate_value]", data.get(3)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			// Execute HTTP Post Request
			response = client.execute(httppost);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		Log.e("AAA","dsb");
		try{
			HttpEntity entity = response.getEntity();
			Log.e("AAA", streamToString(entity.getContent()));
			
		}catch(ClientProtocolException e){
			
		}catch(IOException e){
			
		}
	}
}
