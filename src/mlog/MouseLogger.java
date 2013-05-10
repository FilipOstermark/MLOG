package mlog;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Uses a HashMap of PixelPoints and Integers to log mouse pointer positions over time.
 * 
 * @author Filip �stermark
 * @version 2013-05-10
 */
public class MouseLogger implements Runnable {

	private long sleepTimeMillis;
	private PointerInfo pointer;
	private HashMap<PixelPoint, Integer> pixelTimeLog;
	private volatile boolean running = true;

	private final boolean DEBUG = true;

	/**
	 * Logs the mouse pointer position.
	 */
	@Override
	public void run() {
		while (true) {
			while (!running) {
				Thread.yield();
			}
			this.pointer = MouseInfo.getPointerInfo();
			PixelPoint currentPointerLocation = new PixelPoint(this.pointer.getLocation());
			this.updatePixelTime(currentPointerLocation);

			if (DEBUG) {
				int mouseX = (int) currentPointerLocation.getX();
				int mouseY = (int) currentPointerLocation.getY();
				System.out.println("{ " + mouseX + " ; " + mouseY + " }");
			}

			try {
				Thread.sleep(this.sleepTimeMillis);
			} catch (InterruptedException ie) {
				// TODO: Catch exception
			}
		}
	}

	/**
	 * Creates a new MouseLogger that logs the mouse pointer position with a given time interval.
	 * 
	 * @param sleepTimeMillis	The amount of sleep between mouse pointer position log updates
	 */
	public MouseLogger(long sleepTimeMillis) {
		this.sleepTimeMillis = sleepTimeMillis;
		this.running = false;
		this.pixelTimeLog = new HashMap<PixelPoint, Integer>();
	}

	/**
	 * Starts the logging of mouse pointer positions.
	 */
	public void start() {
		this.running = true;
	}

	/**
	 * Pauses the logging of mouse pointer positions.
	 * 
	 * @throws InterruptedException TODO: Explanation needed
	 */
	public void pause() throws InterruptedException
	{
		this.running = false;
	}

	/**
	 * Prints the current log of mouse pointer positions on the standard output stream.
	 * 
	 * WARNING: The list can become very long for users with big resolutions. Should only be used for debugging.
	 */
	public void printLog() {
		if (DEBUG) {
			System.out.println("---MOUSE LOG---");
			Set<Entry<PixelPoint, Integer>> loggedPositionListKeySet = this.pixelTimeLog.entrySet();
			Iterator<Entry<PixelPoint, Integer>> it = loggedPositionListKeySet.iterator();
			while (it.hasNext()) {
				Entry<PixelPoint, Integer> entry = it.next();
				System.out.println("PIXEL: " + entry.getKey() + " , TIME: " + entry.getValue());
			}
			System.out.println("---------------");
		}
	}

	/**
	 * @return	The log of the mouse pointer positions over time.
	 */
	public HashMap<PixelPoint, Integer> getPixelTimeLog() {
		return this.pixelTimeLog;
	}

	/**
	 * Updates the log of mouse pointer positions over time.
	 * The value is incremented if the pixel was previously visited, otherwise it is set to 1.
	 */
	private void updatePixelTime(PixelPoint pixel) {
		Integer currentPixelTime;
		// Check if the pixel was previously visited
		if ((currentPixelTime = pixelTimeLog.get(pixel)) != null) {
			this.pixelTimeLog.put(pixel, currentPixelTime + 1);
		} else {
			this.pixelTimeLog.put(pixel, 1);
		}
	}
}