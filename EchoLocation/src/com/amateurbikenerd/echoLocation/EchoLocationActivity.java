package com.amateurbikenerd.echoLocation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class EchoLocationActivity extends Activity {
	private Intent svc;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		svc = new Intent(this, NoiseService.class);
	    startService(svc);
	}
	@Override
	public void onDestroy(){
		//Intent svc = new Intent(this, NoiseService.class);
	    stopService(svc);
	    super.onDestroy();
	}
	public void myClickHandler(View view){
		switch(view.getId()){
		case R.id.closeButton:
			System.out.println("finishing");
			this.finish();
		}
	}
}