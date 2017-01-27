package com.tarams.vo;

import java.util.Date;

public class FileInfo {

	private String fileName;
	private Date FileCreatedDate;
	private Date fileModifiedDate;
	private Long fileSize;
	
	public FileInfo() {
		
	}
	
	public FileInfo(String fileName, Date fileCreatedDate, Date fileModifiedDate, Long fileSize) {
		this.fileName = fileName;
		FileCreatedDate = fileCreatedDate;
		this.fileModifiedDate = fileModifiedDate;
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getFileCreatedDate() {
		return FileCreatedDate;
	}

	public void setFileCreatedDate(Date fileCreatedDate) {
		FileCreatedDate = fileCreatedDate;
	}

	public Date getFileModifiedDate() {
		return fileModifiedDate;
	}

	public void setFileModifiedDate(Date fileModifiedDate) {
		this.fileModifiedDate = fileModifiedDate;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	@Override
	public String toString() {
		return "FileInfo [fileName=" + fileName + ", FileCreatedDate=" + FileCreatedDate + ", fileModifiedDate="
				+ fileModifiedDate + ", fileSize=" + fileSize + "]";
	}
	
}
