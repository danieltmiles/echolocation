package com.amateurbikenerd.echoLocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EchoLocationActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent svc = new Intent(this, NoiseService.class);
	    startService(svc);
	}
	@Override
	public void onDestroy(){
		Intent svc = new Intent(this, NoiseService.class);
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