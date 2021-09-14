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

package oem.edge.ets.fe.aic.metrics;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import oem.edge.ets.fe.Defines;

//import com.ibm.as400.webaccess.common.*;

public class AICMetricsCCObj extends AICMetricsObj {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";

	protected String clientDesignation;
	protected float rating;
	protected int lowrating;
	protected int somerating;
	protected int metrating;
	protected int exceedrating;
	protected long ratingDate;
	protected String state;
	protected String expCat;
	protected boolean changeStr;
	protected static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	//protected float avgrating;

	protected double overallCurrRat;
	protected double selfCurrRat;
	protected double setCurrRat;
	protected double surveyCurrRat;
	protected double overallPrevRat;
	protected double selfPrevRat;
	protected double setPrevRat;
	protected double surveyPrevRat;
	protected double overall2Rat;
	protected double self2Rat;
	protected double set2Rat;
	protected double survey2Rat;
	protected double overall3Rat;
	protected double self3Rat;
	protected double set3Rat;
	protected double survey3Rat;
	protected double overall4Rat;
	protected double self4Rat;
	protected double set4Rat;
	protected double survey4Rat;
	protected double overall5Rat;
	protected double self5Rat;
	protected double set5Rat;
	protected double survey5Rat;
	protected double overall6Rat;
	protected double self6Rat;
	protected double set6Rat;
	protected double survey6Rat;
	protected double overall7Rat;
	protected double self7Rat;
	protected double set7Rat;
	protected double survey7Rat;
	protected double overall8Rat;
	protected double self8Rat;
	protected double set8Rat;
	protected double survey8Rat;
	protected String rowName;
	protected String rowDesc;
	protected Vector ratingsToGet;
	protected DecimalFormat decf;

	public AICMetricsCCObj() {
		super();
		decf = new DecimalFormat("###,###.##");

		this.clientDesignation = "";
		rating = -1;
		state = "";
		expCat = "";
		changeStr = false;
		lowrating = 0;
		somerating = 0;
		metrating = 0;
		exceedrating = 0;
		//avgrating = 0;

		rowName = "";
		rowDesc = "";
		overallCurrRat = -1;
		selfCurrRat = -1;
		setCurrRat = -1;
		overallPrevRat = -1;
		selfPrevRat = -1;
		setPrevRat = -1;
		overall2Rat = -1;
		self2Rat = -1;
		set2Rat = -1;
		overall3Rat = -1;
		self3Rat = -1;
		set3Rat = -1;
		overall4Rat = -1;
		self4Rat = -1;
		set4Rat = -1;
		overall5Rat = -1;
		self5Rat = -1;
		set5Rat = -1;
		overall6Rat = -1;
		self6Rat = -1;
		set6Rat = -1;
		overall7Rat = -1;
		self7Rat = -1;
		set7Rat = -1;
		overall8Rat = -1;
		self8Rat = -1;
		set8Rat = -1;
		ratingsToGet = new Vector();

		ratingsToGet.addElement("CurrRatStr");
		ratingsToGet.addElement("PrevRatStr");
		ratingsToGet.addElement("2RatStr");
		ratingsToGet.addElement("3RatStr");
		ratingsToGet.addElement("4RatStr");
		ratingsToGet.addElement("5RatStr");
		ratingsToGet.addElement("6RatStr");
		ratingsToGet.addElement("7RatStr");
		ratingsToGet.addElement("8RatStr");
	}

