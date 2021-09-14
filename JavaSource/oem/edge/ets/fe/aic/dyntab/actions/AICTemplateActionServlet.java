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

package oem.edge.ets.fe.aic.dyntab.actions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.aic.common.exception.AICApplicationException;
import oem.edge.ets.fe.aic.common.exception.AICDBException;
import oem.edge.ets.fe.aic.common.exception.AICDataAccessException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.common.helper.SQLCode;
import oem.edge.ets.fe.aic.common.util.AICDBUtils;
import oem.edge.ets.fe.aic.common.util.AICUtil;
import oem.edge.ets.fe.aic.common.vo.ValueObject;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTemplateBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.proxy.AICDynTabTemplateProxy;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTemplateActionServlet extends HttpServlet {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		doPerform(request, response);
	}

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		doPerform(request, response);
	}

	public void doPerform(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		//createTemplate();
		//viewTemplate();
		//findAllTemplate();
		//createTemplateWithColumns();
		//deleteColumnFromTemplate();
		//editColumnFromTemplate();
		//deleteTemplate();
		findAllTemplateWithConn();
		

	}

	static void createTemplate() {
		AICTemplateVO objAICTemplateVO = new AICTemplateVO();
		objAICTemplateVO.setActive("Y");
		objAICTemplateVO.setTemplateId(AICUtil.getUniqueId());
		//objAICTemplateVO.setTemplateId("1127991475266");
		objAICTemplateVO.setTemplateName("Test Template1");

		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
			new AICDynTabTemplateBusinessDelegate();
		try {

			AICTemplateVO anotherAICTemplateVO =(AICTemplateVO) objAICDynTabTemplateBusinessDelegate.createTemplate(objAICTemplateVO);
			System.out.println(
				"Insertted Sucessfully="
					+ anotherAICTemplateVO.getTemplateUpdatedate());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}
	}

	static void viewTemplateWithColums() {
		AICTemplateVO objAICTemplateVO = new AICTemplateVO();

		objAICTemplateVO.setTemplateId("1127991998484");
		objAICTemplateVO.setTemplateName("Test Template");
		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
			new AICDynTabTemplateBusinessDelegate();
		try {

			AICTemplateVO anotherAICTemplateVO =
				(
					AICTemplateVO) objAICDynTabTemplateBusinessDelegate
						.viewTemplateWithColums(
					objAICTemplateVO);
			System.out.println(
				"View Template=" + anotherAICTemplateVO.getTemplateName());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());
			ase.printStackTrace();

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}
	}

	static void findAllTemplate() {
		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
			new AICDynTabTemplateBusinessDelegate();
		try {

			ArrayList aList =
				(ArrayList) objAICDynTabTemplateBusinessDelegate
					.findAllTemplates();
			System.out.println("Find Template Length =" + aList.size());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}
	}

	static void createTemplateWithColumns() {

		AICTemplateVO objAICTemplateVO = new AICTemplateVO();
		AICTemplateColumnVO objAICTemplateColumnVO1 = new AICTemplateColumnVO();
		objAICTemplateColumnVO1.setColumnId(AICUtil.getUniqueId());
		objAICTemplateColumnVO1.setActive("Y");
		objAICTemplateColumnVO1.setColumnName("Col1");
		objAICTemplateColumnVO1.setColumnOrder(1);
		objAICTemplateColumnVO1.setColumnType("Integer");
		objAICTemplateColumnVO1.setRequired("Y");

		AICTemplateColumnVO objAICTemplateColumnVO2 = new AICTemplateColumnVO();
		objAICTemplateColumnVO2.setColumnId("11279937276754");
		objAICTemplateColumnVO2.setActive("Y");
		objAICTemplateColumnVO2.setColumnName("Col2");
		objAICTemplateColumnVO2.setColumnOrder(2);
		objAICTemplateColumnVO2.setColumnType("String");
		objAICTemplateColumnVO2.setRequired("Y");

		objAICTemplateVO.setTemplateId("1127991998484");
		objAICTemplateVO.setTemplateName("Test Template");

		ArrayList al = new ArrayList();
		al.add(objAICTemplateColumnVO1);
		al.add(objAICTemplateColumnVO2);
		objAICTemplateVO.setTemplateColVOCollection(al);

		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
			new AICDynTabTemplateBusinessDelegate();
		try {

			AICTemplateVO anotherAICTemplateVO =
				(
					AICTemplateVO) objAICDynTabTemplateBusinessDelegate
						.createTemplateWithColumns(
					objAICTemplateVO);
			System.out.println(
				"Cerate Template with columns="
					+ anotherAICTemplateVO.getTemplateName());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}

	}

	static void deleteColumnFromTemplate() {
		AICTemplateVO objAICTemplateVO = new AICTemplateVO();

		AICTemplateColumnVO objAICTemplateColumnVO1 = new AICTemplateColumnVO();
		objAICTemplateColumnVO1.setColumnId("11279937276754");
		objAICTemplateColumnVO1.setActive("N");
		objAICTemplateColumnVO1.setColumnName("Col2");
		objAICTemplateColumnVO1.setColumnOrder(2);
		objAICTemplateColumnVO1.setColumnType("String");
		objAICTemplateColumnVO1.setRequired("Y");

		objAICTemplateVO.setTemplateId("1127991998484");
		objAICTemplateVO.setTemplateName("Test Template");

		ArrayList al = new ArrayList();

		al.add(objAICTemplateColumnVO1);
		objAICTemplateVO.setTemplateColVOCollection(al);

		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
			new AICDynTabTemplateBusinessDelegate();
		try {

			objAICDynTabTemplateBusinessDelegate.deleteColumnFromTemplate(
				objAICTemplateVO);
			System.out.println(
				"Delete Template with columns="
					+ objAICTemplateVO.getTemplateName());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}
	}

	static void editColumnFromTemplate() {
		AICTemplateVO objAICTemplateVO = new AICTemplateVO();

		AICTemplateColumnVO objAICTemplateColumnVO1 = new AICTemplateColumnVO();
		objAICTemplateColumnVO1.setColumnId("1127998376000");
		objAICTemplateColumnVO1.setActive("Y");
		objAICTemplateColumnVO1.setColumnName("Accounts Col2");
		objAICTemplateColumnVO1.setColumnOrder(2);
		objAICTemplateColumnVO1.setColumnType("String");
		objAICTemplateColumnVO1.setRequired("Y");

		objAICTemplateVO.setTemplateId("1127991998484");
		objAICTemplateVO.setTemplateName("Test Template");

		ArrayList al = new ArrayList();

		al.add(objAICTemplateColumnVO1);
		objAICTemplateVO.setTemplateColVOCollection(al);

		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
			new AICDynTabTemplateBusinessDelegate();
		try {

			AICTemplateVO anotherAICTemplateVO =
				(
					AICTemplateVO) objAICDynTabTemplateBusinessDelegate
						.editColumnFromTemplate(
					objAICTemplateVO);
			System.out.println(
				"Edit Template with columns="
					+ anotherAICTemplateVO.getTemplateName());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}
	}

	static void deleteTemplate() {
		AICTemplateVO objAICTemplateVO = new AICTemplateVO();
		objAICTemplateVO.setActive("N");
		objAICTemplateVO.setTemplateId("1127991998484");
		objAICTemplateVO.setTemplateName("Test Template");

		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
			new AICDynTabTemplateBusinessDelegate();
		try {

			objAICDynTabTemplateBusinessDelegate.deleteTemplate(objAICTemplateVO);
			System.out.println(
				"Delete Template Sucessfully="
					+ objAICTemplateVO.getTemplateUpdatedate());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}
	}
	
	static void findAllTemplateWithConn() {
		AICDynTabTemplateProxy objAICDynTabTemplateProxy = new AICDynTabTemplateProxy();
		Connection conn = null;
		try{
		
		conn = ETSDBUtils.getConnection();
		Collection coll = objAICDynTabTemplateProxy.findAllTemplates(conn);
		System.out.println("size="+coll.size());
		Iterator itr = coll.iterator();
		AICTemplateVO objAICTemplateVO = null;
		while(itr.hasNext())
		{
			objAICTemplateVO = (AICTemplateVO)itr.next();
			objAICTemplateVO.getTemplateId();
			objAICTemplateVO.getTemplateName();
		}
		if(conn != null)
		{		
			conn.close();
		}
		}catch(Exception ex)
		{ 
			ex.printStackTrace();
		}
		finally{
			if(conn != null)
			{		
				try{
				
				conn.close();
				}catch(Exception ex)
				{ 
					ex.printStackTrace();
				}
			}
		}
	}
	
	

}
