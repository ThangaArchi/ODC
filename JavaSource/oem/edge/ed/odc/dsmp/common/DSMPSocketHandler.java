package oem.edge.ed.odc.dsmp.common;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class DSMPSocketHandler extends DSMPBaseHandler {

   private Socket socket = null;
   public DSMPSocketHandler(String host, int port, DSMPDispatchBase disp) 
                                         throws UnknownHostException,
                                                IOException {
      super(disp);
      Socket socket = new Socket(host, port);
      setInputOutput(socket.getInputStream(), socket.getOutputStream());
      this.socket = socket;
   }
   public DSMPSocketHandler(Socket socket, DSMPDispatchBase disp) 
                                         throws UnknownHostException,
                                                IOException {
      super(disp);
      setInputOutput(socket.getInputStream(), socket.getOutputStream());
      this.socket = socket;
   }
}
