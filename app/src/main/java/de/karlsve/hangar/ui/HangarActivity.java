package de.karlsve.hangar.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.karlsve.hangar.R;
import de.karlsve.hangar.service.HangarService;

public class HangarActivity extends ActionBarActivity {

    private HangarService service = null;

    private ServiceConnection serviceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            HangarActivity.this.service = ((HangarService.HangarServiceBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            HangarActivity.this.service = null;
        }
    };

    private void startServices() {
        this.startService(new Intent(this.getApplicationContext(), HangarService.class));
    }

    private void bindServices() {
        this.bindService(new Intent(this.getApplicationContext(), HangarService.class), this.serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindServices() {
        this.unbindService(this.serviceConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_hangar);
        this.setupActionBar();
        this.startServices();
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_logo);
        actionBar.setIcon(R.drawable.ic_logo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.menu_hangar, menu);
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
