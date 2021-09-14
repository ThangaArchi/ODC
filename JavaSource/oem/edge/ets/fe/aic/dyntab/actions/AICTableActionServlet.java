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
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.aic.common.exception.AICApplicationException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.common.util.AICDBUtils;
import oem.edge.ets.fe.aic.common.util.AICUtil;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTableBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.proxy.AICDynTabTableProxy;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowsDataVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableVO;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTableActionServlet extends HttpServlet {
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
		//createTable();
		//createTableWithDatas();
		//editDataFromTable();
		//viewTable();
		//deleteRowFromTable();
		//createTableWithConn();
		getTableURI();
	}

	public static void createTable() {
		AICTableVO objAICTableVO = new AICTableVO();
		objAICTableVO.setActive("Y");
		objAICTableVO.setTableId(AICUtil.getUniqueId());
		objAICTableVO.setTableName("Test Table12");
		objAICTableVO.setTemplateId("1127991516453");
		objAICTableVO.setDocId(10002000);
		AICDynTabTableBusinessDelegate objAICDynTabTableBusinessDelegate =
			new AICDynTabTableBusinessDelegate();
		try {

			AICTableVO anotherAICTableVO =
				(
					AICTableVO) objAICDynTabTableBusinessDelegate
						.createTableFromTemplate(
					objAICTableVO);
			System.out.println(
				"Insertted Sucessfully="
					+ anotherAICTableVO.getTableUpdateDate());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}
	}

	static void createTableWithDatas() {

		AICTableVO objAICTableVO = new AICTableVO();

		AICTableRowsDataVO objAICTableRowsDataVO1 = new AICTableRowsDataVO();
		objAICTableRowsDataVO1.setActive("Y");
		objAICTableRowsDataVO1.setColumnName("Col1");
		objAICTableRowsDataVO1.setColumnId("1127998206234");
		objAICTableRowsDataVO1.setDataId(AICUtil.getUniqueId() + "0");
		objAICTableRowsDataVO1.setDataValue("001");
		objAICTableRowsDataVO1.setRowId("1");

		AICTableRowsDataVO objAICTableRowsDataVO2 = new AICTableRowsDataVO();
		objAICTableRowsDataVO2.setActive("Y");
		objAICTableRowsDataVO2.setColumnName("Col2");
		objAICTableRowsDataVO2.setColumnId("1127993723609");
		objAICTableRowsDataVO2.setDataId(AICUtil.getUniqueId() + "1");
		objAICTableRowsDataVO2.setDataValue("Vivekanandan");
		objAICTableRowsDataVO2.setRowId("1");

		AICTableRowsDataVO objAICTableRowsDataVO3 = new AICTableRowsDataVO();
		objAICTableRowsDataVO3.setActive("Y");
		objAICTableRowsDataVO3.setColumnName("Col1");
		objAICTableRowsDataVO3.setColumnId("1127998206234");
		objAICTableRowsDataVO3.setDataId(AICUtil.getUniqueId() + "2");
		objAICTableRowsDataVO3.setDataValue("002");
		objAICTableRowsDataVO3.setRowId("2");

		AICTableRowsDataVO objAICTableRowsDataVO4 = new AICTableRowsDataVO();
		objAICTableRowsDataVO4.setActive("Y");
		objAICTableRowsDataVO4.setColumnName("Col2");
		objAICTableRowsDataVO4.setColumnId("1127993723609");
		objAICTableRowsDataVO4.setDataId(AICUtil.getUniqueId() + "3");
		objAICTableRowsDataVO4.setDataValue("Narendran");
		objAICTableRowsDataVO4.setRowId("2");

		objAICTableVO.setTemplateId("1127991516453");
		objAICTableVO.setTableId("1128075100875");

		ArrayList al = new ArrayList();
		al.add(objAICTableRowsDataVO1);
		al.add(objAICTableRowsDataVO2);
		al.add(objAICTableRowsDataVO3);
		al.add(objAICTableRowsDataVO4);

		objAICTableVO.setAICTableRowsDataCollection(al);

		AICDynTabTableBusinessDelegate objAICDynTabTableBusinessDelegate =
			new AICDynTabTableBusinessDelegate();
		try {

			AICTableVO anotherAICTableVO =
				(AICTableVO) objAICDynTabTableBusinessDelegate.addRowsToTable(
					objAICTableVO);
			System.out.println(
				"Cerate Table with datas=" + anotherAICTableVO.getTableName());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}

	}

	static void editDataFromTable() {

		AICTableVO objAICTableVO = new AICTableVO();

		/*
		AICTableRowsDataVO objAICTableRowsDataVO1 = new AICTableRowsDataVO();			
		objAICTableRowsDataVO1.setActive("Y");
		objAICTableRowsDataVO1.setColumnName("Col1");
		objAICTableRowsDataVO1.setColumnId("1127998206234");
		objAICTableRowsDataVO1.setDataId(AICUtil.getUniqueId()+"0");
		objAICTableRowsDataVO1.setDataValue("001");
		objAICTableRowsDataVO1.setRowId("1");
		
		
		
		AICTableRowsDataVO objAICTableRowsDataVO2 = new AICTableRowsDataVO();			
		objAICTableRowsDataVO2.setActive("Y");
		objAICTableRowsDataVO2.setColumnName("Col2");
		objAICTableRowsDataVO2.setColumnId("1127993723609");
		objAICTableRowsDataVO2.setDataId(AICUtil.getUniqueId()+"1");
		objAICTableRowsDataVO2.setDataValue("Vivekanandan");
		objAICTableRowsDataVO2.setRowId("1");
		*/

		AICTableRowsDataVO objAICTableRowsDataVO3 = new AICTableRowsDataVO();
		objAICTableRowsDataVO3.setActive("Y");
		objAICTableRowsDataVO3.setColumnName("Col1");
		objAICTableRowsDataVO3.setColumnId("1127998206234");
		objAICTableRowsDataVO3.setDataId("11280767386872");
		objAICTableRowsDataVO3.setDataValue("003");
		objAICTableRowsDataVO3.setRowId("2");

		AICTableRowsDataVO objAICTableRowsDataVO4 = new AICTableRowsDataVO();
		objAICTableRowsDataVO4.setActive("Y");
		objAICTableRowsDataVO4.setColumnName("Col2");
		objAICTableRowsDataVO4.setColumnId("1127993723609");
		objAICTableRowsDataVO4.setDataId("11280767386873");
		objAICTableRowsDataVO4.setDataValue("Nandhu");
		objAICTableRowsDataVO4.setRowId("2");

		objAICTableVO.setTemplateId("1127991516453");
		objAICTableVO.setTableId("1128075100875");

		ArrayList al = new ArrayList();
		//al.add(objAICTableRowsDataVO1);
		//al.add(objAICTableRowsDataVO2);
		al.add(objAICTableRowsDataVO3);
		al.add(objAICTableRowsDataVO4);

		objAICTableVO.setAICTableRowsDataCollection(al);

		AICDynTabTableBusinessDelegate objAICDynTabTableBusinessDelegate =
			new AICDynTabTableBusinessDelegate();
		try {

			AICTableVO anotherAICTableVO =
				(
					AICTableVO) objAICDynTabTableBusinessDelegate
						.editRowFromTable(
					objAICTableVO);
			System.out.println(
				"Edit Table with datas=" + anotherAICTableVO.getTableName());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}
	}

	public static void viewTable() {
		AICTableVO objAICTableVO = new AICTableVO();

		objAICTableVO.setTableId("1128075100875");

		AICDynTabTableBusinessDelegate objAICDynTabTableBusinessDelegate =
			new AICDynTabTableBusinessDelegate();
		try {

			AICTableVO anotherAICTableVO =
				(AICTableVO) objAICDynTabTableBusinessDelegate.viewTable(
					objAICTableVO);

			ArrayList al =
				(ArrayList) anotherAICTableVO.getAICTableRowsDataCollection();
			Iterator it = al.iterator();

			AICTableRowsDataVO objAICTableRowsDataVO = null;

			while (it.hasNext()) {
				objAICTableRowsDataVO = (AICTableRowsDataVO) it.next();
				System.out.println(
					"Table Data ID=" + objAICTableRowsDataVO.getDataId());
				System.out.println(
					"Table Data Value=" + objAICTableRowsDataVO.getDataValue());
			}

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());
			ase.printStackTrace();

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}

	}

	public static void deleteRowFromTable() {
		AICTableVO objAICTableVO = new AICTableVO();

		AICTableRowsDataVO objAICTableRowsDataVO3 = new AICTableRowsDataVO();
		objAICTableRowsDataVO3.setActive("N");
		objAICTableRowsDataVO3.setColumnName("Col1");
		objAICTableRowsDataVO3.setColumnId("1127998206234");
		objAICTableRowsDataVO3.setDataId("11280767386872");
		objAICTableRowsDataVO3.setDataValue("003");
		objAICTableRowsDataVO3.setRowId("2");

		AICTableRowsDataVO objAICTableRowsDataVO4 = new AICTableRowsDataVO();
		objAICTableRowsDataVO4.setActive("N");
		objAICTableRowsDataVO4.setColumnName("Col2");
		objAICTableRowsDataVO4.setColumnId("1127993723609");
		objAICTableRowsDataVO4.setDataId("11280767386873");
		objAICTableRowsDataVO4.setDataValue("Nandhu");
		objAICTableRowsDataVO4.setRowId("2");

		objAICTableVO.setTemplateId("1127991516453");
		objAICTableVO.setTableId("1128075100875");

		ArrayList al = new ArrayList();
		//al.add(objAICTableRowsDataVO1);
		//al.add(objAICTableRowsDataVO2);
		al.add(objAICTableRowsDataVO3);
		al.add(objAICTableRowsDataVO4);

		objAICTableVO.setAICTableRowsDataCollection(al);

		AICDynTabTableBusinessDelegate objAICDynTabTableBusinessDelegate =
			new AICDynTabTableBusinessDelegate();
		try {

			objAICDynTabTableBusinessDelegate.deleteRowsFromTable(objAICTableVO);
			System.out.println(
				"Delete Table with datas=" + objAICTableVO.getTableName());

		} catch (AICApplicationException ape) {
			System.out.println("App Message = " + ape.getMessage());

		} catch (AICSystemException ase) {
			System.out.println("Sys Message = " + ase.getMessage());

		} catch (Exception ex) {
			System.out.println("Exp Message = " + ex.getMessage());

		}

	}
	
	public static void createTableWithConn() {
			AICTableVO objAICTableVO = new AICTableVO();
			objAICTableVO.setActive("Y");
			objAICTableVO.setTableId(AICUtil.getUniqueId());
			objAICTableVO.setTableName("Test Table");
			objAICTableVO.setTemplateId("1129715578406");
			objAICTableVO.setDocId(10002000);
			
		AICDynTabTableProxy objAICDynTabTableProxy = new AICDynTabTableProxy();
		Connection conn = null;
			try {
				conn = ETSDBUtils.getConnection();
				AICTableVO anotherAICTableVO =(AICTableVO) objAICDynTabTableProxy.createTableFromTemplate(objAICTableVO,conn);
				System.out.println(
					"Insertted Sucessfully="
						+ anotherAICTableVO.getTableUpdateDate());

			} catch (AICApplicationException ape) {
				System.out.println("App Message = " + ape.getMessage());

			} catch (AICSystemException ase) {
				System.out.println("Sys Message = " + ase.getMessage());

			} catch (Exception ex) {
				System.out.println("Exp Message = " + ex.getMessage());

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
		
	public static void getTableURI() {
		int intDocId = 10001;
		AICDynTabTableProxy objAICDynTabTableProxy = new AICDynTabTableProxy();
		System.out.println("URI to be displayed in Documents Code Base="+objAICDynTabTableProxy.getTableURI(intDocId));
	}
}
