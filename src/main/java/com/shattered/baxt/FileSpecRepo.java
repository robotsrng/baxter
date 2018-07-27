package com.shattered.baxt;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class FileSpecRepo {

	public ArrayList<FileSpec> specs ;
	public String soundDirectory = "" ;
	public double soundTrim = 0 ;
	public long soundDuration ;
	public String outputDirectory;
	public long maxLength = 0 ;

	public FileSpecRepo() { 
		specs = new ArrayList<>();
	}

	public void addFile(String filepath) {
		createSpecOpts(filepath);
	}

	public FileSpec getFileSpecByFileName(String filename) { 
		for(FileSpec fso : specs) { 
			if(fso.getFilename().equals(filename)) {
				return fso;
			}
		}
		return null;
	}

	private void createSpecOpts(String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
		FileSpec fileSpec = new FileSpec();
		String[] options = findVidSpecs(file.getAbsolutePath());
		fileSpec.setDuration(Long.valueOf(options[3]));
		if(fileSpec.getDuration() < 2000) { 
			System.out.println("File too short.");
			return ;
		}
		fileSpec.setOrigFilename(FilenameUtils.removeExtension(file.getName()));
		fileSpec.setFilename((Math.random() * file.getName().hashCode()) + "_" + file.getName());
		fileSpec.setInputURL(file.getAbsolutePath());
		fileSpec.setWidth(Integer.valueOf(options[0]));
		fileSpec.setHeight(Integer.valueOf(options[1]));
		fileSpec.setRotation(options[2]);
		fileSpec.setTrimEnd(Math.min(2.0, (fileSpec.getDuration()/ 1000.0)));
		System.out.println("Added : " + file.getName());
		maxLength += fileSpec.getTrimEnd()  - fileSpec.getTrimStart()  ;
		specs.add(fileSpec);
		}
		else {
			System.out.println("BAD FILE");
		}
	}

	private String[] findVidSpecs(String vidLocation) {
		IContainer container = IContainer.make();
		int result = container.open(vidLocation, IContainer.Type.READ, null);
		if (result<0)
			throw new RuntimeException("Failed to open media file");
		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		// iterate through the streams to print their meta data
		for (int i=0; i<numStreams; i++) {
			// find the stream object
			IStream stream = container.getStream(i);
			// get the pre-configured decoder that can decode this stream;
			IStreamCoder coder = stream.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				continue ;
			}
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				String width = coder.getWidth() + "";
				String height = coder.getHeight() + "";
				String ret = "";
				String duration = (container.getDuration() / 1000) + "";
				if(stream.getMetaData().getValue("rotate") != null) {
					ret = stream.getMetaData().getValue("rotate");
				}
				String[] array = {width, height, ret, duration} ;
				return array;
			}
		}
		return null;
	}		

	private String[] findAudioSpecs(String audioLocation) {
		IContainer container = IContainer.make();
		int result = container.open(audioLocation, IContainer.Type.READ, null);
		if (result<0)
			throw new RuntimeException("Failed to open media file");
		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		// iterate through the streams to print their meta data
		for (int i=0; i<numStreams; i++) {
			// find the stream object
			IStream stream = container.getStream(i);
			// get the pre-configured decoder that can decode this stream;
			IStreamCoder coder = stream.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				String duration = (container.getDuration() / 1000) + "";
				String[] array = {duration} ;
				return array;
			}
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				continue ;
			}
		}
		return null;
	}

	public int setFileTrim(String filename, String trimStart, String trimEnd) {
		if (Double.parseDouble(trimStart) >= Double.parseDouble(trimEnd)) { 
			return 1 ;
		}
		FileSpec workingFS = getFileSpecByFileName(filename);
		if (Double.parseDouble(trimStart) * 1000 >= workingFS.getDuration()) {
			return 2 ;
		}
		maxLength -= workingFS.getTrimEnd() - workingFS.getTrimStart() ;
		workingFS.setTrimStart(Math.round(Double.parseDouble(trimStart)));
		workingFS.setTrimEnd(Math.round(Double.parseDouble(trimEnd)));
		maxLength += (workingFS.getTrimEnd() - workingFS.getTrimStart());
		return 0 ;
	}
	
	public int setFileTrim(String filename, double trimStart, double trimEnd) {
		if (trimStart >= trimEnd) { 
			return 1 ;
		}
		FileSpec workingFS = getFileSpecByFileName(filename);
		if (trimStart * 1000 >= workingFS.getDuration()) {
			System.out.println("2");
			return 2 ;
		}
		maxLength -= workingFS.getTrimEnd() - workingFS.getTrimStart() ;
		workingFS.setTrimStart(Math.round(trimStart));
		workingFS.setTrimEnd(Math.round(trimEnd));
		maxLength += (workingFS.getTrimEnd() - workingFS.getTrimStart());
		return 0 ;
	}
	
	public void moveVideoUp(FileSpec fileSpec) {
		int oldIndex ;
		if((oldIndex = specs.indexOf(fileSpec)) <= 0 ) { 
			return ;
		}
		int newIndex = oldIndex - 1 ;
		FileSpec tempFS = specs.get(newIndex);
		specs.set(newIndex, fileSpec);
		specs.set(oldIndex, tempFS);
	}
	
	public void moveVideoDown(FileSpec fileSpec) {
		int oldIndex ;
		if((oldIndex = specs.indexOf(fileSpec)) >= specs.size() - 1) { 
			return ;
		}
		int newIndex = oldIndex + 1 ;
		FileSpec tempFS = specs.get(newIndex);
		specs.set(newIndex, fileSpec);
		specs.set(oldIndex, tempFS);
	}

	//*******************************************
	// Getters and Setters
	//*******************************************

	public void removeFileSpec(String filename) {
		FileSpec workingFS = getFileSpecByFileName(filename);
		maxLength -= (workingFS.getTrimEnd() - workingFS.getTrimStart());
		specs.remove(workingFS);
	}

	public ArrayList<FileSpec> getSpecs(){
		return specs;
	}

	public String getSoundDirectory() {
		return soundDirectory;
	}

	public void setSoundDirectory(String soundDirectory) {
		this.soundDirectory = soundDirectory;
		this.soundDuration = Long.valueOf(findAudioSpecs(soundDirectory)[0]) / 1000;
	}

	public int setSoundTrim(String trim) {
		if((Double.parseDouble(trim) > soundDuration)) { 
			return 1;
		}else { 
			this.soundTrim = Double.parseDouble(trim);
			return 0;
		}  
	}

	public double getSoundTrim() { 
		return soundTrim;
	}

	public void setSpecs(ArrayList<FileSpec> specs) {
		this.specs = specs;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory ;
	}

	public long getMaxLength() {
		return maxLength;
	}

	public void randomizeAll() {
		for(FileSpec fs : specs) {
			double maxTrim = fs.getDuration() / 1000.0 ;
			double lowerBound = Math.min((long) maxTrim * .25, 3);
			double upperBound = fs.getDuration() / 1000.0 ;
			double start = Math.random() * (upperBound - lowerBound - lowerBound) ;
			double fin =  (start + (Math.random() * (upperBound - lowerBound)));
			while((fin - start > 5) || fin - start < 2) { 
				start = Math.random() * (upperBound - lowerBound - lowerBound) ;
				fin =  (start + (Math.random() * (upperBound - lowerBound)));
			}
			setFileTrim(fs.getFilename(), start, fin);
		}
	}

	public void addFileMulti(File[] userVideoFiles) {
		for(File file : userVideoFiles) {
			createSpecOpts(file.getAbsolutePath());
		}
	}

	public void sortByName() {
		Collections.sort(specs);
	}

	public void emptyRepo() {
		specs.clear();
		soundDirectory = "";
	}

	
}
