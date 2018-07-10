package com.shattered.baxt;

import java.awt.Frame;
import java.io.File;

import javax.swing.JFileChooser;

public class UploadController {

	public File[] chooseFiles() {
		// TODO Add validation for file type and size
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(new Frame());
		File[] files = chooser.getSelectedFiles();
		return files;
	}
}
