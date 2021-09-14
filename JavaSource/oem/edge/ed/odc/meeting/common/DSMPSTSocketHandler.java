package oem.edge.ed.odc.meeting.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class DSMPSTSocketHandler extends DSMPHandler {

   private Socket socket = null;
   public DSMPSTSocketHandler(String host, int port, DSMPDispatchBase disp) 
                                         throws UnknownHostException,
                                                IOException {
      super(disp);
      Socket socket = new Socket(host, port);
      setInputOutput(socket.getInputStream(), socket.getOutputStream());
      this.socket = socket;
   }
   public DSMPSTSocketHandler(Socket socket, DSMPDispatchBase disp) 
                                         throws UnknownHostException,
                                                IOException {
      super(disp);
      setInputOutput(socket.getInputStream(), socket.getOutputStream());
      this.socket = socket;
   }
}
