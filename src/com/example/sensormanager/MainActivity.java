package com.example.sensormanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onResume(){
    	final Intent intent = new Intent(this, SensorActivity.class);
    	final Intent intent1 = new Intent(this, AppStepCounterActivity.class);
    	
    	ExpandableListView list = (ExpandableListView)findViewById(R.id.sensor_list);
    	list.setAdapter(new SensorListAdapter(this.getApplicationContext()));
    	list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				SensorListAdapter.sensorType(groupPosition);
				Bundle data = new Bundle();
				data.putLong("sensorID", id);
				intent.putExtras(data);
				startActivity(intent);
				return true;
			}
		});
    	
    	list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (id == 0x9988){ // special case
					startActivity(intent1);
					return true;
				}
				return false;
			}
		});
    	super.onResume();
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
}
