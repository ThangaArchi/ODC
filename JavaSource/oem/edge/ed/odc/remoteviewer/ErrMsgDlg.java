package oem.edge.ed.odc.remoteviewer;
import javax.swing.JOptionPane;

class ErrMsgDlg {
   public static void main(String[] args) {
       JOptionPane.showConfirmDialog(null, args[0], "Remote Viewer Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
       System.exit(0);
   }
}
