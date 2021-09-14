package oem.edge.ed.odc.xchannel;

/**
 * Insert the type's description here.
 * Creation date: (7/21/2003 7:52:22 PM)
 * @author: Administrator
 */
public class Completion implements Runnable {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private boolean completed = false;
	private int percentCompleted = 0;
	private java.lang.String status ;
	private java.lang.String fieldStat = "Idle";
/**
 * Completion constructor comment.
 */
public Completion() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 8:17:03 PM)
 * @param listener java.beans.PropertyChangeListener
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 8:16:00 PM)
 * @param propertyName java.lang.String
 * @param oldValue java.lang.Object
 * @param newV java.lang.Object
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Insert the method's description here.
 * Creation date: (7/22/2003 11:06:01 AM)
 * @return int
 */
public int getPercentCompleted() {
	return percentCompleted;
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 8:14:16 PM)
 * @return java.beans.PropertyChangeSupport
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
              propertyChange = new java.beans.PropertyChangeSupport(this);
       };
       return propertyChange;
}
/**
 * Gets the stat property (java.lang.String) value.
 * @return The stat property value.
 */
public java.lang.String getStat() {
	//System.out.println("the value of fieldstat is: " + fieldStat);
	return fieldStat;
}
/**
 * Insert the method's description here.
 * Creation date: (7/22/2003 9:38:32 PM)
 * @return java.lang.String
 */
public java.lang.String getStatus() {
	System.out.println("the status is: " + status); 
	return status;
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 8:15:18 PM)
 * @return boolean
 * @param propertyName java.lang.String
 */
public synchronized boolean hasListeners(String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 8:19:43 PM)
 * @return boolean
 */
public boolean isCompleted() {
	return completed;
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 8:42:47 PM)
 */
public void processProgress() {
	Thread t = new Thread(this);
	t.start();
	//System.out.println(" I am firing an event here");
	//firePropertyChange("percentCompleted",new Integer(101),new Integer(percentCompleted));
	
	}
/**
 * Insert the method's description here.
 * Creation date: (7/22/2003 12:16:23 PM)
 * @param listener java.beans.PropertyChangeListener
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	 getPropertyChange().removePropertyChangeListener(listener);
	 }
	/**
	 * When an object implementing interface <code>Runnable</code> is used 
	 * to create a thread, starting the thread causes the object's 
	 * <code>run</code> method to be called in that separately executing 
	 * thread. 
	 * <p>
	 * The general contract of the method <code>run</code> is that it may 
	 * take any action whatsoever.
	 *
	 * @see     java.lang.Thread#run()
	 */
public void run() {
	int oldpercentCompleted = 0;
	while(isCompleted() == false){
		/***
		*     Subu 07/23/03 Observation:- Strings are immutable(cannot be changed). So firePropertyChange was not able to fire the change in the String
		*                        		  property. In the case below -> "fieldStat". So I need to explicitely change the value of fieldStat
		*		  						  as shown in the below code and then fire the firePropertyChange.
		*						 		  This behavious is not the same with integers as described with the following code:-
		*								  firePropertyChange("percentCompleted",new Integer(101),new Integer(i));
		***/
			if(percentCompleted == 0)   {fieldStat = "Initializing..."  ;	firePropertyChange("stat", "yahoo!", "some value");}
			if(percentCompleted == 15)  {fieldStat = "About to connect..." ;	firePropertyChange("stat", "yahoo!", "some value");}
			if(percentCompleted == 45)  {fieldStat = "Establishing connection...";	firePropertyChange("stat", "yahoo!", "some value");}
			if(percentCompleted == 60)  {fieldStat = "Connected"   ;	firePropertyChange("stat", "yahoo!", "some value");}
			if(percentCompleted == 75)  {fieldStat = "Retrieving Information"  ;	firePropertyChange("stat", "yahoo!", "some value");}
			if(percentCompleted == 100) {fieldStat = "Done";	firePropertyChange("stat", "yahoo!", "some value");}

			
			if(	oldpercentCompleted != percentCompleted){
				for(int i = oldpercentCompleted; i < percentCompleted; i+=5){
	
					firePropertyChange("percentCompleted",new Integer(101),new Integer(i));
				}
				oldpercentCompleted = percentCompleted;
			}
	if(percentCompleted == 100)
				setCompleted(true);
			try{ Thread.sleep(1000);}
			catch(InterruptedException ie){}



	}
	
	}
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 8:19:43 PM)
 * @param newCompleted boolean
 */
public void setCompleted(boolean newCompleted) {
	completed = newCompleted;
}
/**
 * Insert the method's description here.
 * Creation date: (7/22/2003 11:06:01 AM)
 * @param newPercentCompleted int
 */
public void setPercentCompleted(int newPercentCompleted) {
	percentCompleted = newPercentCompleted;
	
}
/**
 * Insert the method's description here.
 * Creation date: (7/22/2003 9:38:32 PM)
 * @param newStatus java.lang.String
 */
public void setStatus(java.lang.String newStatus) {
	status = newStatus;
}
}
