package me.aflak.annotation_sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.aflak.annotations.Navigator;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Navigator.startTestActivity(this);
    }
}
