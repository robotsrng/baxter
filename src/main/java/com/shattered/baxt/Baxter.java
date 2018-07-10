package com.shattered.baxt;

import java.io.File;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;


@Controller
public class Baxter {

	private BaxterGUI gui;
	public UploadController uc = new UploadController();
	public static FileStitcher fs = new FileStitcher();
	private static volatile File[] filesArray;
	private static  volatile ArrayList<File> files = new ArrayList<File>() ;

	public Baxter(BaxterGUI gui) {
		this.gui = gui;
	}

	public void addVideos() {
		filesArray = uc.chooseFiles();
		for (File file : filesArray) {
			files.add(file);
			System.out.println("**ADD** " + file.getName());
		}
		updateGUI();
	}

	public void removeVideos(int[] si) {
		ArrayList<File> fileNames = new ArrayList<>();
		for (int i : si) {
			fileNames.add(files.get(i));
		}
		for(File name : fileNames) { 
			System.out.println("**REM** " + name.getName());
			files.remove(name);
		}
		updateGUI();
	}

	public void renderVids(String trimLength) {
		fs.stitchVideos(files, trimLength);
		clearFiles();
		updateGUI();
	}

	public void clearFiles() {
		fs.clearFiles();
		files.clear();
		filesArray = null ;
	}

	private void updateGUI() {
		gui.updateFileList(files);
	}

	// *******************************************
	// Getters and Setters
	// *******************************************

	public ArrayList<File> getFiles() {
		return files;
	}
}
