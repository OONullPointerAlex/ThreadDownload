package com.echo.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class Down extends Service{

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};

}
