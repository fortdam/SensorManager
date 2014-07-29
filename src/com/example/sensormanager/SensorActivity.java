package com.example.sensormanager;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SensorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		
		Bundle data = getIntent().getExtras();
		Long sid = data.getLong("sensorID");
		mSensor = SensorInfo.getInstance(getApplicationContext()).getSensor((int)(sid>>8), (int)(sid&0xff));
	}
	
	public void onResume() {
		super.onResume();
		TextView view = (TextView)findViewById(R.id.sensor);
		
		String text = "Name: "+mSensor.getName()+ 
			    "\nType: " +SensorListAdapter.sensorName(mSensor.getType())+
				"\nVendor: "+mSensor.getVendor()+
				"\nVersion: "+mSensor.getVersion()+
				"\nRange: "+mSensor.getMaximumRange()+
				"\nMin-Delay: " +mSensor.getMinDelay()+
				"\nPower: " +mSensor.getPower()+"mA"+
				"\nResolution: " +mSensor.getResolution() + "\n-------------------------------------\n";
		
		view.setText(text);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private Sensor mSensor;
}
