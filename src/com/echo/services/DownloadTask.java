package com.echo.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpStatus;

import com.echo.download.db.ThreadDAO;
import com.echo.download.db.ThreadDAOImpl;
import com.echo.entities.FileInfo;
import com.echo.entities.ThreadInfo;

import android.content.Context;
import android.content.Intent;
/*
 * 下载任务类
 */
public class DownloadTask {

	private Context mContext = null;
	private FileInfo mFileInfo = null;
	private ThreadDAO mDao = null;
	private int mFinished = 0;
	public boolean isPause = false;
	public DownloadTask(Context mContext, FileInfo mFileInfo) {
		super();
		this.mContext = mContext;
		this.mFileInfo = mFileInfo;
		mDao = new ThreadDAOImpl(mContext);
	}
	
	public void download(){
		//读取数据库线程信息
		List<ThreadInfo> threadInfos= mDao.getThreads(mFileInfo.getUrl());
		ThreadInfo threadInfo = null;
		if(threadInfos.size() == 0){
			threadInfo = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(),0);
		}else{
			threadInfo = threadInfos.get(0);
		}
		//创建子线程进行下载
		new DownloadThread(threadInfo).start();
	}
	
	/**
	 * 下载线程
	*/
	class DownloadThread extends Thread{
		private ThreadInfo mThreadInfo = null;

		public DownloadThread(ThreadInfo mThreadInfo) {
			super();
			this.mThreadInfo = mThreadInfo;
		}
		
		@Override
		public void run() {
			//向数据库中插入信息
			if(mDao.isExists(mThreadInfo.getUrl(), mThreadInfo.getId())){
				mDao.insertThread(mThreadInfo);
			}
			RandomAccessFile raf = null;
			HttpURLConnection conn = null;
			InputStream input = null;
			try {
				URL url = new URL(mThreadInfo.getUrl());
				conn= (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");
				//设置下载位置
				int start =  mThreadInfo.getStart() + mThreadInfo.getFinished();
				conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
				//设置文件的写入位置
				File file = new File(DownloadService.DOWNLOAD_PATH, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				Intent intent = new Intent(DownloadService.ACTION_UPDATE);
				mFinished += mThreadInfo.getFinished();
				//开始下载
				if(conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT){
					//读取数据
					input = conn.getInputStream();
					byte[] buffer = new byte[1024*4];
					int len = -1;
					long time = System.currentTimeMillis();
					while((len = input.read(buffer)) != -1){
						//写入文件
						raf.write(buffer,0,len);
						//把下载进度发送广播给Activity
						mFinished += len;
						if(System.currentTimeMillis() - time > 500){
							time = System.currentTimeMillis();
							intent.putExtra("finished", mFinished * 100 /mFileInfo.getLength());
							mContext.sendBroadcast(intent);
						}
						//在下载暂停时，保存下载进度
						if(isPause){
							mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mFinished);
							return;
						}
					}
					//删除线程信息
					mDao.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getId());
				}			
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				
				try {
					conn.disconnect();
					raf.close();
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}

			
			
		}

	}
}
