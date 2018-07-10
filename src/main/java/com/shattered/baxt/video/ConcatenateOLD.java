package com.shattered.baxt.video;

import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

import com.shattered.baxt.CutChecker;
import com.shattered.baxt.FileSpecOptions;
import com.shattered.baxt.VideoRotator;
import com.xuggle.mediatool.IMediaViewer;
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
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.video.BgrConverter;

/** 
 * A very simple media transcoder which uses {@link IMediaReader}, {@link
 * IMediaWriter} and {@link IMediaViewer}.
 */

public class ConcatenateOLD
{
	/**
	 * Concatenate two files.
	 * 
	 * @param args 3 strings; an input file 1, input file 2, and an output file.
	 */


	public static void concatenate(FileSpecOptions file1, FileSpecOptions file2, String destinationUrl) {


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

		long duration1;
		long trim1;
		long newStart1 ;

		long duration2;
		long trim2 ;
		long newStart2 ;

		duration1 = file1.getDuration();
		if(file1.getTrim() <= 0) { 
			trim1 = 0 ;
		}else {
			trim1 = file1.getTrim() + 200;
		}
		newStart1 = ThreadLocalRandom.current().nextLong(501, (duration1 - trim1 )) - 500;
		duration2 = file2.getDuration();
		if(file2.getTrim() <= 0) {
			trim2 = 0;
		}else {
			trim2 = file2.getTrim() + 200;
		}
		newStart2 = ThreadLocalRandom.current().nextLong(501, (duration2 - trim2 )) - 500;

		// create the first media reader

		IMediaReader reader1 = ToolFactory.makeReader(file1.getInputURL());
		CutChecker cutChecker1 = new CutChecker();
		reader1.addListener(cutChecker1);
		// create the second media reader

		IMediaReader reader2 = ToolFactory.makeReader(file2.getInputURL());
		CutChecker cutChecker2 = new CutChecker();
		reader2.addListener(cutChecker2);
		// create the media concatenator

		MediaConcatenator concatenator = new MediaConcatenator(audioStreamIndex,
				videoStreamIndex, file1, file2);
		// concatenator listens to both readers



		// create the media writer which listens to the concatenator
		IMediaWriter writer = ToolFactory.makeWriter(destinationUrl);

		concatenator.addListener(writer);

		// add the video stream

		writer.addVideoStream(videoStreamIndex, videoStreamId, width, height);

		// add the audio stream

		writer.addAudioStream(audioStreamIndex, audioStreamId, channelCount,
				sampleRate);

		boolean recordStart1 = false ; 
		while (reader1.readPacket() == null) {
			if(trim1 == 0 & !recordStart1) {
				recordStart1 = true ;
				reader1.addListener(concatenator);
				System.out.println("ts    " + cutChecker1.timeInMilisec);
			}
			if(trim1 > 0 && !recordStart1 && cutChecker1.timeInMilisec >= newStart1 - 50) { 
				recordStart1 = true ;
				reader1.addListener(concatenator);
				System.out.println("r1   " + cutChecker1.timeInMilisec);
			}
			if(trim1 > 0 && cutChecker1.timeInMilisec > (newStart1 + trim1)){
				System.out.println("f1  " +cutChecker1.timeInMilisec);
				System.out.println("trimfin1");
				break;
			}
		}

		concatenator.changeFileSpecs();
		System.out.println("trimstart2");
		// read packets from the second source file until done
		boolean recordStart2 = false ; 
		while (reader2.readPacket() == null) {
			if(trim2 == 0 & !recordStart1) {
				recordStart2 = true ;
				reader2.addListener(concatenator);
				System.out.println("ts2    " + cutChecker1.timeInMilisec);
			}
			if(trim2 > 0 && !recordStart2 && cutChecker2.timeInMilisec >= newStart2 - 50) { 
				recordStart2 = true ;
				reader2.addListener(concatenator);
				System.out.println("r2    " + cutChecker2.timeInMilisec);
			}
			if(trim2 > 0 && cutChecker2.timeInMilisec > (newStart2 + trim2)){
				System.out.println("f2  " + cutChecker2.timeInMilisec);
				System.out.println("trimfin2");
				break;
			}
		}
		// close the writer

		writer.close();

	}


	static class MediaConcatenator extends MediaToolAdapter
	{


		// the current offset

		private long mOffset = 0;

		// the next video timestamp

		private long mNextVideo = 0;

		// the next audio timestamp

		private long mNextAudio = 0;

		// the index of the audio stream

		private final int mAudoStreamIndex;

		// the index of the video stream

		private final int mVideoStreamIndex;


		int width ;
		int height  ;
		String rotate;
		int width2 ;
		int height2  ;
		String rotate2;

		long duration;
		long trim;
		long newStart ;

		long duration2;
		long trim2 ;
		long newStart2 ;

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

		public MediaConcatenator(int audioStreamIndex, int videoStreamIndex, FileSpecOptions file1, FileSpecOptions file2)
		{
			mAudoStreamIndex = audioStreamIndex;
			mVideoStreamIndex = videoStreamIndex;
			width = file1.getWidth();
			height = file1.getHeight();
			rotate = file1.getRotation();
			width2 = file2.getWidth();
			height2 = file2.getHeight();
			rotate2 = file2.getRotation();
		}

		public void changeFileSpecs() {
			System.out.println("***********");
			width = width2;
			height = height2;
			rotate = rotate2;
		}
		public void onAudioSamples(IAudioSamplesEvent event)
		{
			IAudioSamples samples = event.getAudioSamples();

			// set the new time stamp to the original plus the offset established
			// for this media file

			long newTimeStamp = samples.getNextPts() + mOffset;

			// keep track of predicted time of the next audio samples, if the end
			// of the media file is encountered, then the offset will be adjusted
			// to this time.
			mNextAudio = samples.getNextPts();

			// set the new timestamp on audio samples

			samples.setTimeStamp(newTimeStamp);

			// create a new audio samples event with the one true audio stream
			// index

			super.onAudioSamples(new AudioSamplesEvent(this, samples,
					mAudoStreamIndex));
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
				if(second) {
					if (originalTimeStamp - compTimeStamp != properTimeStep) {
						second = false;
						System.out.println("1");
					}else {
						properTimeStep = originalTimeStamp - compTimeStamp;
						System.out.println("2");
					}
				}else {
					properTimeStep = originalTimeStamp - compTimeStamp;
					second = true;
					System.out.println("3");
				}
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
			System.out.println(originalTimeStamp/1000);
			// set the new timestamp on video samples
			System.out.println("**");
			// picture.setTimeStamp(newTimeStamp);//
			System.out.println("nextTs : " + properTimeStamp/1000);
			System.out.println("--------");
			System.out.println("");
			// create a new video picture event with the one true video stream
			// index

			super.onVideoPicture(new VideoPictureEvent(this, picture,
					mVideoStreamIndex));
		}

		public void onClose(ICloseEvent event)
		{
			// update the offset by the larger of the next expected audio or video
			// frame time

			mOffset = properTimeStamp + properTimeStep ;// Math.max(mNextVideo, mNextAudio);
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
