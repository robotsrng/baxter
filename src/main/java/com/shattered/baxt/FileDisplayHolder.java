package com.shattered.baxt;

import java.io.File;

public class FileDisplayHolder {

	File file ;
	String filename ;
	public FileDisplayHolder(File file, String filename) {
		this.file = file;
		this.filename = filename;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
}
