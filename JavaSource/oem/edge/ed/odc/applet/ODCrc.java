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

/*
** Module : ODCrc
**
** Purpose: Provide 32bit CRC capability
**
** Author : Joe Crichton - Algorithm initially obtained from IBMPC CRC FORUM
**                         written in Turbo Pascal, circa 1990.
**
** ChangeLog: 
**
**          05-05-01 Initial port to Java
*/
public class ODCrc {
 /*
 ** Code to generate a 32 bit CRC table to use with generateCRC based on
 ** passed in polynomial value.
 */
   int crcTable[] = null;
   int crc        = 0xffffffff;
   public ODCrc() {}
   public void generateCRC(byte arr[]) {
      generateCRC(arr, 0, arr.length);
   }
  /*
  ** generateCRC:
  **
  **    Routine to generate a 32 bit CRC. If the crcTable has not been primed
  **    via the generateCRCTable method, then that will be done using the
  **    polynomial 0xEDB88320. 
  **
  **    initializeNewCRC should be called to prime the CRC. This routine can
  **    then be called many times, and the getCRC routine is then used to 
  **    obtain the CRC value. getCRC can be used at anytime (any many times)
  **    during the CRC generation process to obtain a 'checkpoint' CRC.
  */
   public void generateCRC(byte buf[], int ofs, int len) {
      int i;
      
      if (crcTable == null) { 
         generateCRCTable(0xEDB88320);
      }
      
      len += ofs;
      for(i=ofs; i < len; i++) {
         int idx = (crc & 0x000000FF) ^ buf[i];
         if (idx < 0) idx += 256;
         crc = ((crc >> 8) & 0xffffff) ^ (crcTable [idx]);
      }
   }
   public void generateCRC(String s) {
      generateCRC(s.getBytes(), 0, s.length());
   }
   public void generateCRCTable(int polynomial) {
      
      int crcCode;
      int dataByte;
      int index, bitNo;
      int carryBit, dataBit;
      
      if (crcTable == null) crcTable = new int[256];
      
     /*
     ** For each possible data byte
     */
      for (index = 0; index < 256; index++) {
         
         crcCode = 0;
         dataByte = index;
         
        /*
        ** For each data bit
        */
         for (bitNo = 1; bitNo < 9; bitNo++) {
            carryBit = (crcCode & 0x01);
            dataBit = (dataByte & 0x01);
            crcCode >>= 1;
            if ((carryBit ^ dataBit) != 0) {
               crcCode = crcCode ^ polynomial;
            }
            dataByte = (dataByte>>1) & 0x7f;
         }
         crcTable [index] = crcCode;
      }
   }
   public int getCRC() {
      return crc ^ 0xFFFFFFFF;   /* post-conditioning of CRC value */
   }
   public void resetCRC() {
      crc = 0xFFFFFFFF;   /* pre-conditioning of CRC value */
   }
   public void setDefaultTable() {
      generateCRCTable(0xEDB88320);
   }
}
