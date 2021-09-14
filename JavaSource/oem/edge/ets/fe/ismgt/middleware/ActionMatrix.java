/*
 * Created on May 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.ismgt.middleware;


/**
 * @author jetendra
 * @description This class captures all the possible actions that are possible on an issue
 * and will be used to store their values as boolean
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ActionMatrix {
	public static final String VERSION = "1.0";
	
	public boolean Create = false;
	public boolean Modify = false;
	public boolean Resolve = false;
	public boolean Reject = false;
	public boolean Close = false;
	public boolean Comment = false;
	public boolean Withdraw = false;
	public boolean View = false;
	public boolean Subscribe = false;
	public boolean Changeowner = false;
	
	public ActionMatrix(){
	}
	
	// Constructor that initializes the switches
	public ActionMatrix(boolean create,
						boolean modify,
 						boolean resolve,
 						boolean reject ,
						boolean close ,
 						boolean comment ,
 						boolean withdraw ,
	 					boolean view ,
						boolean subscribe,
						boolean changeowner ){
							
		Create = create;
		Modify = modify;
		Resolve = resolve;
		Reject = reject;
		Close = close;
		Comment = comment;
		Withdraw = withdraw;
		View = view;
		Subscribe = subscribe;
		Changeowner=changeowner;
							
	}
	// Used to add one action matrix to another and get a superset of actions
	public ActionMatrix Add(ActionMatrix matrix){
	
		ActionMatrix combo = new ActionMatrix();
		
		combo.Create= Create || matrix.Create;
		combo.Modify= Modify || matrix.Modify;
		combo.Resolve= Resolve || matrix.Resolve;
		combo.Reject= Reject || matrix.Reject;
		combo.Close= Close || matrix.Close;
		combo.Comment=Comment || matrix.Comment;
		combo.Withdraw=Withdraw || matrix.Withdraw;
		combo.View= View || matrix.View;
		combo.Subscribe= Subscribe || matrix.Subscribe;
		combo.Changeowner= Changeowner || matrix.Changeowner;
		
		return combo;
	}
//	Used to find the common actions between two action matrices 
	public ActionMatrix Filter(ActionMatrix matrix){
	
			ActionMatrix combo = new ActionMatrix();
		
			combo.Create= Create && matrix.Create;
			combo.Modify= Modify && matrix.Modify;
			combo.Resolve= Resolve && matrix.Resolve;
			combo.Reject= Reject && matrix.Reject;
			combo.Close= Close && matrix.Close;
			combo.Comment=Comment && matrix.Comment;
			combo.Withdraw=Withdraw && matrix.Withdraw;
			combo.View= View && matrix.View;
			combo.Subscribe= Subscribe && matrix.Subscribe;
			combo.Changeowner= Changeowner && matrix.Changeowner;
			return combo;
		}
	}
	

