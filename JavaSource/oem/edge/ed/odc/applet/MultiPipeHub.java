package oem.edge.ed.odc.applet;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

/**
 * A MultiPipeHub is responsible for transferring data from producers
 * (MultiPipeOutputStream) to consumers (MultiPipeInputStream). The programmer
 * does not use this class directly. Instead, the programmer creates 
 * MultiPipeInputStream and MultiPipeOutputStream objects and connects them
 * to each other. This object is created as needed to support the connections.
 */
import java.io.*;
import java.util.*;

public class MultiPipeHub {
	protected static int MP_BUFSIZE = 1024;
	protected byte[] buffer = new byte[MP_BUFSIZE];
	protected int in = -1;
	protected int out = 0;
	boolean closed = false;
	Vector consumers = new Vector();
	Vector producers = new Vector();
/**
 * Creates a MultiPipeHub object.
 */
MultiPipeHub() {
	super();
}
/**
 * Returns the number of bytes that can be read by a MultiPipeInputStream
 * from this hub. If more than 1 MultiPipeInputStream is connected to this
 * hub, the information may be unreliable.
 */
synchronized int available() throws IOException {
	if(in < 0)
		return 0;
	else if(in == out)
		return buffer.length;
	else if (in > out)
		return in - out;
	else
		return in + buffer.length - out;
}
/**
 * Connects the specified MultiPipeInputStream to this hub. If the hub is
 * closing, the connection is refused. If the MultiPipeInputStream is already
 * connected to this hub, an exception is thrown.
 */
synchronized void connect(MultiPipeInputStream consumer) throws IOException {
	// Previously open, but now closed?
	if (closed)
		throw new IOException("Multi-pipe closed.");

	// Consumer already connected?
	if (consumers.contains(consumer))
		throw new IOException("Already connected");

	// Connect the consumer.
	consumers.addElement(consumer);
}
/**
 * Connects the specified MultiPipeOutputStream to this hub. If the hub is
 * closing, the connection is refused. If the MultiPipeOutputStream is already
 * connected to this hub, an exception is thrown.
 */
synchronized void connect(MultiPipeOutputStream producer) throws IOException {
	// Previously open, but now closed?
	if (closed)
		throw new IOException("Multi-pipe closed.");

	// Producer already connected?
	if (producers.contains(producer))
		throw new IOException("Already connected");

	// Connect the producer.
	producers.addElement(producer);
}
/**
 * Disconnects the specified MultiPipeInputStream from this hub. If
 * the MultiPipeInputStream is not connected to this hub, an exception
 * is thrown. If this is the last MultiPipeInputStream connected to this
 * hub, the hub is marked closed.
 */
synchronized void disconnect(MultiPipeInputStream consumer) throws IOException {
	// Not one of our consumers?
	if (! consumers.contains(consumer))
		throw new IOException("Not connected");

	// Remove the consumer.
	consumers.removeElement(consumer);

	// No consumer left? Buffer is now closed.
	if (consumers.size() == 0)
		closed = true;
}
/**
 * Disconnects the specified MultiPipeOutputStream from this hub. If
 * the MultiPipeOutputStream is not connected to this hub, an exception
 * is thrown. If this is the last MultiPipeOutputStream connected to this
 * hub, the hub is marked closed.
 */
synchronized void disconnect(MultiPipeOutputStream producer) throws IOException {
	// Not one of our producers?
	if (! producers.contains(producer))
		throw new IOException("Not connected");

	// Remove the producer.
	producers.removeElement(producer);

	// No producer left? Buffer is now closed.
	if (producers.size() == 0)
		closed = true;
}
/**
 * Reads the next byte of data from this hub's buffer. The value byte is
 * returned as an int in the range 0 to 255. If no byte is available because
 * the end of the stream has been reached, the value -1 is returned. This
 * method blocks until input data is available, the end of the stream is
 * detected, or an exception is thrown. 
 */
synchronized int read() throws IOException {
	// Read 1 character.
	int c = readChar();

	// Just took a byte from the buffer, try to wake up a producer.
	notifyAll();

	// Return character.
	return c;
}
/**
 * Reads up to len bytes of data from this hub's buffer into an array
 * of bytes b starting at offset off. The number of bytes transferred
 * into the byte array b is returned. If no bytes are available because
 * the end of the stream has been reached, the value -1 is returned.
 * This method blocks until input data is available, the end of the
 * stream is detected, or an exception is thrown. 
 */
synchronized int read(byte b[], int off, int len)  throws IOException {

	// validate the receiving buffer and arguments.
	if (b == null) {
	    throw new NullPointerException();
	} else if (off < 0 || len < 0 || off + len > b.length) {
	    throw new ArrayIndexOutOfBoundsException();
	} else if (len == 0) {
	    return 0;
	}

	// Get the first byte, wait if none available.
	int c = readChar();

	// End of input stream?
	if (c < 0)
		return -1;

	// Got the first byte.
	b[off] = (byte) c;
	int rlen = 1;

	// Still have bytes left to get...
	while ((in >= 0) && (--len > 0)) {
		// Get next byte.
		b[off + rlen] = buffer[out++];
		rlen++;

		// At buffer's end, loop back to beginning.
		if (out >= buffer.length)
			out = 0;

		// Buffer is now empty?
		if (in == out)
			in = -1;
	}

	// Just took bytes from the buffer, try to wake up a producer.
	notifyAll();

	// Return bytes read.
	return rlen;
}
/**
 * Writes len bytes from the specified byte array b starting at offset
 * off to this hub's buffer. This method blocks until all data is written
 * to the buffer or an exception is thrown. An exception is thrown if no
 * consumers are connected to this hub.
 */
synchronized void write(byte b[], int off, int len) throws IOException {
	// While we have bytes left to write...
	while (--len >= 0) {
		// If the buffer is full, wait until a consumer uses some data.
		while (in == out) {
			// No consumers registered? Don't accept the data.
			if (consumers.size() == 0)
				throw new IOException("No consumers available.");

			// Try to wake up a consumer.
			notifyAll();

			// Ok, wait until one of the consumers uses up some data.
			try {
				wait();
			}
			catch (InterruptedException ex) {
				throw new java.io.InterruptedIOException();
			}
		}

		// If the buffer is empty, start at the beginning.
		if (in < 0) {
			in = 0;
			out = 0;
		}

		// Store the byte in the buffer.
		buffer[in++] = b[off++];

		// At the buffer's end, rap back to the beginning.
		if (in >= buffer.length)
			in = 0;
	}

	// Just put bytes in the buffer, try to wake up a consumer.
	notifyAll();
}
/**
 * Writes the specified byte to this hub's buffer. This method blocks
 * until the byte is written to the buffer or an exception is thrown.
 * An exception is thrown if no consumers are connected to this hub.
 */
synchronized void write(int b)  throws IOException {
	// If the buffer is full, wait until a consumer uses some data.
	while (in == out) {
		// No consumers registered? Don't accept the data.
		if (consumers.size() == 0)
			throw new IOException("No consumers available.");

		// Try to wake up a consumer.
		notifyAll();

		// Ok, wait until one of the consumers uses up some data.
		try {
			wait();
		}
		catch (InterruptedException ex) {
			throw new java.io.InterruptedIOException();
		}
	}

	// If the buffer is empty, start at the beginning.
	if (in < 0) {
		in = 0;
		out = 0;
	}

	// Store the byte in the buffer.
	buffer[in++] = (byte)(b & 0xFF);

	// At the buffer's end, rap back to the beginning.
	if (in >= buffer.length)
		in = 0;

	// Just put a byte in the buffer, try to wake up a consumer.
	notifyAll();
}
/**
 * Reads the next byte of data from this hub's buffer. The value byte is
 * returned as an int in the range 0 to 255. If no byte is available because
 * the end of the stream has been reached, the value -1 is returned. This
 * method blocks until input data is available, the end of the stream is
 * detected, or an exception is thrown. 
 */
private synchronized int readChar() throws IOException {
	// Wait for a producer to put something in the buffer.
	while (in < 0) {
		// Consumers or producers have dropped off?
		if (closed)
			return -1;

		// Not closed, no producers? No need to wait.
		if (producers.size() == 0)
			throw new IOException("No producers.");

		// Try to wake up a waiting producer...
		notifyAll();

		// Ok, now we wait until we are told to check the buffer.
		try {
			wait();
		}
		catch (InterruptedException ex) {
			throw new java.io.InterruptedIOException();
		}
	}

	// Get 1 character out of the buffer.
	int ret = buffer[out++] & 0xFF;

	// At buffer's end? Move to beginning.
	if (out >= buffer.length) {
		out = 0;
	}
	// At the input position? Nothing left in buffer.
	if (in == out) {
		in = -1;		
	}

	// Return character.
	return ret;
}
}
