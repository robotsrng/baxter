package com.shattered.baxt;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.BgrConverter;

public class VideoRotator extends MediaToolAdapter {

	public VideoRotator() {
	}

	public static BufferedImage resizeImage(IVideoPicture img) {
		BgrConverter convert1 = new BgrConverter(IPixelFormat.Type.YUV420P, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
		BufferedImage input = convert1.toImage(img);
		if(input.getWidth() != 1920 || input.getHeight() != 1080) { 
			BufferedImage resized = new BufferedImage(1920, 1080, input.getType());
			Graphics2D g3 = resized.createGraphics();
			g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g3.drawImage(input, 0, 0, 1920, 1080, 0, 0, img.getWidth(),
					img.getHeight(), null);
			return resized;
		}
		return input;
	}
	public static BufferedImage rotateImage(int rotate, BufferedImage img) {

		if (rotate == 0 || img == null) {
			return img;
		}
		BufferedImage resized = new BufferedImage(1080, 608, img.getType());
		Graphics2D g3 = resized.createGraphics();
		g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g3.drawImage(img, 0, 0, 1080, 608, 0, 0, img.getWidth(),
				img.getHeight(), null);
		int width = resized.getWidth();
		int height = resized.getHeight();
		int new_w = 0, new_h = 0;
		int new_radian = rotate;
		if (rotate <= 90) {
			new_w = (int)(width * Math.cos(Math.toRadians(new_radian)) + height * Math.sin(Math.toRadians(new_radian)));
			new_h = (int)(height * Math.cos(Math.toRadians(new_radian)) + width * Math.sin(Math.toRadians(new_radian)));
		} else if (rotate <= 180) {
			new_radian = rotate - 90;
			new_w = (int)(height * Math.cos(Math.toRadians(new_radian)) + width * Math.sin(Math.toRadians(new_radian)));
			new_h = (int)(width * Math.cos(Math.toRadians(new_radian)) + height * Math.sin(Math.toRadians(new_radian)));
		} else if (rotate <= 270) {
			new_radian = rotate - 180;
			new_w = (int)(width * Math.cos(Math.toRadians(new_radian)) + height * Math.sin(Math.toRadians(new_radian)));
			new_h = (int)(height * Math.cos(Math.toRadians(new_radian)) + width * Math.sin(Math.toRadians(new_radian)));
		} else {
			new_radian = rotate - 270;
			new_w = (int)(height * Math.cos(Math.toRadians(new_radian)) +
					width * Math.sin(Math.toRadians(new_radian)));
			new_h = (int)(width * Math.cos(Math.toRadians(new_radian)) +
					height * Math.sin(Math.toRadians(new_radian)));
		}
		BufferedImage toStore = new	BufferedImage(new_w, new_h, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = toStore.createGraphics();
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.rotate(Math.toRadians(rotate), width / 2, height / 2);
		if (rotate != 180) {
			AffineTransform translationTransform =
					findTranslation(affineTransform, resized, rotate);
			affineTransform.preConcatenate(translationTransform);
		}
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, new_w, new_h);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawRenderedImage(resized, affineTransform);
		BufferedImage background = new
				BufferedImage(1920, 1080, BufferedImage.TYPE_3BYTE_BGR);
		int halfPoint = (background.getWidth()/2) - (toStore.getWidth()/2); 
		Graphics2D g2 = background.createGraphics();
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, background.getWidth(), background.getHeight());
		addImage(background, toStore, 1, halfPoint, 0) ;
		return background;
	}

	private static void addImage(BufferedImage buff1, BufferedImage buff2,
			float opaque, int x, int y) {
		Graphics2D g2d = buff1.createGraphics();
		g2d.setComposite(
				AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opaque));
		g2d.drawImage(buff2, x, y, null);
		g2d.dispose();
	}

	private static AffineTransform findTranslation(AffineTransform at,
			BufferedImage bi, int angle) { //45
		Point2D p2din, p2dout;
		double ytrans = 0.0, xtrans = 0.0;
		if (angle <= 90) {
			p2din = new Point2D.Double(0.0, 0.0);
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(0, bi.getHeight());
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();
		}
		/*else if(angle<=135){
                p2din = new Point2D.Double(0.0, bi.getHeight());
                p2dout = at.transform(p2din, null);
                ytrans = p2dout.getY();

                p2din = new Point2D.Double(bi.getWidth(),bi.getHeight());
                p2dout = at.transform(p2din, null);
                xtrans = p2dout.getX();

            }*/
		else if (angle <= 180) {
			p2din = new Point2D.Double(0.0, bi.getHeight());
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();

		}
		/*else if(angle<=225){
                p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
                p2dout = at.transform(p2din, null);
                ytrans = p2dout.getY();

                p2din = new Point2D.Double(bi.getWidth(),0.0);
                p2dout = at.transform(p2din, null);
                xtrans = p2dout.getX();

            }*/
		else if (angle <= 270) {
			p2din = new Point2D.Double(bi.getWidth(), bi.getHeight());
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();

			p2din = new Point2D.Double(bi.getWidth(), 0.0);
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();

		} else {
			p2din = new Point2D.Double(bi.getWidth(), 0.0);
			p2dout = at.transform(p2din, null);
			ytrans = p2dout.getY();


			p2din = new Point2D.Double(0.0, 0.0);
			p2dout = at.transform(p2din, null);
			xtrans = p2dout.getX();

		}
		AffineTransform tat = new AffineTransform();
		tat.translate(-xtrans, -ytrans);
		return tat;
	}

}
