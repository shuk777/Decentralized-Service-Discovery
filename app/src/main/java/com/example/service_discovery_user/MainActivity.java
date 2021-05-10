package com.example.service_discovery_user;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.aware.WifiAwareManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void publish(View view) {
        // Do something in response to button
        final AttachCallbackExt attachCallback =new AttachCallbackExt();
        final Handler handler = new Handler();
        boolean wifiAwareFlag = getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE);
        if (wifiAwareFlag) {
            final WifiAwareManager wifiAwareManager =
                    (WifiAwareManager) getSystemService(Context.WIFI_AWARE_SERVICE);
            wifiAwareManager.attach(attachCallback, handler);
            System.out.println("Start attaching");
        }
    }

    public void subscribe(View view) {
        // Do something in response to button
        final AttachCallbackSub attachCallback = new AttachCallbackSub();
        final Handler handler = new Handler();
        boolean wifiAwareFlag = getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE);
        if (wifiAwareFlag) {
            final WifiAwareManager wifiAwareManager =
                    (WifiAwareManager) getSystemService(Context.WIFI_AWARE_SERVICE);
            System.out.println("isAvailable");
            wifiAwareManager.attach(attachCallback, handler);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}