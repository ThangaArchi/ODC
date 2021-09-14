package oem.edge.ed.odc.tunnel.applet;

import java.awt.event.*;

public class TunnelEvent extends ActionEvent {
	public final static int TUNNEL_EVENT = 1010;
	public Object parm1;
	public Object parm2;
	public Object parm3;
	public Object parm4;
public TunnelEvent(Object source, int id, String command) {
	this(source, id, command, null, null, null, null);
}
public TunnelEvent(Object source, int id, String command, Object parm1) {
	this(source, id, command, parm1, null, null, null);
}
public TunnelEvent(Object source, int id, String command, Object parm1, Object parm2) {
	this(source, id, command, parm1, parm2, null, null);
}
public TunnelEvent(Object source, int id, String command, Object parm1, Object parm2, Object parm3) {
	this(source, id, command, parm1, parm2, parm3, null);
}
public TunnelEvent(Object source, int id, String command, Object parm1, Object parm2, Object parm3, Object Parm4) {
	super(source, id, command, 0);
	this.parm1 = parm1;
	this.parm2 = parm2;
	this.parm3 = parm3;
	this.parm4 = parm4;
}
}
