package com.example.sensormanager;

import java.util.HashMap;

import android.content.Context;
import android.hardware.Sensor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class SensorListAdapter extends BaseExpandableListAdapter {
	
	public SensorListAdapter(Context ctx){
		mAppCntx = ctx;
		mSensorHub = SensorHub.getInstance(ctx);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return SENSOR_LIST.length + 1;
	}
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if (groupPosition >= SENSOR_LIST.length){
			return 0;
		}
		else {
			return mSensorHub.SensorNum(SENSOR_LIST[groupPosition]);
		}
	}
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		if (groupPosition == SENSOR_LIST.length) {
			return 0x9988;
		}
		else {
			return groupPosition;
		}
	}
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return SENSOR_LIST[groupPosition]<<8+childPosition;
	}
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView item = (TextView)convertView;
		
		if (null == item){
			item = new TextView(mAppCntx);
		}
		
		if (groupPosition < SENSOR_LIST.length) {
			item.setText(sensorName(SENSOR_LIST[groupPosition]));
			item.setTextSize(20f);
			
			if (mSensorHub.SensorNum(SENSOR_LIST[groupPosition])>0){
				item.setTextColor(0xFF000000);
				item.setText(sensorName(SENSOR_LIST[groupPosition]) + "(Avail)");
				item.setPadding(70, 0, 0, 0);
			}
			else {
				item.setTextColor(0xFF777777);
				item.setText(sensorName(SENSOR_LIST[groupPosition]) + "(N/A)");		
				item.setPadding(70, 0, 0, 0);
			}
		}
		else {
				item.setText("TCL software pedrometer");
				item.setTextSize(20f);
				item.setPadding(70, 0, 0, 0);
		}
		
		return item;
	}
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView item = (TextView)convertView;
		
		if (null == item){
			item = new TextView(mAppCntx);
		}
		
		item.setText(mSensorHub.getSensor(SENSOR_LIST[groupPosition], childPosition).getName());
		item.setTextSize(18f);
		item.setPadding(100, 0, 0, 0);

		return item;
	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	private static final int SENSOR_LIST[] = {
		Sensor.TYPE_ACCELEROMETER,
		Sensor.TYPE_LINEAR_ACCELERATION,
		
		Sensor.TYPE_ORIENTATION,

		
		Sensor.TYPE_GYROSCOPE,
		//Sensor.TYPE_GYROSCOPE_UNCALIBRATED,
		
		Sensor.TYPE_MAGNETIC_FIELD,
		//Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED,
		
		Sensor.TYPE_ROTATION_VECTOR,

		Sensor.TYPE_GAME_ROTATION_VECTOR,
		Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
		
		Sensor.TYPE_SIGNIFICANT_MOTION,

		Sensor.TYPE_AMBIENT_TEMPERATURE,
		Sensor.TYPE_TEMPERATURE,

		
		Sensor.TYPE_GRAVITY,

		Sensor.TYPE_PRESSURE,
		Sensor.TYPE_RELATIVE_HUMIDITY,
		Sensor.TYPE_PROXIMITY,
		Sensor.TYPE_LIGHT,

		Sensor.TYPE_STEP_COUNTER,
		Sensor.TYPE_STEP_DETECTOR
	};
	
	public static int sensorType(int index){
		return SENSOR_LIST[index];
	}
	
	public static String sensorName(int type) {
		switch(type) {
		case Sensor.TYPE_ACCELEROMETER:
			return "Accelerometer";
		case Sensor.TYPE_LINEAR_ACCELERATION:
			return "Linear Accelerometer";
		case Sensor.TYPE_ORIENTATION:
			return "Orientation";
		case Sensor.TYPE_GYROSCOPE:
			return "Gyroscope";
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return "Gyroscope Uncalibrated";
		
		case Sensor.TYPE_MAGNETIC_FIELD:
			return "e-compass";
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return "e-compass Uncalibrated";
		
		case Sensor.TYPE_ROTATION_VECTOR:
			return "Rotation Vector";
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return "Significant Motion";

		case Sensor.TYPE_AMBIENT_TEMPERATURE:
			return "Ambient Temperature";
		case Sensor.TYPE_TEMPERATURE:
			return "Temperature";

		case Sensor.TYPE_GAME_ROTATION_VECTOR:
			return "Game Rotation Vector";
		case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
			return "Geomagnetic Rotation";
		
		case Sensor.TYPE_GRAVITY:
			return "Gravity";

		case Sensor.TYPE_PRESSURE:
			return "Pressure";
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return "Relative Humidity";
		case Sensor.TYPE_PROXIMITY:
			return "Proximity";

		case Sensor.TYPE_LIGHT:
			return "Light";
		case Sensor.TYPE_STEP_COUNTER:
			return "Step Counter";
		case Sensor.TYPE_STEP_DETECTOR:
			return "Step Detector";
		default:
			return "Unknown";
		}
	}

	private Context mAppCntx;
	private SensorHub mSensorHub;
}
