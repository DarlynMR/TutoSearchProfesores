package com.rd.dmmr.tutosearchprofesores;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;


public class ScrollingActivity extends AppCompatActivity {

    private FloatingActionButton fBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        fBack = (FloatingActionButton) findViewById(R.id.fBackButton);

        fBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }


}
