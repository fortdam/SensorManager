package com.example.sensormanager;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


class FilePrinter implements SensorHub.DataClient{
	
	public static boolean isFileExist(Context ctx, int type){
		return getFile(ctx, type).exists();
	}
	
	public static void removeFile(Context ctx, int type){
		getFile(ctx, type).delete();
	}
	
	public static File getFile(Context ctx, int type){
		String name = SensorListAdapter.sensorName(type);
		
		File dir = new File("/sdcard","sensor");
		
		if (!dir.exists()){
			dir.mkdirs();
		}
		
		return new File(dir, name);
	}

	public FilePrinter(Context ctx, int type){
		File file = getFile(ctx, type);
		
		try{
			mWriter = new FileWriter(file, true);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onData(SensorEvent event, String data) {
		// TODO Auto-generated method stub
		try{
			if (null != mWriter){
				mWriter.append("\n");
				mWriter.append(data);
			}
			Log.i("tangzm", "print file");
		}
		catch (Exception e){
			Log.i("tangzm", "file print error");
			e.printStackTrace();
		}
	}
	
	public void terminate() {
		try{
		mWriter.close();
		mWriter = null;
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private FileWriter mWriter;
}

public class SensorActivity extends Activity implements SensorHub.DataClient{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor);
		
		Bundle data = getIntent().getExtras();
		Long sid = data.getLong("sensorID");
		mSensorHub =  SensorHub.getInstance(getApplicationContext());
		mSensor = mSensorHub.getSensor((int)(sid>>8), (int)(sid&0xff));
	}
	
	private void resetConsole(){
		String text = "Name: "+mSensor.getName()+ 
			    "\nType: " +SensorListAdapter.sensorName(mSensor.getType())+
				"\nVendor: "+mSensor.getVendor()+
				"\nVersion: "+mSensor.getVersion()+
				"\nRange: "+mSensor.getMaximumRange()+
				"\nMin-Delay: " +mSensor.getMinDelay()+
				"\nPower: " +mSensor.getPower()+"mA"+
				"\nResolution: " +mSensor.getResolution() + "\n-------------------------------------\n";

		if (null == mTextView){
			mTextView = (TextView)findViewById(R.id.sensor);
		}
		
		mTextView.setText(text);
	}
	
	private void registerHandlers() {
		
		mConsoleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetConsole();
			}
		});
		
		final SensorActivity inst = this;
		mConsoleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					mTID = Thread.currentThread().getId();
					mHandler = new Handler();
					
					mSensorHub.startSensor(mSensor, inst);
				}
				else {
					mSensorHub.stopSensor(mSensor, inst);
				}
			}
		});
		
		mFileButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = FilePrinter.getFile(inst.getApplicationContext(), mSensor.getType());
				file.delete();
			}
		});
		
		mFileSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					FilePrinter fp = new FilePrinter(inst.getApplicationContext(), mSensor.getType());
					mSensorHub.startSensor(mSensor, fp);
				}
				else {
					ArrayList<SensorHub.DataClient> list = mSensorHub.peekSensorClients(mSensor);
					
					if (list != null) {
						for (int i=0; i<list.size(); i++) {
							if (list.get(i) instanceof FilePrinter) {
								FilePrinter fp = (FilePrinter)list.get(i);
								
								fp.terminate();
								mSensorHub.stopSensor(mSensor, fp);
								break;
							}
						}
					}
				}
			}
		});
	}
	
	public void onResume() {
		super.onResume();
		
		mTextView = (TextView)findViewById(R.id.sensor);
		mConsoleButton = (Button)findViewById(R.id.ClearText);
		mFileButton = (Button)findViewById(R.id.ClearFile);
		mConsoleSwitch = (Switch)findViewById(R.id.SwitchTextPrint);
		mFileSwitch = (Switch)findViewById(R.id.SwitchFilePrint);
		
		resetConsole();
		mTextView.setMovementMethod(new ScrollingMovementMethod());

		mConsoleSwitch.setChecked(false);
		
		mFileSwitch.setOnCheckedChangeListener(null);
		mFileSwitch.setChecked(false);

		ArrayList<SensorHub.DataClient> list = mSensorHub.peekSensorClients(mSensor);
		
		if (list != null){
			for (int i=0; i<list.size(); i++) {
				if (list.get(i) instanceof FilePrinter) {
					mFileSwitch.setChecked(true);
				}
			}
		}
	
		registerHandlers();

	}

	public void onPause() {
		super.onPause();
		mSensorHub.stopSensor(mSensor, this);
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
	
	private SensorHub mSensorHub;
	private Sensor mSensor;
		
	private TextView mTextView;
	private Button mConsoleButton;
	private Button mFileButton;
	private Switch mConsoleSwitch;
	private Switch mFileSwitch;
	
	private Handler mHandler;
	private long mTID;
	
	@Override
	public void onData(SensorEvent event, final String data) {
		// TODO Auto-generated method stub
		if (Thread.currentThread().getId() == mTID){
			//In the main thread
			mTextView.append("\n");
			mTextView.append(data);
		}
		else {
			mHandler.post(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mTextView.append("\n");
					mTextView.append(data);
				}
				
			});
		}
	}
}
