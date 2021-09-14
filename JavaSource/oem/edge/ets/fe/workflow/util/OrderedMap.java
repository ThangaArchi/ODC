
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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
 * Created on Feb 6, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.workflow.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OrderedMap implements Serializable, Cloneable{

	private ArrayList keys      = null;
	private ArrayList values    = null;

	/**
	 * Constructor for Ordered HashMap
	 * which initializes the key array and
	 * value array.
	 */
	public OrderedMap(){
		keys    = new ArrayList();
		values  = new ArrayList();
	}

	/**
	 * Associates the specified value with the specified key
	 *
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 */
	public void put(Object key, Object value){
		if(key == null) throw new NullPointerException("Key cannot be null");
		int index = keys.indexOf(key);
		if(index != -1){
			keys.set(index, key);
			values.set(index, value);
		}else{
			keys.add(key);
			values.add(value);
		}
	}
	/**
		 * Returns the value to which the specified key is mapped in this identity
		 *
		 * @param   key the key whose associated value is to be returned.
		 * @return  the value to which this map maps the specified key.
		 */
		public Object get(Object key){
			int index = keys.indexOf(key);
			Object value = null;
			if(index != -1){
				value = values.get(index);
			}
			return value;
		}

		/**
		 * Returns the value to which the specified key is mapped in this identity
		 *
		 * @param   index the index at which associated value is to be returned.
		 * @return  the value to which this map maps the specified key.
		 */
		public Object get(int index){
			Object value = null;
			if(index > -1 && index < values.size()){
				value = values.get(index);
			}
			return value;
		}

		/**
		 * Returns the key to which the specified value is mapped in this identity
		 *
		 * @param   value the value whose associated value is to be returned.
		 * @return  the key to which this map maps the specified key.
		 */
		public Object getKey(Object value){
			int index = values.indexOf(value);
			Object key = null;
			if(index != -1){
				key = keys.get(index);
			}
			return key;
		}

		/**
		 * Returns the key to which the specified value is mapped in this identity
		 *
		 * @param   value the value whose associated value is to be returned.
		 * @return  the key to which this map maps the specified key.
		 */
		public ArrayList getKeys(Object value){
			ArrayList keys1 = null;
			if(values != null && value != null){
				for(int index=0; index < values.size(); index++){
					if(values.get(index).equals(value)){
						if(keys1 == null) keys1 = new ArrayList();
						keys1.add(keys.get(index));
					}
				}
			}
			return keys1; 
		}

		/**
		 * Returns a set view of the keys.
		 *
		 * @return  ArrayList of keys
		 */
		public ArrayList keys(){
			return (ArrayList)keys.clone();
		}

		/**
		 * Returns a set view of the values.
		 *
		 * @return  ArrayList of values
		 */
		public ArrayList values(){
			return (ArrayList)values.clone();
		}

		/**
		 * Check whether the key is available or not
		 *
		 * @param   key the key availability to be checked
		 * @return  boolean whether the key is available or not
		 */
		public boolean containsKey(Object key){
			return keys.contains(key);
		}

		/**
		 * Check whether the value is available or not
		 *
		 * @param   value the value availability to be checked
		 * @return  boolean whether the value is available or not
		 */
		public boolean containsValue(Object value){
			return values.contains(value);
		}

		/**
		 * Returns the size of the Hash
		 *
		 * @return  int the size of the Hash
		 */
		public int size(){
			return keys.size();
		}

		/**
		 * Removes the element at the index
		 *
		 * @param   index int element to be removed at the index
		 */
		public void remove(int index){
			keys.remove(index);
			values.remove(index);
		}

		/**
		 * Removes the key
		 *
		 * @param   key the key to be removed
		 */
		public void remove(Object key){
			 remove(keys.indexOf(key));
		}

		/**
		 * Clear all the elements in hash
		 */
		public void clear(){
			keys.clear();
			values.clear();
		}
    
		/**
		 * Rename the key
		 *
		 * @param   fromKey     Object
		 * @param   toKey       Object
		 */
		public void renameKey(Object fromKey, Object toKey){
			if(keys.contains(fromKey)){
				keys.set(keys.indexOf(fromKey), toKey);
			}
		}

		/**
		 * Returns the keys as an Object array
		 *
		 * @return Object[]
		 */
		public Object[] toArray(){
			return keys.toArray();
		}

		/**
		 * Returns the keys as an Object array and typecast to
		 * the type of array passed as parameter.
		 *
		 * @param	type	Object[] The type to which the key 
		 *					objects need to converted
		 * @return Object[]
		 */
		public Object[] toArray(Object[] type){
			return keys.toArray(type);
		}

		public String toString(){
			return "Key Array:" + keys + ",Values:" + values;
		}
	}
