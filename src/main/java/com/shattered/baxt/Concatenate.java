package com.shattered.baxt;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.shattered.baxt.video.IMediaReader;
import com.shattered.baxt.video.IMediaWriter;
import com.shattered.baxt.video.ToolFactory;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseCoderEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IOpenCoderEvent;
import com.xuggle.mediatool.event.IOpenEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.BgrConverter;

/** 
 * A very simple media transcoder which uses {@link IMediaReader}, {@link
 * IMediaWriter} and {@link IMediaViewer}.
 */

public class Concatenate
{
	/**
	 * Concatenate two files.
	 * 
	 * @param args 3 strings; an input file 1, input file 2, and an output file.
	 */


	public static void concatenate(ArrayList<FileSpecOptions> fileSpecs, String destinationUrl) {


		//////////////////////////////////////////////////////////////////////
		//                                                                  //
		// NOTE: be sure that the audio and video parameters match those of //
		// your input media                                                 //
		//                                                                  //
		//////////////////////////////////////////////////////////////////////

		// video parameters

		final int videoStreamIndex = 0;
		final int videoStreamId = 0;
		int width = 1920;
		int height = 1080	;
		// audio parameters

		final int audioStreamIndex = 1;
		final int audioStreamId = 0;
		final int channelCount = 2;
		final int sampleRate = 48000; // Hz


		ArrayList<IMediaReader> readers = new ArrayList<>();
		for(FileSpecOptions file : fileSpecs) {
			IMediaReader tempReader = ToolFactory.makeReader(file.getInputURL());
			tempReader.setDuration(file.getDuration());
			if(file.getTrim() <= 0) { 
				tempReader.setTrim(0) ;
			}else {
				tempReader.setTrim(file.getTrim() + 75) ;
			}
			try {
			tempReader.setNewStart(ThreadLocalRandom.current().nextLong(501, (file.getDuration() - file.getTrim())) - 500);
			}catch(IllegalArgumentException ia) { 
				tempReader.setNewStart(0);
				tempReader.setTrim(0);
				System.out.println("TRIM LENGTH LONGER THAN VIDEO -> NO TRIM");
			}
			tempReader.setRotation(file.getRotation());
			tempReader.setCutchecker(new CutChecker());
			tempReader.addListener(tempReader.getCutchecker());
			readers.add(tempReader);
		}

		// create the first media reader


		MediaConcatenator concatenator = new MediaConcatenator(audioStreamIndex,
				videoStreamIndex);
		// concatenator listens to both readers



		// create the media writer which listens to the concatenator
		IMediaWriter writer = ToolFactory.makeWriter(destinationUrl);

		concatenator.addListener(writer);

		// add the video stream

		writer.addVideoStream(videoStreamIndex, videoStreamId, width, height);

		// add the audio stream

		writer.addAudioStream(audioStreamIndex, audioStreamId, channelCount,
				sampleRate);
		
		for(IMediaReader reader : readers) { 
			concatenator.setConcatRotation(reader.getRotation());
			long trim = reader.getTrim();
			long newStart = reader.getNewStart();
			CutChecker cutchecker = reader.getCutchecker();

			boolean recordStart = false ; 
			while (reader.readPacket() == null) {
				long cutCheckerTime = cutchecker.timeInMilisec;

				if(trim == 0 & !recordStart) {
					recordStart = true ;
					reader.addListener(concatenator);
					System.out.println("ts    " + cutCheckerTime);
				}
				if(trim > 0 && !recordStart && cutCheckerTime >= newStart - 50) { 
					recordStart = true ;
					reader.addListener(concatenator);
					System.out.println("r1   " + cutCheckerTime);
				}
				if(trim > 0 && cutCheckerTime > (newStart + trim)){
					System.out.println("f1  " +cutCheckerTime);
					System.out.println("trimfin1");
					break;
				}
			}

		}

		writer.close();

	}


	static class MediaConcatenator extends MediaToolAdapter
	{

		// the next video timestamp

		private long mNextVideo = 0;

		// the next audio timestamp

		private long mNextAudio = 0;

		// the index of the video stream

		private final int mVideoStreamIndex;

		String rotate = "0";

		long properTimeStamp = 0;
		long properTimeStep = 0;
		long compTimeStamp = 0;
		boolean first = false;
		boolean second = false;

