package com.example.sensormanager;

import java.util.ArrayList;

import android.hardware.SensorEvent;

public class StepCounter implements SensorHub.DataClient{
	
	interface ChangeListener {
		void onStep(int step);
	}
	
	private static final int SAMPLING_FREQ = 20; //data sample frequency, should be larger then 10 to follow Nyquiste theorem

	private static final int SETUP_STEP_THRESHOLD = 8;
	private static final int SETUP_TICK_THRESHOLD = 70;
	
	private static final long SAMPLING_INERVAL = 1000000000/SAMPLING_FREQ;
	private static final int AXIS_ALIGN_INTERVAL = SAMPLING_FREQ*2; //every 2s
	
	private static final float ACCELERATOR_THRESHOLD = 7.5f;
	private static final int MIN_STEP_INTERVAL = SAMPLING_FREQ*2/4;
	private static final int MAX_STEP_INTERVAL = (int)(SAMPLING_FREQ*1.5f);
	
	private static final int STATE_PREPARING = 0;
	private static final int STATE_COUNTING = 1;
	
	static private class FusionEvent {
		public long mTimestamp;
		public float[] mValues = new float[3];
		public int mCount;
	}
	
	
	@Override
	public void onData(SensorEvent event, String text) {
		// Preprocess the event
		if ((mCurrentEvent.mCount>0) && (event.timestamp - mCurrentEvent.mTimestamp)>SAMPLING_INERVAL){
			mCurrentEvent.mValues[0] /= mCurrentEvent.mCount;
			mCurrentEvent.mValues[1] /= mCurrentEvent.mCount;
			mCurrentEvent.mValues[2] /= mCurrentEvent.mCount;
		}
		else {
			mCurrentEvent.mCount++;
			mCurrentEvent.mValues[0] += event.values[0];
			mCurrentEvent.mValues[1] += event.values[1];
			mCurrentEvent.mValues[2] += event.values[2];
			return;
		}
		
		//Process the event if necessary
		mTick++;
		
		//Find the significant axis
		mSquareSum[0] += mCurrentEvent.mValues[0] * mCurrentEvent.mValues[0];
		mSquareSum[1] += mCurrentEvent.mValues[1] * mCurrentEvent.mValues[1];
		mSquareSum[2] += mCurrentEvent.mValues[2] * mCurrentEvent.mValues[2];
		mSum[0] += mCurrentEvent.mValues[0];
		mSum[1] += mCurrentEvent.mValues[1];
		mSum[2] += mCurrentEvent.mValues[2];
		
		if ((++mAxisAlignTick)%AXIS_ALIGN_INTERVAL == 0){
			if (mSquareSum[0] > mSquareSum[1]){
				if (mSquareSum[0] > mSquareSum[2]){
					mPickAxis = 0;
				}
				else {
					mPickAxis = 2;
				}
			}
			else if (mSquareSum[1] > mSquareSum[2]){
				mPickAxis = 1;
			}
			else {
				mPickAxis = 2;
			}
		}

		mFactor = (mSum[mPickAxis]>0)?1:(-1);
		
		
		//counting
		if (STATE_COUNTING == mCurrState){
			if (mCurrentEvent.mValues[mPickAxis]*mFactor > ACCELERATOR_THRESHOLD && 
					mCurrentEvent.mValues[mPickAxis]*mFactor > mPrevEvent.mValues[mPickAxis]*mFactor &&
					mTickInterval > MIN_STEP_INTERVAL){
				mSteps += 2;
				mTickInterval = 0;
				
				if (mListener != null){
					mListener.onStep(mSteps);
				}
			}
			else {
				if (++mTickInterval > MAX_STEP_INTERVAL){
					mCurrState = STATE_PREPARING;
					mStepCandidate.clear();
				}
			}
		}
		else{
			if (mCurrentEvent.mValues[mPickAxis]*mFactor > ACCELERATOR_THRESHOLD && 
					mCurrentEvent.mValues[mPickAxis]*mFactor > mPrevEvent.mValues[mPickAxis]*mFactor &&
					mTickInterval > MIN_STEP_INTERVAL) {
				mStepCandidate.add(mTick);
				mTickInterval++;
				
				if (mStepCandidate.size()*2 >= SETUP_STEP_THRESHOLD){
					while (mTick - mStepCandidate.get(0) > SETUP_TICK_THRESHOLD){
						mStepCandidate.remove(0);
					}
					
					if (mStepCandidate.size()*2 >= SETUP_STEP_THRESHOLD) {
						mCurrState = STATE_COUNTING;
						mSteps += mStepCandidate.size()*2;
						mStepCandidate.clear();
						
						if (mListener != null){
							mListener.onStep(mSteps);
						}
					}
				}
			}
			else {
				mTickInterval++;
			}
			
		}
		
		
		//End: reset mCurrentEvent
		mPrevEvent.mCount = mCurrentEvent.mCount;
		mPrevEvent.mTimestamp = mCurrentEvent.mTimestamp;
		mPrevEvent.mValues[0] = mCurrentEvent.mValues[0];
		mPrevEvent.mValues[1] = mCurrentEvent.mValues[1];
		mPrevEvent.mValues[2] = mCurrentEvent.mValues[2];
		
		mCurrentEvent.mCount = 1;
		mCurrentEvent.mValues[0] = event.values[0];
		mCurrentEvent.mValues[1] = event.values[1];
		mCurrentEvent.mValues[2] = event.values[2];
		mCurrentEvent.mTimestamp = event.timestamp;
	}
	
	public void clearStep(){
		mSteps = 0;
		
		if (mListener != null){
			mListener.onStep(0);
		}
	}

	public void registerListener(ChangeListener listener){
		mListener = listener;
	}
	
	public void unregisterListener(){
		mListener = null;
	}
	//For Event re-sample
	private FusionEvent mCurrentEvent = new FusionEvent();
	private FusionEvent mPrevEvent = new FusionEvent();
	
	//For Axis Alignment
	private int mAxisAlignTick=0;
	private float[] mSquareSum = new float[3];
	private float[] mSum = new float[3];
	private int mPickAxis = 0;
	private int mFactor; //1 or -1
	
	//For Step Counter
	private int mCurrState = STATE_PREPARING;
	private int mTickInterval = 0;
	private int mTick = 0;
	private int mSteps = 0;
	private ArrayList<Integer> mStepCandidate = new ArrayList<Integer>();
	
	private ChangeListener mListener = null;
}
