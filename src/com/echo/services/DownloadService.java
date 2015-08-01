package com.echo.services;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import com.echo.entities.FileInfo;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputFilter.LengthFilter;
import android.util.Log;

/*
 * DownloadService中接收从Activity传来的一些数据（FileInfo由Activity传到Service进行接收）
 * 之前必须必须先将FileInfo进行序列化
 * 在xml中进行注册
 */
public class DownloadService extends Service{
	
	public static final String DOWNLOAD_PATH = 
			Environment.getExternalStorageDirectory().getAbsolutePath() + //根目录路径 
			"/downloads/";
	public static final String ACTION_START = "ACTION_START";
	public static final String ACTION_STOP = "ACTION_STOP";	
	public static final String ACTION_UPDATE = "ACTION_UPDATE";	
	public static final int MSG_INIT = 0;
	private DownloadTask mTask = null;
	
	//onStartCommand中获取一些数据值（接收代码）
	public int onStartCommand(Intent intent, int flags, int startId) {
		//获取Activity传来的参数
		//数据从intent中传递过来，从intent中获取数据
		//判断intent传来的值
		if(ACTION_START.equals(intent.getAction())){
			FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			//测试数据有没有传递过来
			Log.i("test","start" + fileInfo.toString());
			//启动初始化线程
			new InitThread(fileInfo).start();
		}else if(ACTION_STOP.equals(intent.getAction())){
			FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.i("test","stop" + fileInfo.toString());
			if(mTask != null){
				mTask.isPause = true;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_INIT:  //文件初始化
				FileInfo fileInfo = (FileInfo) msg.obj;
				Log.i("test", "Init:" + fileInfo);
				//启动下载任务
				mTask = new DownloadTask(DownloadService.this, fileInfo);
				mTask.download();
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 初始化子线程
	 */
	class InitThread extends Thread{
		//需要一些文件信息
		private FileInfo mFileInfo = null;

		public InitThread(FileInfo mFileInfo) {
			super();
			this.mFileInfo = mFileInfo;
		}
		
		//run方法中实现网络请求
		@Override
		public void run() {
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			try {
				//1 连接网络文件
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");//从服务器下载文件，所以使用Get获取数据，除了下载操作以外，使用POST
				int length = -1;
				if(conn.getResponseCode() == HttpStatus.SC_OK){
					//2 获取文件长度
					length = conn.getContentLength();
				}
				if(length <= 0){
					return;
				}
				File dir = new File(DOWNLOAD_PATH);
				if(!dir.exists()){
					dir.mkdir();
				}
				//3 在本地创建文件
				File file = new File(dir, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd"); //可读 可写 可删除 RandomAccessFile可在任意位置写入
				//4 设置文件长度
				raf.setLength(length);	
				mFileInfo.setLength(length);
				//得到文件长度后发送回Service,线程和Service进行交互使用Handler
				mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{	
				try {
					conn.disconnect();
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
