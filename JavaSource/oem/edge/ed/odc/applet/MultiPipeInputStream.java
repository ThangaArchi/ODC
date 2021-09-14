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
 * A MultiPipeInputStream is the receiving end of a communications 
 * cluster. Multiple threads can communicate by having producers send data 
 * through a MultiPipeOutputStream and having consumers read the 
 * data through a MultiPipeInputStream. A MultiPipeHub is the
 * conduit through which data is exchanged.
 */
import java.io.*;

public class MultiPipeInputStream extends InputStream {
	MultiPipeHub hub = null;
/**
 * Creates a MultiPipeInputStream (consumer) that is unconnected. It
 * must be connected to a producer before being used. This is done by
 * constructing a MultiPipeOutputStream and providing this as an
 * argument or by calling connect with a MultiPipeOutputStream as an
 * argument.
 *
 * Additional producers and consumers may be connected as desired.
 */
public MultiPipeInputStream() {
}
/**
 * Creates a MultiPipeInputStream (consumer) that is connected to
 * the specified MultiPipeInputStream (also a consumer). A
 * MultiPipeOutputStream must be connected to either of these consumers
 * before this MultiPipeInputStream can be used.
 *
 * Additional producers and consumers may be connected as desired.
 */
public MultiPipeInputStream(MultiPipeInputStream consumer) throws IOException {
	connect(consumer);
}
/**
 * Creates a MultiPipeInputStream (consumer) that is connected to
 * the specified MultiPipeOutputStream (producer).
 *
 * Additional producers and consumers may be connected as desired.
 */
public MultiPipeInputStream(MultiPipeOutputStream producer) throws IOException {
	connect(producer);
}
/**
 * Returns the number of bytes that can be read from this
 * MultiPipeInputStream without blocking.
 */
public int available() throws IOException {
	// Can't read if we are not connected to a hub.
	if (hub == null)
		throw new IOException("Not connected.");

	// available from the hub.
	return hub.available();
}
/**
 * Disconnects this MultiPipeInputStream from the MultiPipeHub. If
 * no other producers or consumers are connected to the MultiPipeHub,
 * then any system resources associated with this stream are released.
 */
public void close()  throws IOException {
	// Not connected, can't close.
	if (hub == null)
		throw new IOException("Not connected.");

	// Disconnect ourselves from the hub.
	hub.disconnect(this);
	hub = null;
}
/**
 * Connects this MultiPipeInputStream to the specified
 * MultiPipeInputStream. This stream must not be already connected. A
 * MultiPipeOutputStream must connect before this stream may be used.
 */
public void connect(MultiPipeInputStream consumer) throws IOException {
	// Are we connected already?
	if (hub != null)
		throw new IOException("Already connected.");

	// Connect to the consumer's hub.
	consumer.join(this);
}
/**
 * Connects this MultiPipeInputStream to the specified
 * MultiPipeOutputStream. This stream must not be already connected. When
 * the connect completes, the stream may be used.
 */
public void connect(MultiPipeOutputStream producer) throws IOException {
	// Are we connected already?
	if (hub != null)
		throw new IOException("Already connected.");

	// Connect to the producer's hub.
	producer.join(this);
}
/**
 * Connects this MultiPipeInputStream to the specified hub.
 */
void join(MultiPipeHub hub) throws IOException {
	// Check that we aren't connected to a hub?
	if (this.hub != null)
		throw new IOException("Already connected to a hub!");

	// Connect to the hub provided.
	this.hub = hub;
	hub.connect(this);
}
/**
 * Connects the specified MultiPipeInputStream to our hub. A new
 * MultiPipeHub is created, if necessary.
 */
void join(MultiPipeInputStream consumer) throws IOException {
	// Do we need a new MultiPipeHub?
	if (hub == null) {
		hub = new MultiPipeHub();
		hub.connect(this);
	}

	// Connect the consumer to our hub.
	consumer.join(hub);
}
/**
 * Connects the specified MultiPipeOutputStream to our hub. A new
 * MultiPipeHub is created, if necessary.
 */
void join(MultiPipeOutputStream producer) throws IOException {
	// Do we need a new MultiPipeHub?
	if (hub == null) {
		hub = new MultiPipeHub();
		hub.connect(this);
	}

	// Connect the producer to our hub.
	producer.join(hub);
}
/**
 * Reads the next byte of data from this MultiPipeInputStream. The 
 * value byte is returned as an int in the range 0 to 255. If no byte
 * is available because the end of the stream has been reached, the value 
 * -1 is returned. This method blocks until input data is available, the
 * end of the stream is detected, or an exception is thrown. 
 */
public int read() throws IOException {
	// Can't read if we are not connected to a hub.
	if (hub == null)
		throw new IOException("Not connected.");

	// Read from the hub.
	return hub.read();
}
/**
 * Reads up to len bytes of data from this MultiPipeInputStream
 * into an array of bytes b starting at offset off. The number of bytes
 * transferred into the byte array b is returned. If no bytes are
 * available because the end of the stream has been reached, the value 
 * -1 is returned. This method blocks until input data is available, the
 * end of the stream is detected, or an exception is thrown. 
 */
public int read(byte b[], int off, int len) throws IOException {
	// Can't read if we are not connected to a hub.
	if (hub == null)
		throw new IOException("Not connected.");

	// Read from the hub.
	return hub.read(b, off, len);
}
}
