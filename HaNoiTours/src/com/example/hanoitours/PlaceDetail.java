package com.example.hanoitours;

import com.google.android.maps.MapView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class PlaceDetail extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        TextView text = (TextView) findViewById(R.id.place_name);
        text.setText(getIntent().getStringExtra("TEST"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_place_detail, menu);
        return true;
    }

    
}
