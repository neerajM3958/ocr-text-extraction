import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public final class Utils
{
	/**
	 * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
	 *
	 * @param frame
	 *            the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	public static Image mat2Image(Mat frame)
	{
		try
		{
			return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
		}
		catch (Exception e)
		{
			System.err.println("Cannot convert the Mat obejct: " + e);
			return null;
		}
	}
	
	/**
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * 
	 * @param property
	 *            a {@link ObjectProperty}
	 * @param value
	 *            the value to set for the given {@link ObjectProperty}
	 */
	public static <T> void onFXThread(final ObjectProperty<T> property, final T value)
	{
		Platform.runLater(() -> {
			property.set(value);
		});
	}
	
	/**
	 * Support for the {@linkmat2image()} method
	 * 
	 * @param original
	 *            the {@link Mat} object in BGR or grayscale
	 * @return the corresponding {@link BufferedImage}
	 */
	public static BufferedImage matToBufferedImage(Mat original)
	{
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);
		
		if (original.channels() > 1)
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}
		else
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		
		return image;
	}
	public static Mat img2Mat(BufferedImage in)
	{
		Mat out;
		byte[] data;
		int width=in.getWidth(),height= in.getHeight();
		out = new Mat(height, width, CvType.CV_8UC3);
		data = new byte[width * height * (int)out.elemSize()];
		int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
		for(int i = 0; i < dataBuff.length; i++)
		{
			data[i*3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
			data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
			data[i*3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
		}
		out.put(0, 0, data);
		return out;
	}
	/*
	public static Mat img2MatGray(BufferedImage in)
	{
		Mat out;
		byte[] data;
		int r, g, b;
		int width=in.getWidth(),height= in.getHeight();
		*//*if(in.getType() == BufferedImage.TYPE_INT_RGB)
		{
		out = new Mat(height, width, CvType.CV_8UC3);
		data = new byte[width * height * (int)out.elemSize()];
		int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
		for(int i = 0; i < dataBuff.length; i++)
		{
			data[i*3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
			data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
			data[i*3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
		}
		}
		else
		{*//*
			out = new Mat(height, width, CvType.CV_8UC1);
			data = new byte[width * height * (int)out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, width, height, null, 0, width);
			for(int i = 0; i < dataBuff.length; i++)
			{
				r = (byte) ((dataBuff[i] >> 16) & 0xFF);
				g = (byte) ((dataBuff[i] >> 8) & 0xFF);
				b = (byte) ((dataBuff[i] >> 0) & 0xFF);
				data[i] = (byte)((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
			}
		//}
		out.put(0, 0, data);
		return out;
	}*/
}
