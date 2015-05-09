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
 * DownloadService�н��մ�Activity������һЩ����
 */
public class DownloadService extends Service{
	
	public static final String DOWNLOAD_PATH = 
			Environment.getExternalStorageDirectory().getAbsolutePath() + //��Ŀ¼·�� 
			"/downloads/";
	public static final String ACTION_START = "ACTION_START";
	public static final String ACTION_STOP = "ACTION_STOP";	
	public static final String ACTION_UPDATE = "ACTION_UPDATE";	
	public static final int MSG_INIT = 0;
	private DownloadTask mTask = null;
	
	//onStartCommand�л�ȡһЩ����ֵ
	public int onStartCommand(Intent intent, int flags, int startId) {
		//��ȡActivity�����Ĳ���
		if(ACTION_START.equals(intent.getAction())){
			FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.i("test","start" + fileInfo.toString());
			//������ʼ���߳�
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
			case MSG_INIT:
				FileInfo fileInfo = (FileInfo) msg.obj;
				Log.i("test", "Init:" + fileInfo);
				//������������
				mTask = new DownloadTask(DownloadService.this, fileInfo);
				mTask.download();
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * ��ʼ�����߳�
	 */
	class InitThread extends Thread{
		private FileInfo mFileInfo = null;

		public InitThread(FileInfo mFileInfo) {
			super();
			this.mFileInfo = mFileInfo;
		}
		@Override
		public void run() {
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			try {
				//���������ļ�
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection)url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");
				int length = -1;
				if(conn.getResponseCode() == HttpStatus.SC_OK){
					//��ȡ�ļ�����
					length = conn.getContentLength();
				}
				if(length <= 0){
					return;
				}
				File dir = new File(DOWNLOAD_PATH);
				if(!dir.exists()){
					dir.mkdir();
				}
				//�ڱ��ش����ļ�
				File file = new File(dir, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd"); //�ɶ� ��д ��ɾ��
				//�����ļ�����
				raf.setLength(length);	
				mFileInfo.setLength(length);
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
