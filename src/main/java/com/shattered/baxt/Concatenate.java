package com.shattered.baxt;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import com.shattered.baxt.video.IMediaReader;
import com.shattered.baxt.video.IMediaWriter;
import com.shattered.baxt.video.ToolFactory;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.AudioSamplesEvent;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseCoderEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IOpenCoderEvent;
import com.xuggle.mediatool.event.IOpenEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.BgrConverter;

public class Concatenate{

	public static String concatenate(FileSpecRepo fsr) {

		final int videoStreamIndex = 0;
		final int videoStreamId = 0;
		int outWidth = 1280;
		int outHeight = 720;

		long audioStartTime = (long) fsr.getSoundTrim() * 1000000 ;
		final int audioStreamIndex = 1;
		final int audioStreamId = 0;
		final int channelCount = 2;
		int sampleRate = 44100; // Hz

		String inputAudioFilePath = fsr.getSoundDirectory();
		IContainer containerAudio;
		IStreamCoder coderAudio;
		containerAudio = IContainer.make();
		if (containerAudio.open(inputAudioFilePath, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("Cant find " + inputAudioFilePath);
		coderAudio = containerAudio.getStream(0).getStreamCoder();
		if (coderAudio.getCodecType() != ICodec.Type.CODEC_TYPE_AUDIO) { 
			coderAudio = containerAudio.getStream(1).getStreamCoder();
		}
		if (coderAudio.open(null, null) < 0)
			throw new RuntimeException("Cant open audio coder");
		sampleRate = coderAudio.getSampleRate();
		coderAudio.close();
		containerAudio.close();

		long maxLength = 0 ;

		ArrayList<IMediaReader> readers = new ArrayList<>();
		for(FileSpec file : fsr.getSpecs()) {
			IMediaReader tempReader = ToolFactory.makeReader(file.getInputURL());
			Long trimStart = (long) file.getTrimStart() * 1000;
			Long trimEnd = (long) file.getTrimEnd() * 1000;
			tempReader.setDuration(file.getDuration());
			if(trimStart - 100 <= 0) { 
				tempReader.setTrimStart(0) ;
			}else {
				tempReader.setTrimStart(trimStart - 100) ;
			}
			if(trimEnd + 100 >= tempReader.getDuration()) {
				tempReader.setTrimEnd(tempReader.getDuration());
				System.out.println("TRIM LENGTH LONGER THAN VIDEO -> NO TRIM");
			}else {
				tempReader.setTrimEnd(trimEnd + 100);
			}
			tempReader.setRotation(file.getRotation());
			tempReader.setCutchecker(new CutChecker());
			tempReader.addListener(tempReader.getCutchecker());
			maxLength += (tempReader.getTrimEnd() - tempReader.getTrimStart()) * 1000;
			readers.add(tempReader);
		}

		// create the first media reader

		MediaConcatenator concatenator = new MediaConcatenator(audioStreamIndex,
				videoStreamIndex, inputAudioFilePath, maxLength, audioStartTime, outWidth, outHeight);

		String outfileName =File.separator + ( Math.random() * Math.abs((fsr.getSoundDirectory().hashCode() + fsr.getSpecs().get(0).getOrigFilename().hashCode() / Math.random()))) + "_baxter.mp4" ;

		// create the media writer which listens to the concatenator

		IMediaWriter writer = ToolFactory.makeWriter(fsr.getOutputDirectory() + outfileName );

		concatenator.addListener(writer);

		// add the video stream

		writer.addVideoStream(videoStreamIndex, videoStreamId, outWidth, outHeight);

		// add the audio stream

		writer.addAudioStream(audioStreamIndex, audioStreamId, channelCount, sampleRate);

		for(IMediaReader reader : readers) { 
			concatenator.setConcatRotation(reader.getRotation());
			long trimStart = reader.getTrimStart();
			long trimEnd = reader.getTrimEnd();
			CutChecker cutchecker = reader.getCutchecker();
			boolean recordStart = false ; 
			while (reader.readPacket() == null) {
				long cutCheckerTime = cutchecker.timeInMilisec;
				if(cutCheckerTime > trimEnd){
					break;
				}
				if(!recordStart && cutCheckerTime >= trimStart) { 
					recordStart = true ;
					reader.addListener(concatenator);
				}
			}
			reader.removeListener(reader.getCutchecker());
			reader.removeListener(concatenator);
		}
		while(concatenator.aProperTimeStamp < maxLength && concatenator.aProperTimeStamp >= 0) { 
			concatenator.onAudioSamples(null);
		}
		writer.close();
		System.out.println("BAXTED");
		return fsr.getOutputDirectory() + outfileName;
	}

	static class MediaConcatenator extends MediaToolAdapter{
		
		private final int mVideoStreamIndex;
		private final int mAudioStreamIndex;
		
		int outWidth ;
		int outHeight ;

		String rotate = "0";

		long audioStartTime = 0;
		long aProperTimeStamp = 0;
		long aProperTimeStep = 0;
		long aCompTimeStamp = 0;

		long vOrigTimeStamp = 0;
		long vProperTimeStamp = 0;
		long vProperTimeStep = 0;
		long vCompTimeStamp = 0;

		boolean first = false;
		boolean second = false;
		boolean third = false;
		
		IVideoResampler resampler = null ;
		Dimension newPictureDimensions = null;

		long maxLength;
		String inputAudioFilePath = File.separator + "home" + File.separator + "srng" + File.separator + "baxter" + File.separator + "panic.mp3";
		IContainer containerAudio;
		IStreamCoder coderAudio;
		IPacket packetaudio;

		public MediaConcatenator(int audioStreamIndex, int videoStreamIndex, String inputAudioFilePath, long length, long audioStart, int width, int height )
		{
			this.inputAudioFilePath = inputAudioFilePath ;
			this.outWidth = width ;
			this.outHeight = height;
			mVideoStreamIndex = videoStreamIndex;
			mAudioStreamIndex = audioStreamIndex;
			containerAudio = IContainer.make();
			if (containerAudio.open(this.inputAudioFilePath, IContainer.Type.READ, null) < 0)
				throw new IllegalArgumentException("Cant find " + inputAudioFilePath);
			coderAudio = containerAudio.getStream(0).getStreamCoder();
			if (coderAudio.getCodecType() != ICodec.Type.CODEC_TYPE_AUDIO) {
				coderAudio = containerAudio.getStream(1).getStreamCoder();
			}
			coderAudio.setSampleRate(44100);
			if (coderAudio.open(null, null) < 0)
				throw new RuntimeException("Cant open audio coder");
			packetaudio = IPacket.make();
			maxLength = length;
			audioStartTime = audioStart;
			if ( audioStart > 0) { 
				while(true) {
					IAudioSamples newsamples = IAudioSamples.make(1024, 2, Format.FMT_S16 );
					containerAudio.readNextPacket(packetaudio);
					coderAudio.decodeAudio(newsamples, packetaudio, 0);
					if(newsamples.getPts() > audioStart - 1000) {
						break;
					}
				}
			}
		}
		
		public void onVideoPicture(IVideoPictureEvent event){	
			IVideoPicture picture = event.getMediaData();
			long originalTimeStamp = picture.getTimeStamp();
			BufferedImage img = null;
			if(!rotate.equals("0")) { 
				newPictureDimensions = getScaledDimension(picture.getWidth(), picture.getHeight(), outHeight, outHeight);
			}else { 
				newPictureDimensions = getScaledDimension(picture.getWidth(), picture.getHeight(), outWidth, outHeight);
			}
			if((newPictureDimensions.getWidth() != picture.getWidth() || newPictureDimensions.getHeight() != picture.getHeight()) || picture.getPixelType() != IPixelFormat.Type.YUV420P) {
				if (resampler == null) {
					resampler = IVideoResampler.make((int) newPictureDimensions.getWidth(), (int) newPictureDimensions.getHeight(), IPixelFormat.Type.YUV420P, picture.getWidth(), picture.getHeight(), picture.getPixelType());
				}
				IVideoPicture out = IVideoPicture.make(IPixelFormat.Type.YUV420P, (int) newPictureDimensions.getWidth(), (int) newPictureDimensions.getHeight());
				resampler.resample(out, picture);
				picture = out;
			}
			if(!rotate.equals("0") || (picture.getWidth() != outWidth || picture.getHeight() != outHeight)) { 
				img = VideoRotator.preBaxtImg(Integer.parseInt(rotate), picture, outWidth, outHeight);
			}
			if(first) {
				vProperTimeStep = originalTimeStamp - vCompTimeStamp;
			}
			vProperTimeStamp += vProperTimeStep;
			if(img != null) { 
				BgrConverter convert1 = new BgrConverter(IPixelFormat.Type.YUV420P, outWidth, outHeight, outWidth, outHeight);
				picture = convert1.toPicture(img, vProperTimeStamp);
			}else {
				picture.setTimeStamp(vProperTimeStamp);
			}
			vCompTimeStamp = originalTimeStamp;
			if(!first) { 
				first = true;
			}
			super.onVideoPicture(new VideoPictureEvent(this, picture, mVideoStreamIndex));
		}
		
		public void onAudioSamples(IAudioSamplesEvent event){
			IAudioSamples samples = null ;
			IAudioSamples newsamples = null ;
			newsamples = IAudioSamples.make(1024, 2, Format.FMT_S16 );
			containerAudio.readNextPacket(packetaudio);
			coderAudio.decodeAudio(newsamples, packetaudio, 0);
			long originalTimeStamp = newsamples.getNextPts();
			if(originalTimeStamp >= audioStartTime) { 
				samples = newsamples ;
				if(second && !third) {
					aProperTimeStep = originalTimeStamp - aCompTimeStamp;
				}
				aProperTimeStamp += aProperTimeStep;
				samples.setTimeStamp(aProperTimeStamp );
				aCompTimeStamp = originalTimeStamp;
				if(!second) { 
					second = true;
				}
				if(samples.getTimeStamp() <= maxLength) {
					super.onAudioSamples(new AudioSamplesEvent(this, samples, mAudioStreamIndex));
				}
			}
			if(originalTimeStamp >= containerAudio.getDuration() || originalTimeStamp < 0) { 
				resetAudio();
				third = true ;
			}
		}

		public void resetAudio() {
			containerAudio = IContainer.make();
			if (containerAudio.open(this.inputAudioFilePath, IContainer.Type.READ, null) < 0)
				throw new IllegalArgumentException("Cant find " + inputAudioFilePath);
			coderAudio = containerAudio.getStream(0).getStreamCoder();
			if (coderAudio.getCodecType() != ICodec.Type.CODEC_TYPE_AUDIO) {
				coderAudio = containerAudio.getStream(1).getStreamCoder();
			}
			coderAudio.setSampleRate(44100);
			if (coderAudio.open(null, null) < 0)
				throw new RuntimeException("Cant open audio coder");
			packetaudio = IPacket.make();
			if ( audioStartTime > 0) { 
				while(true) {
					IAudioSamples newsamples = IAudioSamples.make(1024, 2, Format.FMT_S16 );
					containerAudio.readNextPacket(packetaudio);
					coderAudio.decodeAudio(newsamples, packetaudio, 0);
					if(newsamples.getPts() > audioStartTime - 1000) {
						break;
					}
				}
			}
		}

		public void setConcatRotation(String newRotate) {
			System.out.println("****************");
			System.out.println(newRotate);
			rotate = newRotate;
			first = false ;
			second = false;
			resampler = null ;
			newPictureDimensions = null ;
		}

		//TODO Add aspect scaling
		public static Dimension getScaledDimension(int width, int height, int boundW, int boundH) {
			int original_width = width;
			int original_height = height;
			int bound_width = boundW ;
			int bound_height = boundH ;
			int new_width = original_width;
			int new_height = original_height;
//TODO change this, return array of doubles. if there is a decimal, I need to crop last pixel out
			 if(original_width < bound_width) { 
				new_width = bound_width;
				new_height = (new_width * original_height) / original_width;
			}
			else if (new_height < bound_height) {
				new_height = bound_height;
				new_width = (new_height * original_width) / original_height;
			}
			if (original_width > bound_width) {
				new_width = bound_width;
				new_height = (new_width * original_height) / original_width;
			}
			if (new_height > bound_height) {
				new_height = bound_height;
				new_width = (new_height * original_width) / original_height;
			}
			return new Dimension(new_width , new_height);
		}

		public void onClose(ICloseEvent event)
		{
			// overridden to ensure that close coder events are not passed down the
			// tool chain to the writer, which could cause problems
		}

		public void onAddStream(IAddStreamEvent event)
		{
			// overridden to ensure that add stream events are not passed down
			// the tool chain to the writer, which could cause problems
		}

		public void onOpen(IOpenEvent event)
		{
			// overridden to ensure that open events are not passed down the tool
			// chain to the writer, which could cause problems
		}

		public void onOpenCoder(IOpenCoderEvent event)
		{
			// overridden to ensure that open coder events are not passed down the
			// tool chain to the writer, which could cause problems
		}

		public void onCloseCoder(ICloseCoderEvent event)
		{
			// overridden to ensure that close coder events are not passed down the
			// tool chain to the writer, which could cause problems
		}
	}
}
