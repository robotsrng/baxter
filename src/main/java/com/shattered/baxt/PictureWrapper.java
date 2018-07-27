package com.shattered.baxt;

import org.apache.commons.codec.binary.Base64;

public class PictureWrapper
{

	private byte[] image;

	protected String mediaFileName;


	public PictureWrapper(byte[] input, String name) {
		setImage(input);
		setMediaFileName(name);
	}


	public byte[] getImage()
	{
		return image;
	}

	public String generateBase64Image()
	{
		return Base64.encodeBase64String(this.getImage());
	}

	public void setImage(byte[] image)
	{
		this.image = image;
	}

	public String getMediaFileName()
	{
		return mediaFileName;
	}

	public void setMediaFileName(String mediaFileName)
	{
		this.mediaFileName = mediaFileName;
	}
}
