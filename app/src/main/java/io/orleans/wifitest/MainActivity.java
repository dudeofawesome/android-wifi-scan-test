package io.orleans.wifitest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView wifiList;
    private Context context;
    private WifiManager wifiManager;
    WifiScanReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wifiList = (TextView) findViewById(R.id.wifiList);
        context = getBaseContext();

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWifiScan();
                Snackbar.make(view, "Starting WiFi scan", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
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

    class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            System.out.println("Found wifis");
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            System.out.println(wifiScanList);
            wifiList.setText(wifiScanList.toString());
        }
    }

    // WTF is this?
    int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 5;

    public void startWifiScan () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Reuesting permissions");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                System.out.println("We should explain our perm request");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            // After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            System.out.println("Already had permissions");
            // Do something, permission was previously granted; or legacy device
            wifiManager.startScan();
        }
        wifiList.setText("Scanning...");
        wifiManager.startScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        System.out.println("Got permissions request results");
        System.out.println(Arrays.toString(permissions));
        System.out.println(Arrays.toString(grantResults));
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            wifiManager.startScan();
        }
    }
}
