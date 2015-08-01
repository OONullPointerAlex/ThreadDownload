package com.echo.downloaddemo;

import com.echo.entities.FileInfo;
import com.echo.services.DownloadService;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView mTvFileName = null;
	private ProgressBar mPbProgress = null;
	private Button mBtStop = null;
	private Button mBtStart = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvFileName = (TextView) findViewById(R.id.tvFileName);
        mPbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        mBtStart = (Button) findViewById(R.id.btStart);
        mBtStop = (Button) findViewById(R.id.btStop);
        mPbProgress.setMax(100);
        //创建文件信息对象
        final FileInfo fileInfo = new FileInfo(0,"https://raw.githubusercontent.com/Aspsine/Daily/master/art/daily.apk"
        		,"daily.apk",0,0);
        //添加事件监听
        mBtStart.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// 通过Intent传递参数给Service
				Intent intent = new Intent(MainActivity.this, DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				intent.putExtra("fileInfo", fileInfo);
				startService(intent);		
			}
		});
        mBtStop.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// 通过Intent传递参数给Service
				Intent intent = new Intent(MainActivity.this, DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra("fileInfo", fileInfo);
				startService(intent);	//第一次启动的时候启动	
			}
		});
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	unregisterReceiver(mReceiver);
    }
    
    BroadcastReceiver mReceiver = new BroadcastReceiver(){
    	public void onReceive(android.content.Context arg0, Intent intent) {
    		if(DownloadService.ACTION_UPDATE.equals(intent.getAction())){
    			int finished = intent.getIntExtra("finished", 0);
    			mPbProgress.setProgress(finished);
    		}
    	}
    };
    
}
