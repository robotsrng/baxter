package com.shattered.baxt.video;

import com.xuggle.mediatool.event.IRawMediaEvent;
import com.xuggle.xuggler.IAudioSamples;


public interface IAudioSamplesEvent extends IRawMediaEvent
{

  /**
   * {@inheritDoc}
   */
  public abstract IAudioSamples getMediaData();

  /**
   * Forwards to {@link #getMediaData()}.
   * @see #getMediaData()  
   */
  public abstract IAudioSamples getAudioSamples();

}