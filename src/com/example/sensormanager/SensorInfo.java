package com.example.sensormanager;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorInfo {
	
	public static SensorInfo getInstance(Context ctx){
		if (null == mInstance) {
			mInstance = new SensorInfo(ctx);
		}
		return mInstance;
	}

	private SensorInfo (Context ctx){
		mSensorManager = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);
		mSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		Log.i("tangzm", "total sensor:"+mSensors.size());
	}
	
	public int SensorNum(int type){
		int num = 0;
		
		for (int i=0; i<mSensors.size(); i++){			
			if (type == mSensors.get(i).getType()){
				num++;
			}
		}
		return num;		
	}
	
	public Sensor getSensor(int type, int index){		
		for (int i=0; i<mSensors.size(); i++){
			
			if (type == mSensors.get(i).getType()){
				if (index == 0){
					return mSensors.get(i);
				}
				else {
					index--;
				}
			}
		}
		
		return null;
	}
	
	private static SensorInfo mInstance = null;
	private SensorManager mSensorManager;
	private List<Sensor> mSensors;
}
