package com.example.hanoitours;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class PlaceDetail extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_place_detail, menu);
        return true;
    }

    
}
