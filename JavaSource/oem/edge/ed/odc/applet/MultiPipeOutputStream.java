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
 * A MultiPipeOutputStream is the sending end of a communications 
 * cluster. Multiple threads can communicate by having producers send data 
 * through a MultiPipeOutputStream and having consumers read the 
 * data through a MultiPipeInputStream. A MultiPipeHub is the
 * conduit through which data is exchanged.
 */

import java.io.*;

public class MultiPipeOutputStream extends OutputStream {
	MultiPipeHub hub = null;
/**
 * Creates a MultiPipeOutputStream (producer) that is unconnected. It
 * must be connected to a consumer before being used. This is done by
 * constructing a MultiPipeInputStream and providing this as an
 * argument or by calling connect with a MultiPipeInputStream as an
 * argument.
 *
 * Additional producers and consumers may be connected as desired.
 */
public MultiPipeOutputStream() {
}
/**
 * Creates a MultiPipeOutputStream (producer) that is connected to
 * the specified MultiPipeInputStream (consumer).
 *
 * Additional producers and consumers may be connected as desired.
 */
public MultiPipeOutputStream(MultiPipeInputStream consumer)  throws IOException {
	connect(consumer);
}
/**
 * Creates a MultiPipeOutputStream (producer) that is connected to
 * the specified MultiPipeOutputStream (also a producer). A
 * MultiPipeInputStream must be connected to either of these producers
 * before the MultiPipeOutputStream can be used.
 *
 * Additional producers and consumers may be connected as desired.
 */
public MultiPipeOutputStream(MultiPipeOutputStream producer)  throws IOException {
	connect(producer);
}
/**
 * Disconnects this MultiPipeOutputStream from the MultiPipeHub. If
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
 * Connects this MultiPipeOutputStream to the specified
 * MultiPipeInputStream. The MultiPipeOutputStream must not be already
 * connected. When the connect completes, the stream may be used.
 */
public void connect(MultiPipeInputStream consumer) throws IOException {
	// Are we connected already?
	if (hub != null)
		throw new IOException("Already connected.");

	// Connect to the consumer's hub.
	consumer.join(this);
}
/**
 * Connects this MultiPipeOutputStream to the specified
 * MultiPipeOutputStream. This stream must not be already connected. A
 * MultiPipeInputStream must connect before this stream may be used.
 */
public void connect(MultiPipeOutputStream producer) throws IOException {
	// Are we connected already?
	if (hub != null)
		throw new IOException("Already connected.");

	// Connect to the producer's hub.
	producer.join(this);
}
/**
 * Connects this MultiPipeOutputStream to the specified MultiPipeHub.
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
 * Writes len bytes from the specified byte array b starting at offset
 * off to this MultiPipeOutputStream. 
 */
public void write(byte b[], int off, int len) throws IOException {
	// Can't write if we are not connected to a hub.
	if (hub == null)
		throw new IOException("Not connected.");

	// Write to the hub.
	hub.write(b, off, len);
}
/**
 * Writes the specified byte to this MultiPipeOutputStream.
 */
public void write(int b)  throws IOException {
	// Can't write if we are not connected to a hub.
	if (hub == null)
		throw new IOException("Not connected.");

	// Write to the hub.
	hub.write(b);
}
}
