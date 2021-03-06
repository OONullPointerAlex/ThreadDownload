package com.echo.entities;

import java.io.Serializable;

/**
 * 文件信息
 * @author Administrator
 *  实现了Serializable接口就可以在intent中间就可以把实体类对象放在中间进行传递。
 */
public class FileInfo implements Serializable{
	private int id;
	private String url;
	private String fileName;
	private int length; //文件长度
	private int finished; //完成进度
	public FileInfo() {
		super();
	}
	
	public FileInfo(int id, String url, String fileName, int length,
			int finished) {
		super();
		this.id = id;
		this.url = url;
		this.fileName = fileName;
		this.length = length;
		this.finished = finished;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getFinished() {
		return finished;
	}
	public void setFinished(int finished) {
		this.finished = finished;
	}

	//使用toString信息输出
	@Override
	public String toString() {
		return "FileInfo [id=" + id + ", url=" + url + ", fileName=" + fileName
				+ ", length=" + length + ", finished=" + finished + "]";
	}

}
