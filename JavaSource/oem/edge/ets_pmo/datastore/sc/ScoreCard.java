package oem.edge.ets_pmo.datastore.sc;

import java.util.Vector;
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
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ScoreCard {
private static String CLASS_VERSION = "4.5.1";	
private String ScoreCardId;
private String element_name;
private String rating_score;
private Vector vScoreCards;

public ScoreCard(){
	ScoreCardId		= null;
	element_name	= null;
	rating_score	= null;
	vScoreCards 	= null;
}
/**
 * Returns the element_name.
 * @return String
 */
public String getElement_name() {
	return element_name;
}

/**
 * Returns the rating_score.
 * @return String
 */
public String getRating_score() {
	return rating_score;
}

/**
 * Returns the scoreCardId.
 * @return String
 */
public String getScoreCardId() {
	return ScoreCardId;
}

/**
 * Sets the element_name.
 * @param element_name The element_name to set
 */
public void setElement_name(String element_name) {
	this.element_name = element_name;
}

/**
 * Sets the rating_score.
 * @param rating_score The rating_score to set
 */
public void setRating_score(String rating_score) {
	this.rating_score = rating_score;
}

/**
 * Sets the scoreCardId.
 * @param scoreCardId The scoreCardId to set
 */
public void setScoreCardId(String scoreCardId) {
	ScoreCardId = scoreCardId;
}

public void populate_ScoreCards(ScoreCard sc){
	if(this.vScoreCards == null){
			vScoreCards = new Vector();	
		}
	vScoreCards.add(sc);	
}

public int retrieveScoreCardPopulation(){
	if(vScoreCards == null)
		return -1;
	return vScoreCards.size();
}

public ScoreCard retrieveScoreCard(int index) throws IndexOutOfBoundsException{
	ScoreCard sc = null;
	if(this.vScoreCards != null &&
		!this.vScoreCards.isEmpty()){
			if(index >= vScoreCards.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vScoreCards");
			}
	sc = (ScoreCard)vScoreCards.get(index);
	}
	return sc;
		
}
public String toString(){
	String str = "[ScoreCardID : " + ScoreCardId + ", Element_Name : " + element_name + ", Rating_Score : "  + rating_score + 
				 ", HasChildrenScoreCard vector size" + retrieveScoreCardPopulation() + " ] ";
	return str;
}

/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}

}