		/**
		 * Create a concatenator.
		 * 
		 * @param audioStreamIndex index of audio stream
		 * @param videoStreamIndex index of video stream
		 * @param width 
		 * @param height 
		 */

		public MediaConcatenator(int audioStreamIndex, int videoStreamIndex)
		{
			mVideoStreamIndex = videoStreamIndex;
		}

		public void setConcatRotation(String newRotate) {
			System.out.println("****************");

			rotate = newRotate;
			first = false ;
		}
		
		public void onAudioSamples(IAudioSamplesEvent event)
		{
			//REMOVED ALL ORIGINAL AUDIO CODE
		}


		public BufferedImage checkRotate(BufferedImage img) {
			if( (rotate == null || rotate.equals(null) ||rotate.isEmpty() || rotate == ("0"))) {	
				return img;
			}
			BufferedImage input  = VideoRotator.rotateImage(Integer.valueOf(rotate), img);
			return input;
		}

		public void onVideoPicture(IVideoPictureEvent event)
		{	
			IVideoPicture picture = event.getMediaData();
			long originalTimeStamp = picture.getTimeStamp();
			//long newTimeStamp = properTimeStamp;
			IVideoResampler resampler = null;
			resampler = IVideoResampler.make(picture.getWidth(), 
					picture.getHeight(), IPixelFormat.Type.YUV420P,
					picture.getWidth(), picture.getHeight(), picture.getPixelType());
			IVideoPicture newPic = picture;
			newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
					picture.getWidth(), picture.getHeight());
			resampler.resample(newPic, picture);
			//System.out.println(newPic.getPixelType());
			BufferedImage img = VideoRotator.resizeImage(newPic);
			img = checkRotate(img);
			BgrConverter convert1 = new BgrConverter(IPixelFormat.Type.YUV420P, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
			//System.out.println("input   " + img.getWidth() + "  :   " + img.getHeight());

			if(first) {
				/*if(second) {
					if (originalTimeStamp - compTimeStamp != properTimeStep) {
						second = false;
						System.out.println("1");
					}else {
						properTimeStep = originalTimeStamp - compTimeStamp;
						System.out.println("2");
					}
				}else {*/
					properTimeStep = originalTimeStamp - compTimeStamp;
			}
			properTimeStamp += properTimeStep;
			picture = convert1.toPicture(img, properTimeStamp);

			compTimeStamp = originalTimeStamp;

			// set the new time stamp to the original plus the offset established
			// for this media file

			if(!first) { //compTimeStamp == 0 || originalTimeStamp >= compTimeStamp) { 
				first = true;
			}


			// keep track of predicted time of the next video picture, if the end
			// of the media file is encountered, then the offset will be adjusted
			// to this this time.
			//
			// You'll note in the audio samples listener above we used
			// a method called getNextPts().  Video pictures don't have
			// a similar method because frame-rates can be variable, so
			// we don't now.  The minimum thing we do know though (since
			// all media containers require media to have monotonically
			// increasing time stamps), is that the next video timestamp
			// should be at least one tick ahead.  So, we fake it.
			mNextVideo = originalTimeStamp + 1;
			//System.out.println(originalTimeStamp/1000);
			// set the new timestamp on video samples
			//System.out.println("**");
			// picture.setTimeStamp(newTimeStamp);//
			//System.out.println("nextTs : " + properTimeStamp/1000);
			//System.out.println("--------");
			//System.out.println("");
			// create a new video picture event with the one true video stream
			// index

			super.onVideoPicture(new VideoPictureEvent(this, picture,
					mVideoStreamIndex));
		}

		public void onClose(ICloseEvent event)
		{
			// update the offset by the larger of the next expected audio or video
			// frame time

			if (mNextAudio < mNextVideo)
			{
				// In this case we know that there is more video in the
				// last file that we read than audio. Technically you
				// should pad the audio in the output file with enough
				// samples to fill that gap, as many media players (e.g.
				// Quicktime, Microsoft Media Player, MPlayer) actually
				// ignore audio time stamps and just play audio sequentially.
				// If you don't pad, in those players it may look like
				// audio and video is getting out of sync.

				// However kiddies, this is demo code, so that code
				// is left as an exercise for the readers. As a hint,
				// see the IAudioSamples.defaultPtsToSamples(...) methods.

			}
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
