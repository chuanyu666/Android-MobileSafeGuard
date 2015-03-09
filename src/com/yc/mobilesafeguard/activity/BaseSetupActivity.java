package com.yc.mobilesafeguard.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		detector = new GestureDetector(this, new SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
				float velocityX, float velocityY) {
				//fling too slow 
				if(Math.abs(velocityX)<200){
					return true;
				}
				
				if(Math.abs(e2.getRawY()-e1.getRawY())>100){
					return true;
				}
				
				if((e2.getRawX() - e1.getRawX())>200){
					//left to right
					showPrev();
					return true;
				}
				if((e1.getRawX() - e2.getRawX())>200){
					//right to left
					showNext();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	
	public void next(View view){
		showNext();
	}
	
	public void previous(View view){
		showPrev();
	}
	
	public abstract void showNext();
	public abstract void showPrev();
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
