package com.shattered.baxt;

import com.shattered.baxt.video.IMediaWriter;
import com.shattered.baxt.video.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

public class AudioFormatConverter {

	public static void convert( String input, String output) {
		String inputAudioFilePath = input;
		String outputAudioFilePath = output;

		IMediaWriter mWriter = ToolFactory.makeWriter(outputAudioFilePath);

		IContainer containerAudio = IContainer.make();

		// check files are readable
		if (containerAudio.open(inputAudioFilePath, IContainer.Type.READ, null) < 0)
			throw new IllegalArgumentException("Cant find " + inputAudioFilePath);

		// read audio file and create stream
		IStreamCoder coderAudio = containerAudio.getStream(0).getStreamCoder();
		if (coderAudio.open(null, null) < 0)
			throw new RuntimeException("Cant open audio coder");
		IPacket packetaudio = IPacket.make();

		mWriter.addAudioStream(1, 0, coderAudio.getChannels(), coderAudio.getSampleRate());

		//Dummy read one packet to prevent missing header errors on some 
		containerAudio.readNextPacket(packetaudio); 
		while(containerAudio.readNextPacket(packetaudio) >= 0) 
		{ 
			IAudioSamples samples = IAudioSamples.make(512, coderAudio.getChannels(), IAudioSamples.Format.FMT_S32);
			coderAudio.decodeAudio(samples, packetaudio, 0);
			if (samples.isComplete()) 
				mWriter.encodeAudio(1, samples);
		}

		coderAudio.close();
		containerAudio.close();
		mWriter.close();
	}

}