	public AICMetricsCCObj(AICMetricsCCObj obj) {
		super();
		decf = obj.decf;
		clientDesignation = obj.getClientDesignation();
		rating = obj.getRating();
		state = obj.getState();
		expCat = obj.getExpCat();
		changeStr = obj.changeStr;
		lowrating = obj.getLowRating();
		somerating = obj.getSomeRating();
		metrating = obj.getMetRating();
		exceedrating = obj.getExceedRating();
		//avgrating = 0;

		rowName = "";
		rowDesc = "";
		overallCurrRat = obj.overallCurrRat;
		selfCurrRat = obj.selfCurrRat;
		setCurrRat = obj.setCurrRat;
		overallPrevRat = obj.overallPrevRat;
		selfPrevRat = obj.selfPrevRat;
		setPrevRat = obj.setPrevRat;
		overall2Rat = obj.overall2Rat;
		self2Rat = obj.self2Rat;
		set2Rat = obj.set2Rat;
		overall3Rat = obj.overall3Rat;
		self3Rat = obj.self3Rat;
		set3Rat = obj.set3Rat;
		overall4Rat = obj.overall4Rat;
		self4Rat = obj.self4Rat;
		set4Rat = obj.set4Rat;
		overall5Rat = obj.overall5Rat;
		self5Rat = obj.self5Rat;
		set5Rat = obj.set5Rat;
		overall6Rat = obj.overall6Rat;
		self6Rat = obj.self6Rat;
		set6Rat = obj.set6Rat;
		overall7Rat = obj.overall7Rat;
		self7Rat = obj.self7Rat;
		set7Rat = obj.set7Rat;
		overall8Rat = obj.overall8Rat;
		self8Rat = obj.self8Rat;
		set8Rat = obj.set8Rat;
		ratingsToGet = obj.ratingsToGet;

	}

	public void setClientDesignation(String cd) {
		if (cd == null)
			cd = "";
		this.clientDesignation = cd;
	}
	public String getClientDesignation() {
		return clientDesignation;
	}

	public void setState(String s) {
		if (s == null)
			s = "OPEN";
		this.state = s;
	}
	public String getState() {
		return state;
	}

	public void setExpCat(String s) {
		if (s == null)
			s = "";
		this.expCat = s;
	}
	public String getExpCat() {
		return expCat;
	}

	public void setRating(float i) {
		if (state.equals("OPEN") && changeStr)
			i = -1;
		this.rating = i;
	}
	public float getRating() {
		return rating;
	}
	public String getRatingStr() {
		if (wsType.equals(Defines.METRICS_SA)) {
			NumberFormat format = new DecimalFormat("0.0");
			String ar = format.format(rating);
			return String.valueOf(ar);
		} else {
			NumberFormat format = new DecimalFormat("0");
			String ar = format.format(rating);

			if (state.equals("OPEN") && changeStr)
				return "-";
			else
				return String.valueOf(ar);
		}
	}

	public void setSurveyRating(int i,double d) {
		if (i==0) surveyCurrRat=d;
		else if (i==1) surveyPrevRat=d;
		else if (i==2) survey2Rat=d;
		else if (i==3) survey3Rat=d;
		else if (i==4) survey4Rat=d;
		else if (i==5) survey5Rat=d;
		else if (i==6) survey6Rat=d;
		else if (i==7) survey7Rat=d;
		else if (i==8) survey8Rat=d;


	}

	public long getRatingDate() {
		return ratingDate;
	}
	Timestamp getRatingDateTS() {
		return new Timestamp(ratingDate);
	}
	void setRatingDate(java.sql.Timestamp d) {
		this.ratingDate = d.getTime();
	}
	void setRatingDate(java.util.Date d) {
		this.ratingDate = d.getTime();
	}

	public String getRatingDateStr() {
		if (state.equals("OPEN") && changeStr)
			return "open";
		else
			return df.format(new java.util.Date(ratingDate));
	}

	public void setLowRating(int i) {
		this.lowrating = i;
	}
	public int getLowRating() {
		return lowrating;
	}

	public void setSomeRating(int i) {
		this.somerating = i;
	}
	public int getSomeRating() {
		return somerating;
	}

	public void setMetRating(int i) {
		this.metrating = i;
	}
	public int getMetRating() {
		return metrating;
	}

	public void setExceedRating(int i) {
		this.exceedrating = i;
	}
	public int getExceedRating() {
		return exceedrating;
	}

