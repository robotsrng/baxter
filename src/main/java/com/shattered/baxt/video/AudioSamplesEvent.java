package com.shattered.baxt.video;

import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaGenerator;
import com.xuggle.mediatool.event.ARawMediaMixin;
import com.xuggle.xuggler.IAudioSamples;

public class AudioSamplesEvent extends ARawMediaMixin implements IAudioSamplesEvent
{
	/**
	 * Create an {@link AudioSamplesEvent}.
	 * @param source the source
	 * @param samples the samples (must be non null).
	 * @param streamIndex the stream index of the stream that generated
	 *   these samples, or null if unknown.
	 * @throws IllegalArgumentException if samples is null.
	 */
	public AudioSamplesEvent (IMediaGenerator source,
			IAudioSamples samples,
			Integer streamIndex)
	{
		super(source, samples, null, samples.getTimeStamp(), TimeUnit.MICROSECONDS, streamIndex);
	}

	/**
	 * {@inheritDoc}
	 * 
	 *  @see com.xuggle.mediatool.event.IAudioSamplesEvent#getMediaData()
	 */
	@Override
	public IAudioSamples getMediaData()
	{
		return (IAudioSamples) super.getMediaData();
	}

	/**
	 * {@inheritDoc}
	 * @see com.xuggle.mediatool.event.IAudioSamplesEvent#getAudioSamples()
	 */
	public IAudioSamples getAudioSamples()
	{
		return getMediaData();
	}

}