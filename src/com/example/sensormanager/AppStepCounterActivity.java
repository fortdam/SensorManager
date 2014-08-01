package com.example.sensormanager;

import java.util.ArrayList;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class AppStepCounterActivity extends Activity implements StepCounter.ChangeListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_step_counter);
		
		mSensorHub =  SensorHub.getInstance(getApplicationContext());
		mSensor = mSensorHub.getSensor(Sensor.TYPE_LINEAR_ACCELERATION, 0);
		
		mStepCounter = new StepCounter();
	}

	public void onResume() {
		super.onResume();
		
		mTextView = (TextView)findViewById(R.id.AppStepCounter);
		mClearButton = (Button)findViewById(R.id.ClearStep);
		mSwitch = (Switch)findViewById(R.id.SwitchStep);
		final AppStepCounterActivity currApp = this;
		
		
		mSwitch.setOnCheckedChangeListener(null);
		
		mSwitch.setChecked(false);
		
		ArrayList<SensorHub.DataClient> list = mSensorHub.peekSensorClients(mSensor);
		
		if (list != null){
			for (int i=0; i<list.size(); i++) {
				if (list.get(i) instanceof StepCounter) {
					mSwitch.setChecked(true);
					mStepCounter = (StepCounter)list.get(i);
					mStepCounter.registerListener(this);
				}
			}
		}
		
		mClearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mStepCounter.clearStep();
			}
		});
		
		mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					mHandler = new Handler();
					mTID = Thread.currentThread().getId();
					mStepCounter.registerListener(currApp);
					mSensorHub.startSensor(mSensor, mStepCounter);
				}
				else{
					mStepCounter.unregisterListener();
					mSensorHub.stopSensor(mSensor, mStepCounter);
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_step_counter, menu);
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
	
	private SensorHub mSensorHub;
	private Sensor mSensor;
		
	private TextView mTextView;
	private Button mClearButton;
	private Switch mSwitch;
	
	private Handler mHandler;
	private long mTID;
	
	private StepCounter mStepCounter;

	@Override
	public void onStep(final int step) {
		// TODO Auto-generated method stub
		if (Thread.currentThread().getId() == mTID){
			mTextView.setText(""+step+" steps");		}
		else {
			mHandler.post(new Runnable(){

				@Override
				public void run() {
					mTextView.setText(""+step+" steps");
				}
				
			});
		}
	}
}