	//	Current Qtr
	public String getOverallCurrRatStr() {
		if (selfCurrRat > 0 && setCurrRat > 0) {
			double d = (selfCurrRat + setCurrRat) / 2.00;
			return String.valueOf(decf.format(d));
		} else if (selfCurrRat > 0) {
			double d = (selfCurrRat) / 1.00;
			return String.valueOf(decf.format(d));
		} else if (setCurrRat > 0) {
			double d = (setCurrRat) / 1.00;
			return String.valueOf(decf.format(d));
		} else {
			return "";
		}
	}
	public void setSelfCurrRating(double i) {
		this.selfCurrRat = i;
	}
	public double getSelfCurrRat() {
		return selfCurrRat;
	}
	public String getSelfCurrRatStr() {
		if (selfCurrRat < 0) {
			return "";
		}
		return String.valueOf(decf.format(selfCurrRat));
	}

	public void setSetCurrRating(double i) {
		this.setCurrRat = i;
	}
	public double getSetCurrRat() {
		return setCurrRat;
	}
	public String getSetCurrRatStr() {
		if (setCurrRat < 0) {
			return "";
		}
		return String.valueOf(decf.format(setCurrRat));
	}

	public void setSurveyCurrRating(double i) {
		this.surveyCurrRat = i;
	}
	public double getSurveyCurrRat() {
		return surveyCurrRat;
	}
	public String getSurveyCurrRatStr() {
		if (surveyCurrRat < 0) {
			return "";
		}
		return String.valueOf(decf.format(surveyCurrRat));
	}

	// Previous Qtr
	public String getOverallPrevRatStr() {
		if (selfPrevRat > 0 && setPrevRat > 0) {
			double d = (selfPrevRat + setPrevRat) / 2.00;
			return String.valueOf(decf.format(d));
		} else if (selfPrevRat > 0) {
			double d = (selfPrevRat) / 1.00;
			return String.valueOf(decf.format(d));
		} else if (setPrevRat > 0) {
			double d = (setPrevRat) / 1.00;
			return String.valueOf(decf.format(d));
		} else {
			return "";
		}
	}
	public void setSelfPrevRating(double i) {
		this.selfPrevRat = i;
	}
	public double getSelfPrevRat() {
		return selfPrevRat;
	}
	public String getSelfPrevRatStr() {
		if (selfPrevRat < 0) {
			return "";
		}
		return String.valueOf(decf.format(selfPrevRat));
	}

	public void setSetPrevRating(double i) {
		System.out.println(i);
		this.setPrevRat = i;
	}
	public double getSetPrevRat() {
		return setPrevRat;
	}
	public String getSetPrevRatStr() {
		if (setPrevRat < 0) {
			return "";
		}
		return String.valueOf(decf.format(setPrevRat));
	}

	public void setSurveyPrevRating(double i) {
		System.out.println(i);
		this.surveyPrevRat = i;
	}
	public double getSurveyPrevRat() {
		return surveyPrevRat;
	}
	public String getSurveyPrevRatStr() {
		if (surveyPrevRat < 0) {
			return "";
		}
		return String.valueOf(decf.format(surveyPrevRat));
	}

