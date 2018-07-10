package com.shattered.baxt;

import java.io.File;
import java.util.ArrayList;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class FileStitcher {

	public static String base = "/home/srng/baxter/";
	public static String out = "/home/srng/baxter/out.mp4";
	public static String test = "/home/srng/baxter/test";
	public static String temp = "/home/srng/baxter/temp.mp4";
	public static String outvid = "/home/srng/outvid.mp4";
	public static String sound = "/home/srng/panic.mp3";
	public static String finalvid = "/home/srng/finalvid.mp4";
	
	public static ArrayList<FileSpecOptions> specs = new ArrayList<>();

	static int count = 0;

	public static VideoConverter converter = new VideoConverter();


	public void stitchVideos(ArrayList<File> files, String trimLength)  {
		createSpecOpts(files, trimLength);
		concatenateMulti();
		VideoSounder.add(outvid, sound, finalvid);
		System.out.println("VIDEO BAXTED");
	}

	public void createSpecOpts(ArrayList<File> files, String trimLength) {
		for(File file : files) { 
			//CREATE filespec objects with all neccessary info
			//then use those in all the conversions
			FileSpecOptions fileSpec = new FileSpecOptions();
			String[] options = findVidSpecs(file.getPath());
			fileSpec.setFilename(file.getName());
			fileSpec.setInputURL(file.getPath());
			fileSpec.setOutputURL(base + count + file.getName());
			fileSpec.setWidth(Integer.valueOf(options[0]));
			fileSpec.setHeight(Integer.valueOf(options[1]));
			fileSpec.setRotation(options[2]);
			fileSpec.setTrim(Long.valueOf(trimLength));
			fileSpec.setDuration(Long.valueOf(options[3]));
			specs.add(fileSpec);
			count++;
		}
	}

	private void concatenateMulti() {
		ArrayList<String> fileURLS = new ArrayList<>();
		for(FileSpecOptions file : specs) {
			fileURLS.add(file.getInputURL());
		}
		Concatenate.concatenate(specs, outvid);
		clearFiles();
	}

	public static String[] findVidSpecs(String vidLocation) {
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

	public void clearFiles() {
		specs.clear();
		count = 0;
	}

	public static void showVidInfo(String vidLocation) {
		System.out.println("");
		System.out.println(vidLocation);
		IContainer container = IContainer.make();
		int result = container.open(vidLocation, IContainer.Type.READ, null);
		if (result<0)
			throw new RuntimeException("Failed to open media file");
		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		// query for the total duration
		long duration = container.getDuration();
		// query for the file size
		long fileSize = container.getFileSize();
		// query for the bit rate
		long bitRate = container.getBitRate();
		System.out.println("Number of streams: " + numStreams);
		System.out.println("Duration (ms): " + duration);
		System.out.println("File Size (bytes): " + fileSize);
		System.out.println("Bit Rate: " + bitRate);
		// iterate through the streams to print their meta data
		for (int i=0; i<numStreams; i++) {
			// find the stream object
			IStream stream = container.getStream(i);
			// get the pre-configured decoder that can decode this stream;
			IStreamCoder coder = stream.getStreamCoder();
			System.out.println("*** Start of Stream Info ***");
			System.out.printf("stream %d: ", i);
			System.out.printf("type: %s; ", coder.getCodecType());
			System.out.printf("codec: %s; ", coder.getCodecID());
			System.out.printf("duration: %s; ", stream.getDuration());
			System.out.printf("start time: %s; ", container.getStartTime());
			System.out.printf("timebase: %d/%d; ", stream.getTimeBase().getNumerator(), stream.getTimeBase().getDenominator());
			System.out.printf("coder tb: %d/%d; ", coder.getTimeBase().getNumerator(), coder.getTimeBase().getDenominator());
			System.out.println();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				System.out.printf("sample rate: %d; ", coder.getSampleRate());
				System.out.printf("channels: %d; ", coder.getChannels());
				System.out.printf("format: %s", coder.getSampleFormat());
			} 
			else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				System.out.printf("width: %d; ", coder.getWidth());
				System.out.printf("height: %d; ", coder.getHeight());
				System.out.printf("format: %s; ", coder.getPixelType());
				System.out.printf("frame-rate: %5.2f; ", coder.getFrameRate().getDouble());
			}
			System.out.println();
			System.out.println("*** End of Stream Info ***");
		}
		System.out.println("");
	}
}

