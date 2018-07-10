package com.shattered.baxt;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class CutChecker extends MediaToolAdapter {

	public Long timeInMilisec = (long) 0;

	@Override
	public void onVideoPicture(IVideoPictureEvent event)
	{
		timeInMilisec = event.getTimeStamp()/1000;
		super.onVideoPicture(event);
	}
}