	// 2 Qtrs ago
	public String getOverall2RatStr() {
		if (self2Rat > 0 && set2Rat > 0) {
			double d = (self2Rat + set2Rat) / 2.00;
			return String.valueOf(decf.format(d));
		} else if (self2Rat > 0) {
			double d = (self2Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else if (set2Rat > 0) {
			double d = (set2Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else {
			return "";
		}
	}
	public void setSelf2Rating(double i) {
		this.self2Rat = i;
	}
	public double getSelf2Rat() {
		return self2Rat;
	}
	public String getSelf2RatStr() {
		if (self2Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(self2Rat));
	}

	public void setSet2Rating(double i) {
		this.set2Rat = i;
	}
	public double getSet2Rat() {
		return set2Rat;
	}
	public String getSet2RatStr() {
		if (set2Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(set2Rat));
	}
	public void setSurvey2Rating(double i) {
		this.survey2Rat = i;
	}
	public double getSurvey2Rat() {
		return survey2Rat;
	}
	public String getSurvey2RatStr() {
		if (survey2Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(survey2Rat));
	}

	//	3 Qtrs ago
	public String getOverall3RatStr() {
		if (self3Rat > 0 && set3Rat > 0) {
			double d = (self3Rat + set3Rat) / 2.00;
			return String.valueOf(decf.format(d));
		} else if (self3Rat > 0) {
			double d = (self3Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else if (set3Rat > 0) {
			double d = (set3Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else {
			return "";
		}
	}
	public void setSelf3Rating(double i) {
		this.self3Rat = i;
	}
	public double getSelf3Rat() {
		return self3Rat;
	}
	public String getSelf3RatStr() {
		if (self3Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(self3Rat));
	}

	public void setSet3Rating(double i) {
		this.set3Rat = i;
	}
	public double getSet3Rat() {
		return set3Rat;
	}
	public String getSet3RatStr() {
		if (set3Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(set3Rat));
	}
	public void setSurvey3Rating(double i) {
		this.survey3Rat = i;
	}
	public double getSurvey3Rat() {
		return survey3Rat;
	}
	public String getSurvey3RatStr() {
		if (survey3Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(survey3Rat));
	}

	//	4 Qtrs ago
	public String getOverall4RatStr() {
		if (self4Rat > 0 && set4Rat > 0) {
			double d = (self4Rat + set4Rat) / 2.00;
			return String.valueOf(decf.format(d));
		} else if (self4Rat > 0) {
			double d = (self4Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else if (set4Rat > 0) {
			double d = (set4Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else {
			return "";
		}
	}

	public void setSelf4Rating(double i) {
		this.self4Rat = i;
	}
	public double getSelf4Rat() {
		return self4Rat;
	}
	public String getSelf4RatStr() {
		if (self4Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(self4Rat));
	}

	public void setSet4Rating(double i) {
		this.set4Rat = i;
	}
	public double getSet4Rat() {
		return set4Rat;
	}
	public String getSet4RatStr() {
		if (set4Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(set4Rat));
	}

	public void setSurvey4Rating(double i) {
		this.survey4Rat = i;
	}
	public double getSurvey4Rat() {
		return survey4Rat;
	}
	public String getSurvey4RatStr() {
		if (survey4Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(survey4Rat));
	}
	//	5 Qtrs ago
	public String getOverall5RatStr() {
		if (self5Rat > 0 && set5Rat > 0) {
			double d = (self5Rat + set5Rat) / 2.00;
			return String.valueOf(decf.format(d));
		} else if (self5Rat > 0) {
			double d = (self2Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else if (set5Rat > 0) {
			double d = (set5Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else {
			return "";
		}
	}

	public void setSelf5Rating(double i) {
		this.self5Rat = i;
	}
	public double getSelf5Rat() {
		return self5Rat;
	}
	public String getSelf5RatStr() {
		if (self5Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(self5Rat));
	}

	public void setSet5Rating(double i) {
		this.set5Rat = i;
	}
	public double getSet5Rat() {
		return set5Rat;
	}
	public String getSet5RatStr() {
		if (set5Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(set5Rat));
	}
	public void setSurvey5Rating(double i) {
		this.survey5Rat = i;
	}
	public double getSurvey5Rat() {
		return survey5Rat;
	}
	public String getSurvey5RatStr() {
		if (survey5Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(survey5Rat));
	}

	//	6 Qtrs ago
	public String getOverall6RatStr() {
		if (self6Rat > 0 && set6Rat > 0) {
			double d = (self6Rat + set6Rat) / 2.00;
			return String.valueOf(decf.format(d));
		} else if (self6Rat > 0) {
			double d = (self6Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else if (set6Rat > 0) {
			double d = (set6Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else {
			return "";
		}
	}

	public void setSelf6Rating(double i) {
		this.self6Rat = i;
	}
	public double getSelf6Rat() {
		return self6Rat;
	}
	public String getSelf6RatStr() {
		if (self6Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(self6Rat));
	}

	public void setSet6Rating(double i) {
		this.set6Rat = i;
	}
	public double getSet6Rat() {
		return set6Rat;
	}
	public String getSet6RatStr() {
		if (set6Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(set6Rat));
	}
	public void setSurvey6Rating(double i) {
		this.survey6Rat = i;
	}
	public double getSurvey6Rat() {
		return survey6Rat;
	}
	public String getSurvey6RatStr() {
		if (survey6Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(survey6Rat));
	}
	//	7 Qtrs ago
	public String getOverall7RatStr() {
		if (self7Rat > 0 && set7Rat > 0) {
			double d = (self7Rat + set7Rat) / 2.00;
			return String.valueOf(decf.format(d));
		} else if (self7Rat > 0) {
			double d = (self7Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else if (set7Rat > 0) {
			double d = (set7Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else {
			return "";
		}
	}

	public void setSelf7Rating(double i) {
		this.self7Rat = i;
	}
	public double getSelf7Rat() {
		return self7Rat;
	}
	public String getSelf7RatStr() {
		if (self7Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(self7Rat));
	}

	public void setSet7Rating(double i) {
		this.set7Rat = i;
	}
	public double getSet7Rat() {
		return set7Rat;
	}
	public String getSet7RatStr() {
		if (set7Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(set7Rat));
	}
	public void setSurvey7Rating(double i) {
		this.survey7Rat = i;
	}
	public double getSurvey7Rat() {
		return survey7Rat;
	}
	public String getSurvey7RatStr() {
		if (survey7Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(survey7Rat));
	}

	//	8 Qtrs ago
	public String getOverall8RatStr() {
		if (self8Rat > 0 && set8Rat > 0) {
			double d = (self8Rat + set8Rat) / 2.00;
			return String.valueOf(decf.format(d));
		} else if (self8Rat > 0) {
			double d = (self8Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else if (set8Rat > 0) {
			double d = (set8Rat) / 1.00;
			return String.valueOf(decf.format(d));
		} else {
			return "";
		}
	}
	public void setSelf8Rating(double i) {
		this.self8Rat = i;
	}
	public double getSelf8Rat() {
		return self8Rat;
	}
	public String getSelf8RatStr() {
		if (self8Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(self8Rat));
	}

	public void setSet8Rating(double i) {
		this.set8Rat = i;
	}
	public double getSet8Rat() {
		return set8Rat;
	}
	public String getSet8RatStr() {
		if (set8Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(set8Rat));
	}
	public void setSurvey8Rating(double i) {
		this.survey8Rat = i;
	}
	public double getSurvey8Rat() {
		return survey2Rat;
	}
	public String getSurvey8RatStr() {
		if (survey2Rat < 0) {
			return "";
		}
		return String.valueOf(decf.format(survey2Rat));
	}
	public void setRowName(String s) {
		rowName = s;
	}
	public String getRowName() {
		return rowName;
	}

	public void setRowDesc(String s) {
		rowDesc = s;
	}
	public String getRowDesc() {
		return rowDesc;
	}

	/*
		public void setAvgRating(float i){
			 this.avgrating = i;
			 System.out.println("avg = "+i);
		}
		public String getAvgRatingStr(){
			NumberFormat format = new DecimalFormat("0.0");
			String ar = format.format(avgrating);
			System.out.println("ar = "+ar);
			return String.valueOf(ar);
		}
		*/

	public void setChangeStr(boolean b) {
		changeStr = b;
	}

	public String getFieldByName(String name) {
		String s = name;

		try {
			String sGet = "get";
			if (ratingsToGet.contains(name))
				sGet = "get" + rowDesc;
			Method m = getClass().getMethod(sGet + name, null);
			Object o = m.invoke(this, null);

			s = String.valueOf(o);

		} catch (Exception e) {
			System.out.println("ERROR in getting field" + name);
			e.printStackTrace();
		}

		return s;
	}

}
