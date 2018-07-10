package com.shattered.baxt;

import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IPixelFormat.Type;

public class FileSpecOptions {

	String filename = null;
	String inputURL = null;
	String outputURL = null ;

	private boolean mHasAudio = false;
	private boolean mHasVideo = true;

	private boolean mRealTimeEncoder = false;

	String acodec = null ;
	String vcodec = null ;
	String containerFormat = "mpeg4";
	int astream = -1 ;
	int aquality = 0;

	int sampleRate = 48000;
	int channels = 0;
	int abitrate = 150000;
	//	int vbitrate = 25000000;
	int vbitrate = 1000000;

	int vbitratetolerance = 0 ;
	int vquality  = -1;
	int vstream = -1 ;
	double vscaleFactor = 1.0 ;

	String icontainerFormat = null;
	String iacodec = null;
	int isampleRate = 0;
	int ichannels = 0 ;
	String cpreset = null;
	
	
	int height = 0;
	int width = 0;
	private String rotation ;
	long trim ; 
	long duration ;

	Type pixType = IPixelFormat.Type.YUVJ420P ;



	public String getInputURL() {
		return inputURL;
	}
	public void setInputURL(String inputURL) {
		this.inputURL = inputURL;
	}
	public String getOutputURL() {
		return outputURL;
	}
	public void setOutputURL(String outputURL) {
		this.outputURL = outputURL;
	}
	public boolean ismHasAudio() {
		return mHasAudio;
	}
	public void setmHasAudio(boolean mHasAudio) {
		this.mHasAudio = mHasAudio;
	}
	public boolean ismHasVideo() {
		return mHasVideo;
	}
	public void setmHasVideo(boolean mHasVideo) {
		this.mHasVideo = mHasVideo;
	}
	public boolean ismRealTimeEncoder() {
		return mRealTimeEncoder;
	}
	public void setmRealTimeEncoder(boolean mRealTimeEncoder) {
		this.mRealTimeEncoder = mRealTimeEncoder;
	}
	public String getAcodec() {
		return acodec;
	}
	public void setAcodec(String acodec) {
		this.acodec = acodec;
	}
	public String getVcodec() {
		return vcodec;
	}
	public void setVcodec(String vcodec) {
		this.vcodec = vcodec;
	}
	public String getContainerFormat() {
		return containerFormat;
	}
	public void setContainerFormat(String containerFormat) {
		this.containerFormat = containerFormat;
	}
	public int getAstream() {
		return astream;
	}
	public void setAstream(int astream) {
		this.astream = astream;
	}
	public int getAquality() {
		return aquality;
	}
	public void setAquality(int aquality) {
		this.aquality = aquality;
	}
	public int getSampleRate() {
		return sampleRate;
	}
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
	public int getChannels() {
		return channels;
	}
	public void setChannels(int channels) {
		this.channels = channels;
	}
	public int getAbitrate() {
		return abitrate;
	}
	public void setAbitrate(int abitrate) {
		this.abitrate = abitrate;
	}
	public int getVbitrate() {
		return vbitrate;
	}
	public void setVbitrate(int vbitrate) {
		this.vbitrate = vbitrate;
	}
	public int getVbitratetolerance() {
		return vbitratetolerance;
	}
	public void setVbitratetolerance(int vbitratetolerance) {
		this.vbitratetolerance = vbitratetolerance;
	}
	public int getVquality() {
		return vquality;
	}
	public void setVquality(int vquality) {
		this.vquality = vquality;
	}
	public int getVstream() {
		return vstream;
	}
	public void setVstream(int vstream) {
		this.vstream = vstream;
	}
	public double getVscaleFactor() {
		return vscaleFactor;
	}
	public void setVscaleFactor(double vscaleFactor) {
		this.vscaleFactor = vscaleFactor;
	}
	public String getIcontainerFormat() {
		return icontainerFormat;
	}
	public void setIcontainerFormat(String icontainerFormat) {
		this.icontainerFormat = icontainerFormat;
	}
	public String getIacodec() {
		return iacodec;
	}
	public void setIacodec(String iacodec) {
		this.iacodec = iacodec;
	}
	public int getIsampleRate() {
		return isampleRate;
	}
	public void setIsampleRate(int isampleRate) {
		this.isampleRate = isampleRate;
	}
	public int getIchannels() {
		return ichannels;
	}
	public void setIchannels(int ichannels) {
		this.ichannels = ichannels;
	}
	public String getCPreset() {
		return cpreset;
	}
	public void setCpreset(String cpreset) {
		this.cpreset = cpreset;
	}
	public String getRotation() {
		return rotation;
	}
	public void setRotation(String rotation) {
		this.rotation = rotation;
	}
	public Long getTrim() {
		return trim;
	}
	public void setTrim(long trim) {
		this.trim = trim * 1000;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration ;
	}
	public Type getPixType() {
		return pixType;
	}
	public void setPixType(Type pixType) {
		this.pixType = pixType;
	}
	public String getCpreset() {
		return cpreset;
	}

}
