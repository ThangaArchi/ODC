/**
 * DropboxAccessServiceInformation.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf20535.11 v91905174159
 */

package oem.edge.ed.odc.dropbox.service;

public class DropboxAccessServiceInformation implements com.ibm.ws.webservices.multiprotocol.ServiceInformation {

    private static java.util.Map operationDescriptions;
    private static java.util.Map typeMappings;

    static {
         initOperationDescriptions();
         initTypeMappings();
    }

    private static void initOperationDescriptions() { 
        operationDescriptions = new java.util.HashMap();

        java.util.Map inner0 = new java.util.HashMap();

        java.util.List list0 = new java.util.ArrayList();
        inner0.put("addGroupAcl", list0);

        com.ibm.ws.webservices.engine.description.OperationDesc addGroupAcl0Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params0 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_17"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_17"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params0[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params0[0].setOption("partName","long");
        _params0[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params0[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc0 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults0 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        addGroupAcl0Op = new com.ibm.ws.webservices.engine.description.OperationDesc("addGroupAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAcl"), _params0, _returnDesc0, _faults0, null);
        addGroupAcl0Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        addGroupAcl0Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addGroupAcl0Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        addGroupAcl0Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAclResponse"));
        addGroupAcl0Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAclRequest"));
        addGroupAcl0Op.setOption("outputName","addGroupAclResponse");
        addGroupAcl0Op.setOption("inputName","addGroupAclRequest");
        addGroupAcl0Op.setOption("buildNum","cf20535.11");
        addGroupAcl0Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addGroupAcl0Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list0.add(addGroupAcl0Op);

        com.ibm.ws.webservices.engine.description.OperationDesc addGroupAcl1Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params1 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_18"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_18"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_18"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params1[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params1[0].setOption("partName","string");
        _params1[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params1[1].setOption("partName","string");
        _params1[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params1[2].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc1 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults1 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        addGroupAcl1Op = new com.ibm.ws.webservices.engine.description.OperationDesc("addGroupAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAcl"), _params1, _returnDesc1, _faults1, null);
        addGroupAcl1Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        addGroupAcl1Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addGroupAcl1Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        addGroupAcl1Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAclResponse1"));
        addGroupAcl1Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAclRequest1"));
        addGroupAcl1Op.setOption("outputName","addGroupAclResponse1");
        addGroupAcl1Op.setOption("inputName","addGroupAclRequest1");
        addGroupAcl1Op.setOption("buildNum","cf20535.11");
        addGroupAcl1Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addGroupAcl1Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list0.add(addGroupAcl1Op);

        java.util.List list2 = new java.util.ArrayList();
        inner0.put("addItemToPackage", list2);

        com.ibm.ws.webservices.engine.description.OperationDesc addItemToPackage2Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params2 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_46"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_46"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params2[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params2[0].setOption("partName","long");
        _params2[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params2[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc2 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults2 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        addItemToPackage2Op = new com.ibm.ws.webservices.engine.description.OperationDesc("addItemToPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addItemToPackage"), _params2, _returnDesc2, _faults2, null);
        addItemToPackage2Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        addItemToPackage2Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addItemToPackage2Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        addItemToPackage2Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addItemToPackageResponse"));
        addItemToPackage2Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addItemToPackageRequest"));
        addItemToPackage2Op.setOption("outputName","addItemToPackageResponse");
        addItemToPackage2Op.setOption("inputName","addItemToPackageRequest");
        addItemToPackage2Op.setOption("buildNum","cf20535.11");
        addItemToPackage2Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addItemToPackage2Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list2.add(addItemToPackage2Op);

        java.util.List list3 = new java.util.ArrayList();
        inner0.put("addPackageAcl", list3);

        com.ibm.ws.webservices.engine.description.OperationDesc addPackageAcl3Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params3 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_13"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_13"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_13"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
          };
        _params3[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params3[0].setOption("partName","long");
        _params3[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params3[1].setOption("partName","string");
        _params3[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params3[2].setOption("partName","byte");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc3 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults3 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        addPackageAcl3Op = new com.ibm.ws.webservices.engine.description.OperationDesc("addPackageAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAcl"), _params3, _returnDesc3, _faults3, null);
        addPackageAcl3Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        addPackageAcl3Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addPackageAcl3Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        addPackageAcl3Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAclResponse"));
        addPackageAcl3Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAclRequest"));
        addPackageAcl3Op.setOption("outputName","addPackageAclResponse");
        addPackageAcl3Op.setOption("inputName","addPackageAclRequest");
        addPackageAcl3Op.setOption("buildNum","cf20535.11");
        addPackageAcl3Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addPackageAcl3Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list3.add(addPackageAcl3Op);

        com.ibm.ws.webservices.engine.description.OperationDesc addPackageAcl4Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params4 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_14"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_14"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "AclInfo"), oem.edge.ed.odc.dropbox.common.AclInfo.class, false, false, false, false, true, false), 
          };
        _params4[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params4[0].setOption("partName","long");
        _params4[1].setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}AclInfo");
        _params4[1].setOption("partName","AclInfo");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc4 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults4 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        addPackageAcl4Op = new com.ibm.ws.webservices.engine.description.OperationDesc("addPackageAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAcl"), _params4, _returnDesc4, _faults4, null);
        addPackageAcl4Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        addPackageAcl4Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addPackageAcl4Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        addPackageAcl4Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAclResponse1"));
        addPackageAcl4Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAclRequest1"));
        addPackageAcl4Op.setOption("outputName","addPackageAclResponse1");
        addPackageAcl4Op.setOption("inputName","addPackageAclRequest1");
        addPackageAcl4Op.setOption("buildNum","cf20535.11");
        addPackageAcl4Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addPackageAcl4Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list3.add(addPackageAcl4Op);

        java.util.List list5 = new java.util.ArrayList();
        inner0.put("addProjectAcl", list5);

        com.ibm.ws.webservices.engine.description.OperationDesc addProjectAcl5Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params5 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_19"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_19"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params5[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params5[0].setOption("partName","long");
        _params5[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params5[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc5 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults5 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        addProjectAcl5Op = new com.ibm.ws.webservices.engine.description.OperationDesc("addProjectAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addProjectAcl"), _params5, _returnDesc5, _faults5, null);
        addProjectAcl5Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        addProjectAcl5Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addProjectAcl5Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        addProjectAcl5Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addProjectAclResponse"));
        addProjectAcl5Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addProjectAclRequest"));
        addProjectAcl5Op.setOption("outputName","addProjectAclResponse");
        addProjectAcl5Op.setOption("inputName","addProjectAclRequest");
        addProjectAcl5Op.setOption("buildNum","cf20535.11");
        addProjectAcl5Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addProjectAcl5Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list5.add(addProjectAcl5Op);

        java.util.List list6 = new java.util.ArrayList();
        inner0.put("addUserAcl", list6);

        com.ibm.ws.webservices.engine.description.OperationDesc addUserAcl6Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params6 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_16"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_16"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params6[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params6[0].setOption("partName","long");
        _params6[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params6[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc6 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults6 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        addUserAcl6Op = new com.ibm.ws.webservices.engine.description.OperationDesc("addUserAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addUserAcl"), _params6, _returnDesc6, _faults6, null);
        addUserAcl6Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        addUserAcl6Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addUserAcl6Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        addUserAcl6Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addUserAclResponse"));
        addUserAcl6Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addUserAclRequest"));
        addUserAcl6Op.setOption("outputName","addUserAclResponse");
        addUserAcl6Op.setOption("inputName","addUserAclRequest");
        addUserAcl6Op.setOption("buildNum","cf20535.11");
        addUserAcl6Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        addUserAcl6Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list6.add(addUserAcl6Op);

        java.util.List list7 = new java.util.ArrayList();
        inner0.put("allocateUploadFileSlot", list7);

        com.ibm.ws.webservices.engine.description.OperationDesc allocateUploadFileSlot7Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params7 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_49"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_49"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_49"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params7[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params7[0].setOption("partName","long");
        _params7[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params7[1].setOption("partName","long");
        _params7[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params7[2].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc7 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "allocateUploadFileSlotReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileSlot"), oem.edge.ed.odc.dropbox.common.FileSlot.class, true, false, false, false, true, false); 
        _returnDesc7.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}FileSlot");
        _returnDesc7.setOption("partName","FileSlot");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults7 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        allocateUploadFileSlot7Op = new com.ibm.ws.webservices.engine.description.OperationDesc("allocateUploadFileSlot", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "allocateUploadFileSlot"), _params7, _returnDesc7, _faults7, null);
        allocateUploadFileSlot7Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        allocateUploadFileSlot7Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        allocateUploadFileSlot7Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        allocateUploadFileSlot7Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "allocateUploadFileSlotResponse"));
        allocateUploadFileSlot7Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "allocateUploadFileSlotRequest"));
        allocateUploadFileSlot7Op.setOption("outputName","allocateUploadFileSlotResponse");
        allocateUploadFileSlot7Op.setOption("inputName","allocateUploadFileSlotRequest");
        allocateUploadFileSlot7Op.setOption("buildNum","cf20535.11");
        allocateUploadFileSlot7Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        allocateUploadFileSlot7Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list7.add(allocateUploadFileSlot7Op);

        java.util.List list8 = new java.util.ArrayList();
        inner0.put("changePackageExpiration", list8);

        com.ibm.ws.webservices.engine.description.OperationDesc changePackageExpiration8Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params8 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_24"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_24"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params8[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params8[0].setOption("partName","long");
        _params8[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params8[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc8 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults8 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        changePackageExpiration8Op = new com.ibm.ws.webservices.engine.description.OperationDesc("changePackageExpiration", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "changePackageExpiration"), _params8, _returnDesc8, _faults8, null);
        changePackageExpiration8Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        changePackageExpiration8Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        changePackageExpiration8Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        changePackageExpiration8Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "changePackageExpirationResponse"));
        changePackageExpiration8Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "changePackageExpirationRequest"));
        changePackageExpiration8Op.setOption("outputName","changePackageExpirationResponse");
        changePackageExpiration8Op.setOption("inputName","changePackageExpirationRequest");
        changePackageExpiration8Op.setOption("buildNum","cf20535.11");
        changePackageExpiration8Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        changePackageExpiration8Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list8.add(changePackageExpiration8Op);

        java.util.List list9 = new java.util.ArrayList();
        inner0.put("closeSession", list9);

        com.ibm.ws.webservices.engine.description.OperationDesc closeSession9Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params9 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc9 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults9 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        closeSession9Op = new com.ibm.ws.webservices.engine.description.OperationDesc("closeSession", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "closeSession"), _params9, _returnDesc9, _faults9, null);
        closeSession9Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        closeSession9Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        closeSession9Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        closeSession9Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "closeSessionResponse"));
        closeSession9Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "closeSessionRequest"));
        closeSession9Op.setOption("outputName","closeSessionResponse");
        closeSession9Op.setOption("inputName","closeSessionRequest");
        closeSession9Op.setOption("buildNum","cf20535.11");
        closeSession9Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        closeSession9Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list9.add(closeSession9Op);

        java.util.List list10 = new java.util.ArrayList();
        inner0.put("commitPackage", list10);

        com.ibm.ws.webservices.engine.description.OperationDesc commitPackage10Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params10 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_11"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params10[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params10[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc10 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults10 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        commitPackage10Op = new com.ibm.ws.webservices.engine.description.OperationDesc("commitPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitPackage"), _params10, _returnDesc10, _faults10, null);
        commitPackage10Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        commitPackage10Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        commitPackage10Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        commitPackage10Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitPackageResponse"));
        commitPackage10Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitPackageRequest"));
        commitPackage10Op.setOption("outputName","commitPackageResponse");
        commitPackage10Op.setOption("inputName","commitPackageRequest");
        commitPackage10Op.setOption("buildNum","cf20535.11");
        commitPackage10Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        commitPackage10Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list10.add(commitPackage10Op);

        java.util.List list11 = new java.util.ArrayList();
        inner0.put("commitUploadedFile", list11);

        com.ibm.ws.webservices.engine.description.OperationDesc commitUploadedFile11Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params11 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_55"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_55"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_55"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_55"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params11[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params11[0].setOption("partName","long");
        _params11[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params11[1].setOption("partName","long");
        _params11[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params11[2].setOption("partName","long");
        _params11[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params11[3].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc11 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults11 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        commitUploadedFile11Op = new com.ibm.ws.webservices.engine.description.OperationDesc("commitUploadedFile", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitUploadedFile"), _params11, _returnDesc11, _faults11, null);
        commitUploadedFile11Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        commitUploadedFile11Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        commitUploadedFile11Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        commitUploadedFile11Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitUploadedFileResponse"));
        commitUploadedFile11Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitUploadedFileRequest"));
        commitUploadedFile11Op.setOption("outputName","commitUploadedFileResponse");
        commitUploadedFile11Op.setOption("inputName","commitUploadedFileRequest");
        commitUploadedFile11Op.setOption("buildNum","cf20535.11");
        commitUploadedFile11Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        commitUploadedFile11Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list11.add(commitUploadedFile11Op);

        java.util.List list12 = new java.util.ArrayList();
        inner0.put("createGroup", list12);

        com.ibm.ws.webservices.engine.description.OperationDesc createGroup12Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params12 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_60"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_60"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_60"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
          };
        _params12[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params12[0].setOption("partName","string");
        _params12[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params12[1].setOption("partName","byte");
        _params12[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params12[2].setOption("partName","byte");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc12 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults12 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        createGroup12Op = new com.ibm.ws.webservices.engine.description.OperationDesc("createGroup", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroup"), _params12, _returnDesc12, _faults12, null);
        createGroup12Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        createGroup12Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createGroup12Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        createGroup12Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroupResponse"));
        createGroup12Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroupRequest"));
        createGroup12Op.setOption("outputName","createGroupResponse");
        createGroup12Op.setOption("inputName","createGroupRequest");
        createGroup12Op.setOption("buildNum","cf20535.11");
        createGroup12Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createGroup12Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list12.add(createGroup12Op);

        com.ibm.ws.webservices.engine.description.OperationDesc createGroup13Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params13 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_61"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params13[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params13[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc13 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults13 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        createGroup13Op = new com.ibm.ws.webservices.engine.description.OperationDesc("createGroup", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroup"), _params13, _returnDesc13, _faults13, null);
        createGroup13Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        createGroup13Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createGroup13Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        createGroup13Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroupResponse1"));
        createGroup13Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroupRequest1"));
        createGroup13Op.setOption("outputName","createGroupResponse1");
        createGroup13Op.setOption("inputName","createGroupRequest1");
        createGroup13Op.setOption("buildNum","cf20535.11");
        createGroup13Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createGroup13Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list12.add(createGroup13Op);

        java.util.List list14 = new java.util.ArrayList();
        inner0.put("createPackage", list14);

        com.ibm.ws.webservices.engine.description.OperationDesc createPackage14Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params14 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_5_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_6_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params14[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params14[0].setOption("partName","string");
        _params14[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params14[1].setOption("partName","string");
        _params14[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params14[2].setOption("partName","long");
        _params14[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params14[3].setOption("partName","long");
        _params14[4].setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _params14[4].setOption("partName","Vector");
        _params14[5].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params14[5].setOption("partName","int");
        _params14[6].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params14[6].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc14 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc14.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc14.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults14 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        createPackage14Op = new com.ibm.ws.webservices.engine.description.OperationDesc("createPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackage"), _params14, _returnDesc14, _faults14, null);
        createPackage14Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        createPackage14Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createPackage14Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        createPackage14Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageResponse"));
        createPackage14Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageRequest"));
        createPackage14Op.setOption("outputName","createPackageResponse");
        createPackage14Op.setOption("inputName","createPackageRequest");
        createPackage14Op.setOption("buildNum","cf20535.11");
        createPackage14Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createPackage14Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list14.add(createPackage14Op);

        com.ibm.ws.webservices.engine.description.OperationDesc createPackage15Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params15 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_5_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params15[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params15[0].setOption("partName","string");
        _params15[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params15[1].setOption("partName","string");
        _params15[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params15[2].setOption("partName","long");
        _params15[3].setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _params15[3].setOption("partName","Vector");
        _params15[4].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params15[4].setOption("partName","int");
        _params15[5].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params15[5].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc15 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc15.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc15.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults15 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        createPackage15Op = new com.ibm.ws.webservices.engine.description.OperationDesc("createPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackage"), _params15, _returnDesc15, _faults15, null);
        createPackage15Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        createPackage15Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createPackage15Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        createPackage15Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageResponse1"));
        createPackage15Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageRequest1"));
        createPackage15Op.setOption("outputName","createPackageResponse1");
        createPackage15Op.setOption("inputName","createPackageRequest1");
        createPackage15Op.setOption("buildNum","cf20535.11");
        createPackage15Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createPackage15Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list14.add(createPackage15Op);

        com.ibm.ws.webservices.engine.description.OperationDesc createPackage16Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params16 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params16[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params16[0].setOption("partName","string");
        _params16[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params16[1].setOption("partName","string");
        _params16[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params16[2].setOption("partName","long");
        _params16[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params16[3].setOption("partName","int");
        _params16[4].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params16[4].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc16 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc16.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc16.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults16 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        createPackage16Op = new com.ibm.ws.webservices.engine.description.OperationDesc("createPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackage"), _params16, _returnDesc16, _faults16, null);
        createPackage16Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        createPackage16Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createPackage16Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        createPackage16Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageResponse2"));
        createPackage16Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageRequest2"));
        createPackage16Op.setOption("outputName","createPackageResponse2");
        createPackage16Op.setOption("inputName","createPackageRequest2");
        createPackage16Op.setOption("buildNum","cf20535.11");
        createPackage16Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createPackage16Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list14.add(createPackage16Op);

        com.ibm.ws.webservices.engine.description.OperationDesc createPackage17Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params17 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_7"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params17[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params17[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc17 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc17.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc17.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults17 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        createPackage17Op = new com.ibm.ws.webservices.engine.description.OperationDesc("createPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackage"), _params17, _returnDesc17, _faults17, null);
        createPackage17Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        createPackage17Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createPackage17Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        createPackage17Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageResponse3"));
        createPackage17Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageRequest3"));
        createPackage17Op.setOption("outputName","createPackageResponse3");
        createPackage17Op.setOption("inputName","createPackageRequest3");
        createPackage17Op.setOption("buildNum","cf20535.11");
        createPackage17Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createPackage17Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list14.add(createPackage17Op);

        java.util.List list18 = new java.util.ArrayList();
        inner0.put("createSession", list18);

        com.ibm.ws.webservices.engine.description.OperationDesc createSession18Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params18 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_0"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params18[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params18[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc18 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createSessionReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc18.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc18.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults18 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        createSession18Op = new com.ibm.ws.webservices.engine.description.OperationDesc("createSession", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSession"), _params18, _returnDesc18, _faults18, null);
        createSession18Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        createSession18Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createSession18Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        createSession18Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSessionResponse"));
        createSession18Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSessionRequest"));
        createSession18Op.setOption("outputName","createSessionResponse");
        createSession18Op.setOption("inputName","createSessionRequest");
        createSession18Op.setOption("buildNum","cf20535.11");
        createSession18Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createSession18Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list18.add(createSession18Op);

        com.ibm.ws.webservices.engine.description.OperationDesc createSession19Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params19 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_1"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_1"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params19[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params19[0].setOption("partName","string");
        _params19[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params19[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc19 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createSessionReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc19.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc19.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults19 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        createSession19Op = new com.ibm.ws.webservices.engine.description.OperationDesc("createSession", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSession"), _params19, _returnDesc19, _faults19, null);
        createSession19Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        createSession19Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createSession19Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        createSession19Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSessionResponse1"));
        createSession19Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSessionRequest1"));
        createSession19Op.setOption("outputName","createSessionResponse1");
        createSession19Op.setOption("inputName","createSessionRequest1");
        createSession19Op.setOption("buildNum","cf20535.11");
        createSession19Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        createSession19Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list18.add(createSession19Op);

        java.util.List list20 = new java.util.ArrayList();
        inner0.put("deleteGroup", list20);

        com.ibm.ws.webservices.engine.description.OperationDesc deleteGroup20Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params20 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_62"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params20[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params20[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc20 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults20 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        deleteGroup20Op = new com.ibm.ws.webservices.engine.description.OperationDesc("deleteGroup", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deleteGroup"), _params20, _returnDesc20, _faults20, null);
        deleteGroup20Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        deleteGroup20Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        deleteGroup20Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        deleteGroup20Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deleteGroupResponse"));
        deleteGroup20Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deleteGroupRequest"));
        deleteGroup20Op.setOption("outputName","deleteGroupResponse");
        deleteGroup20Op.setOption("inputName","deleteGroupRequest");
        deleteGroup20Op.setOption("buildNum","cf20535.11");
        deleteGroup20Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        deleteGroup20Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list20.add(deleteGroup20Op);

        java.util.List list21 = new java.util.ArrayList();
        inner0.put("deletePackage", list21);

        com.ibm.ws.webservices.engine.description.OperationDesc deletePackage21Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params21 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_10"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params21[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params21[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc21 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults21 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        deletePackage21Op = new com.ibm.ws.webservices.engine.description.OperationDesc("deletePackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deletePackage"), _params21, _returnDesc21, _faults21, null);
        deletePackage21Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        deletePackage21Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        deletePackage21Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        deletePackage21Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deletePackageResponse"));
        deletePackage21Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deletePackageRequest"));
        deletePackage21Op.setOption("outputName","deletePackageResponse");
        deletePackage21Op.setOption("inputName","deletePackageRequest");
        deletePackage21Op.setOption("buildNum","cf20535.11");
        deletePackage21Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        deletePackage21Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list21.add(deletePackage21Op);

        java.util.List list22 = new java.util.ArrayList();
        inner0.put("downloadPackage", list22);

        com.ibm.ws.webservices.engine.description.OperationDesc downloadPackage22Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params22 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_56"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_56"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params22[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params22[0].setOption("partName","long");
        _params22[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params22[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc22 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "downloadPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://schemas.xmlsoap.org/soap/encoding/", "base64"), byte[].class, true, false, false, false, true, false); 
        _returnDesc22.setOption("partQNameString","{http://schemas.xmlsoap.org/soap/encoding/}base64");
        _returnDesc22.setOption("partName","base64");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults22 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        downloadPackage22Op = new com.ibm.ws.webservices.engine.description.OperationDesc("downloadPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackage"), _params22, _returnDesc22, _faults22, null);
        downloadPackage22Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        downloadPackage22Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        downloadPackage22Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        downloadPackage22Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageResponse"));
        downloadPackage22Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageRequest"));
        downloadPackage22Op.setOption("outputName","downloadPackageResponse");
        downloadPackage22Op.setOption("inputName","downloadPackageRequest");
        downloadPackage22Op.setOption("buildNum","cf20535.11");
        downloadPackage22Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        downloadPackage22Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list22.add(downloadPackage22Op);

        java.util.List list23 = new java.util.ArrayList();
        inner0.put("downloadPackageItem", list23);

        com.ibm.ws.webservices.engine.description.OperationDesc downloadPackageItem23Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params23 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_57"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_57"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params23[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params23[0].setOption("partName","long");
        _params23[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params23[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc23 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "downloadPackageItemReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://schemas.xmlsoap.org/soap/encoding/", "base64"), byte[].class, true, false, false, false, true, false); 
        _returnDesc23.setOption("partQNameString","{http://schemas.xmlsoap.org/soap/encoding/}base64");
        _returnDesc23.setOption("partName","base64");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults23 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        downloadPackageItem23Op = new com.ibm.ws.webservices.engine.description.OperationDesc("downloadPackageItem", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItem"), _params23, _returnDesc23, _faults23, null);
        downloadPackageItem23Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        downloadPackageItem23Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        downloadPackageItem23Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        downloadPackageItem23Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItemResponse"));
        downloadPackageItem23Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItemRequest"));
        downloadPackageItem23Op.setOption("outputName","downloadPackageItemResponse");
        downloadPackageItem23Op.setOption("inputName","downloadPackageItemRequest");
        downloadPackageItem23Op.setOption("buildNum","cf20535.11");
        downloadPackageItem23Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        downloadPackageItem23Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list23.add(downloadPackageItem23Op);

        com.ibm.ws.webservices.engine.description.OperationDesc downloadPackageItem24Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params24 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_58"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_58"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_58"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_58"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params24[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params24[0].setOption("partName","long");
        _params24[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params24[1].setOption("partName","long");
        _params24[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params24[2].setOption("partName","long");
        _params24[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params24[3].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc24 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "downloadPackageItemReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://schemas.xmlsoap.org/soap/encoding/", "base64"), byte[].class, true, false, false, false, true, false); 
        _returnDesc24.setOption("partQNameString","{http://schemas.xmlsoap.org/soap/encoding/}base64");
        _returnDesc24.setOption("partName","base64");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults24 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        downloadPackageItem24Op = new com.ibm.ws.webservices.engine.description.OperationDesc("downloadPackageItem", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItem"), _params24, _returnDesc24, _faults24, null);
        downloadPackageItem24Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        downloadPackageItem24Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        downloadPackageItem24Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        downloadPackageItem24Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItemResponse1"));
        downloadPackageItem24Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItemRequest1"));
        downloadPackageItem24Op.setOption("outputName","downloadPackageItemResponse1");
        downloadPackageItem24Op.setOption("inputName","downloadPackageItemRequest1");
        downloadPackageItem24Op.setOption("buildNum","cf20535.11");
        downloadPackageItem24Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        downloadPackageItem24Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list23.add(downloadPackageItem24Op);

        java.util.List list25 = new java.util.ArrayList();
        inner0.put("getLoginMessage", list25);

        com.ibm.ws.webservices.engine.description.OperationDesc getLoginMessage25Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params25 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc25 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getLoginMessageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, true, false, false, false, true, false); 
        _returnDesc25.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _returnDesc25.setOption("partName","string");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults25 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        getLoginMessage25Op = new com.ibm.ws.webservices.engine.description.OperationDesc("getLoginMessage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getLoginMessage"), _params25, _returnDesc25, _faults25, null);
        getLoginMessage25Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        getLoginMessage25Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getLoginMessage25Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        getLoginMessage25Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getLoginMessageResponse"));
        getLoginMessage25Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getLoginMessageRequest"));
        getLoginMessage25Op.setOption("outputName","getLoginMessageResponse");
        getLoginMessage25Op.setOption("inputName","getLoginMessageRequest");
        getLoginMessage25Op.setOption("buildNum","cf20535.11");
        getLoginMessage25Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getLoginMessage25Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list25.add(getLoginMessage25Op);

        java.util.List list26 = new java.util.ArrayList();
        inner0.put("getOption", list26);

        com.ibm.ws.webservices.engine.description.OperationDesc getOption26Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params26 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_30"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params26[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params26[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc26 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getOptionReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, true, false, false, false, true, false); 
        _returnDesc26.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _returnDesc26.setOption("partName","string");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults26 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        getOption26Op = new com.ibm.ws.webservices.engine.description.OperationDesc("getOption", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOption"), _params26, _returnDesc26, _faults26, null);
        getOption26Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        getOption26Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getOption26Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        getOption26Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionResponse"));
        getOption26Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionRequest"));
        getOption26Op.setOption("outputName","getOptionResponse");
        getOption26Op.setOption("inputName","getOptionRequest");
        getOption26Op.setOption("buildNum","cf20535.11");
        getOption26Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getOption26Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list26.add(getOption26Op);

        java.util.List list27 = new java.util.ArrayList();
        inner0.put("getOptions", list27);

        com.ibm.ws.webservices.engine.description.OperationDesc getOptions27Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params27 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc27 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getOptionsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc27.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc27.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults27 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        getOptions27Op = new com.ibm.ws.webservices.engine.description.OperationDesc("getOptions", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptions"), _params27, _returnDesc27, _faults27, null);
        getOptions27Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        getOptions27Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getOptions27Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        getOptions27Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionsResponse"));
        getOptions27Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionsRequest"));
        getOptions27Op.setOption("outputName","getOptionsResponse");
        getOptions27Op.setOption("inputName","getOptionsRequest");
        getOptions27Op.setOption("buildNum","cf20535.11");
        getOptions27Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getOptions27Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list27.add(getOptions27Op);

        com.ibm.ws.webservices.engine.description.OperationDesc getOptions28Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params28 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_29"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, false, false, false, false, true, false), 
          };
        _params28[0].setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _params28[0].setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc28 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getOptionsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc28.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc28.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults28 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        getOptions28Op = new com.ibm.ws.webservices.engine.description.OperationDesc("getOptions", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptions"), _params28, _returnDesc28, _faults28, null);
        getOptions28Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        getOptions28Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getOptions28Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        getOptions28Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionsResponse1"));
        getOptions28Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionsRequest1"));
        getOptions28Op.setOption("outputName","getOptionsResponse1");
        getOptions28Op.setOption("inputName","getOptionsRequest1");
        getOptions28Op.setOption("buildNum","cf20535.11");
        getOptions28Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getOptions28Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list27.add(getOptions28Op);

        java.util.List list29 = new java.util.ArrayList();
        inner0.put("getPackageItemMD5", list29);

        com.ibm.ws.webservices.engine.description.OperationDesc getPackageItemMD529Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params29 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_59"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_59"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_59"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params29[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params29[0].setOption("partName","long");
        _params29[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params29[1].setOption("partName","long");
        _params29[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params29[2].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc29 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getPackageItemMD5Return"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "ArrayOf_xsd_nillable_string"), java.lang.String[].class, true, false, false, false, true, false); 
        _returnDesc29.setOption("partQNameString","{http://service.dropbox.odc.ed.edge.oem}ArrayOf_xsd_nillable_string");
        _returnDesc29.setOption("partName","ArrayOf_xsd_nillable_string");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults29 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        getPackageItemMD529Op = new com.ibm.ws.webservices.engine.description.OperationDesc("getPackageItemMD5", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getPackageItemMD5"), _params29, _returnDesc29, _faults29, null);
        getPackageItemMD529Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        getPackageItemMD529Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getPackageItemMD529Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        getPackageItemMD529Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getPackageItemMD5Response"));
        getPackageItemMD529Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getPackageItemMD5Request"));
        getPackageItemMD529Op.setOption("outputName","getPackageItemMD5Response");
        getPackageItemMD529Op.setOption("inputName","getPackageItemMD5Request");
        getPackageItemMD529Op.setOption("buildNum","cf20535.11");
        getPackageItemMD529Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getPackageItemMD529Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list29.add(getPackageItemMD529Op);

        java.util.List list30 = new java.util.ArrayList();
        inner0.put("getProjectList", list30);

        com.ibm.ws.webservices.engine.description.OperationDesc getProjectList30Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params30 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc30 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getProjectListReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc30.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc30.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults30 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        getProjectList30Op = new com.ibm.ws.webservices.engine.description.OperationDesc("getProjectList", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getProjectList"), _params30, _returnDesc30, _faults30, null);
        getProjectList30Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        getProjectList30Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getProjectList30Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        getProjectList30Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getProjectListResponse"));
        getProjectList30Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getProjectListRequest"));
        getProjectList30Op.setOption("outputName","getProjectListResponse");
        getProjectList30Op.setOption("inputName","getProjectListRequest");
        getProjectList30Op.setOption("buildNum","cf20535.11");
        getProjectList30Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getProjectList30Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list30.add(getProjectList30Op);

        java.util.List list31 = new java.util.ArrayList();
        inner0.put("getStoragePoolInstance", list31);

        com.ibm.ws.webservices.engine.description.OperationDesc getStoragePoolInstance31Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params31 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_25"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params31[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params31[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc31 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getStoragePoolInstanceReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "PoolInfo"), oem.edge.ed.odc.dropbox.common.PoolInfo.class, true, false, false, false, true, false); 
        _returnDesc31.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}PoolInfo");
        _returnDesc31.setOption("partName","PoolInfo");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults31 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        getStoragePoolInstance31Op = new com.ibm.ws.webservices.engine.description.OperationDesc("getStoragePoolInstance", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getStoragePoolInstance"), _params31, _returnDesc31, _faults31, null);
        getStoragePoolInstance31Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        getStoragePoolInstance31Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getStoragePoolInstance31Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        getStoragePoolInstance31Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getStoragePoolInstanceResponse"));
        getStoragePoolInstance31Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getStoragePoolInstanceRequest"));
        getStoragePoolInstance31Op.setOption("outputName","getStoragePoolInstanceResponse");
        getStoragePoolInstance31Op.setOption("inputName","getStoragePoolInstanceRequest");
        getStoragePoolInstance31Op.setOption("buildNum","cf20535.11");
        getStoragePoolInstance31Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        getStoragePoolInstance31Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list31.add(getStoragePoolInstance31Op);

        java.util.List list32 = new java.util.ArrayList();
        inner0.put("lookupUser", list32);

        com.ibm.ws.webservices.engine.description.OperationDesc lookupUser32Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params32 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_44"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_44"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params32[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params32[0].setOption("partName","string");
        _params32[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params32[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc32 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "lookupUserReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc32.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc32.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults32 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        lookupUser32Op = new com.ibm.ws.webservices.engine.description.OperationDesc("lookupUser", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "lookupUser"), _params32, _returnDesc32, _faults32, null);
        lookupUser32Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        lookupUser32Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        lookupUser32Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        lookupUser32Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "lookupUserResponse"));
        lookupUser32Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "lookupUserRequest"));
        lookupUser32Op.setOption("outputName","lookupUserResponse");
        lookupUser32Op.setOption("inputName","lookupUserRequest");
        lookupUser32Op.setOption("buildNum","cf20535.11");
        lookupUser32Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        lookupUser32Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list32.add(lookupUser32Op);

        java.util.List list33 = new java.util.ArrayList();
        inner0.put("markPackage", list33);

        com.ibm.ws.webservices.engine.description.OperationDesc markPackage33Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params33 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_12"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_12"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params33[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params33[0].setOption("partName","long");
        _params33[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params33[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc33 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults33 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        markPackage33Op = new com.ibm.ws.webservices.engine.description.OperationDesc("markPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "markPackage"), _params33, _returnDesc33, _faults33, null);
        markPackage33Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        markPackage33Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        markPackage33Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        markPackage33Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "markPackageResponse"));
        markPackage33Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "markPackageRequest"));
        markPackage33Op.setOption("outputName","markPackageResponse");
        markPackage33Op.setOption("inputName","markPackageRequest");
        markPackage33Op.setOption("buildNum","cf20535.11");
        markPackage33Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        markPackage33Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list33.add(markPackage33Op);

        java.util.List list34 = new java.util.ArrayList();
        inner0.put("modifyGroupAttributes", list34);

        com.ibm.ws.webservices.engine.description.OperationDesc modifyGroupAttributes34Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params34 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_63"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_63"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_63"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
          };
        _params34[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params34[0].setOption("partName","string");
        _params34[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params34[1].setOption("partName","byte");
        _params34[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params34[2].setOption("partName","byte");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc34 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults34 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        modifyGroupAttributes34Op = new com.ibm.ws.webservices.engine.description.OperationDesc("modifyGroupAttributes", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "modifyGroupAttributes"), _params34, _returnDesc34, _faults34, null);
        modifyGroupAttributes34Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        modifyGroupAttributes34Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        modifyGroupAttributes34Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        modifyGroupAttributes34Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "modifyGroupAttributesResponse"));
        modifyGroupAttributes34Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "modifyGroupAttributesRequest"));
        modifyGroupAttributes34Op.setOption("outputName","modifyGroupAttributesResponse");
        modifyGroupAttributes34Op.setOption("inputName","modifyGroupAttributesRequest");
        modifyGroupAttributes34Op.setOption("buildNum","cf20535.11");
        modifyGroupAttributes34Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        modifyGroupAttributes34Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list34.add(modifyGroupAttributes34Op);

        java.util.List list35 = new java.util.ArrayList();
        inner0.put("queryFile", list35);

        com.ibm.ws.webservices.engine.description.OperationDesc queryFile35Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params35 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_40"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params35[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params35[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc35 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryFileReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileInfo"), oem.edge.ed.odc.dropbox.common.FileInfo.class, true, false, false, false, true, false); 
        _returnDesc35.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}FileInfo");
        _returnDesc35.setOption("partName","FileInfo");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults35 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryFile35Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryFile", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFile"), _params35, _returnDesc35, _faults35, null);
        queryFile35Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryFile35Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryFile35Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryFile35Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileResponse"));
        queryFile35Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileRequest"));
        queryFile35Op.setOption("outputName","queryFileResponse");
        queryFile35Op.setOption("inputName","queryFileRequest");
        queryFile35Op.setOption("buildNum","cf20535.11");
        queryFile35Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryFile35Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list35.add(queryFile35Op);

        java.util.List list36 = new java.util.ArrayList();
        inner0.put("queryFileSlots", list36);

        com.ibm.ws.webservices.engine.description.OperationDesc queryFileSlots36Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params36 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_51"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_51"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params36[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params36[0].setOption("partName","long");
        _params36[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params36[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc36 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryFileSlotsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc36.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc36.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults36 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryFileSlots36Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryFileSlots", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileSlots"), _params36, _returnDesc36, _faults36, null);
        queryFileSlots36Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryFileSlots36Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryFileSlots36Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryFileSlots36Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileSlotsResponse"));
        queryFileSlots36Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileSlotsRequest"));
        queryFileSlots36Op.setOption("outputName","queryFileSlotsResponse");
        queryFileSlots36Op.setOption("inputName","queryFileSlotsRequest");
        queryFileSlots36Op.setOption("buildNum","cf20535.11");
        queryFileSlots36Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryFileSlots36Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list36.add(queryFileSlots36Op);

        java.util.List list37 = new java.util.ArrayList();
        inner0.put("queryFiles", list37);

        com.ibm.ws.webservices.engine.description.OperationDesc queryFiles37Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params37 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_38"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_38"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_38"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params37[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params37[0].setOption("partName","string");
        _params37[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params37[1].setOption("partName","boolean");
        _params37[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params37[2].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc37 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryFilesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc37.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc37.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults37 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryFiles37Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryFiles", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFiles"), _params37, _returnDesc37, _faults37, null);
        queryFiles37Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryFiles37Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryFiles37Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryFiles37Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFilesResponse"));
        queryFiles37Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFilesRequest"));
        queryFiles37Op.setOption("outputName","queryFilesResponse");
        queryFiles37Op.setOption("inputName","queryFilesRequest");
        queryFiles37Op.setOption("buildNum","cf20535.11");
        queryFiles37Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryFiles37Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list37.add(queryFiles37Op);

        com.ibm.ws.webservices.engine.description.OperationDesc queryFiles38Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params38 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_39"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params38[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params38[0].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc38 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryFilesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc38.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc38.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults38 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryFiles38Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryFiles", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFiles"), _params38, _returnDesc38, _faults38, null);
        queryFiles38Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryFiles38Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryFiles38Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryFiles38Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFilesResponse1"));
        queryFiles38Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFilesRequest1"));
        queryFiles38Op.setOption("outputName","queryFilesResponse1");
        queryFiles38Op.setOption("inputName","queryFilesRequest1");
        queryFiles38Op.setOption("buildNum","cf20535.11");
        queryFiles38Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryFiles38Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list37.add(queryFiles38Op);

        java.util.List list39 = new java.util.ArrayList();
        inner0.put("queryGroup", list39);

        com.ibm.ws.webservices.engine.description.OperationDesc queryGroup39Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params39 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_66"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params39[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params39[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc39 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryGroupReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "GroupInfo"), oem.edge.ed.odc.dsmp.common.GroupInfo.class, true, false, false, false, true, false); 
        _returnDesc39.setOption("partQNameString","{http://common.dsmp.odc.ed.edge.oem}GroupInfo");
        _returnDesc39.setOption("partName","GroupInfo");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults39 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryGroup39Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryGroup", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroup"), _params39, _returnDesc39, _faults39, null);
        queryGroup39Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryGroup39Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryGroup39Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryGroup39Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupResponse"));
        queryGroup39Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupRequest"));
        queryGroup39Op.setOption("outputName","queryGroupResponse");
        queryGroup39Op.setOption("inputName","queryGroupRequest");
        queryGroup39Op.setOption("buildNum","cf20535.11");
        queryGroup39Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryGroup39Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list39.add(queryGroup39Op);

        java.util.List list40 = new java.util.ArrayList();
        inner0.put("queryGroups", list40);

        com.ibm.ws.webservices.engine.description.OperationDesc queryGroups40Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params40 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_64"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_64"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_64"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_64"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params40[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params40[0].setOption("partName","string");
        _params40[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params40[1].setOption("partName","boolean");
        _params40[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params40[2].setOption("partName","boolean");
        _params40[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params40[3].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc40 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryGroupsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc40.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc40.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults40 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryGroups40Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryGroups", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroups"), _params40, _returnDesc40, _faults40, null);
        queryGroups40Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryGroups40Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryGroups40Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryGroups40Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupsResponse"));
        queryGroups40Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupsRequest"));
        queryGroups40Op.setOption("outputName","queryGroupsResponse");
        queryGroups40Op.setOption("inputName","queryGroupsRequest");
        queryGroups40Op.setOption("buildNum","cf20535.11");
        queryGroups40Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryGroups40Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list40.add(queryGroups40Op);

        com.ibm.ws.webservices.engine.description.OperationDesc queryGroups41Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params41 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_65"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_65"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params41[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params41[0].setOption("partName","boolean");
        _params41[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params41[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc41 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryGroupsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc41.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc41.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults41 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryGroups41Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryGroups", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroups"), _params41, _returnDesc41, _faults41, null);
        queryGroups41Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryGroups41Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryGroups41Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryGroups41Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupsResponse1"));
        queryGroups41Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupsRequest1"));
        queryGroups41Op.setOption("outputName","queryGroupsResponse1");
        queryGroups41Op.setOption("inputName","queryGroupsRequest1");
        queryGroups41Op.setOption("buildNum","cf20535.11");
        queryGroups41Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryGroups41Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list40.add(queryGroups41Op);

        java.util.List list42 = new java.util.ArrayList();
        inner0.put("queryPackage", list42);

        com.ibm.ws.webservices.engine.description.OperationDesc queryPackage42Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params42 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_36"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_36"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params42[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params42[0].setOption("partName","long");
        _params42[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params42[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc42 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "PackageInfo"), oem.edge.ed.odc.dropbox.common.PackageInfo.class, true, false, false, false, true, false); 
        _returnDesc42.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}PackageInfo");
        _returnDesc42.setOption("partName","PackageInfo");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults42 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryPackage42Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackage"), _params42, _returnDesc42, _faults42, null);
        queryPackage42Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryPackage42Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackage42Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryPackage42Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageResponse"));
        queryPackage42Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageRequest"));
        queryPackage42Op.setOption("outputName","queryPackageResponse");
        queryPackage42Op.setOption("inputName","queryPackageRequest");
        queryPackage42Op.setOption("buildNum","cf20535.11");
        queryPackage42Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackage42Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list42.add(queryPackage42Op);

        java.util.List list43 = new java.util.ArrayList();
        inner0.put("queryPackageAclCompanies", list43);

        com.ibm.ws.webservices.engine.description.OperationDesc queryPackageAclCompanies43Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params43 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_42"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params43[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params43[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc43 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageAclCompaniesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc43.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc43.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults43 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryPackageAclCompanies43Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackageAclCompanies", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclCompanies"), _params43, _returnDesc43, _faults43, null);
        queryPackageAclCompanies43Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryPackageAclCompanies43Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackageAclCompanies43Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryPackageAclCompanies43Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclCompaniesResponse"));
        queryPackageAclCompanies43Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclCompaniesRequest"));
        queryPackageAclCompanies43Op.setOption("outputName","queryPackageAclCompaniesResponse");
        queryPackageAclCompanies43Op.setOption("inputName","queryPackageAclCompaniesRequest");
        queryPackageAclCompanies43Op.setOption("buildNum","cf20535.11");
        queryPackageAclCompanies43Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackageAclCompanies43Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list43.add(queryPackageAclCompanies43Op);

        java.util.List list44 = new java.util.ArrayList();
        inner0.put("queryPackageAcls", list44);

        com.ibm.ws.webservices.engine.description.OperationDesc queryPackageAcls44Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params44 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_41"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_41"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params44[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params44[0].setOption("partName","long");
        _params44[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params44[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc44 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageAclsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc44.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc44.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults44 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryPackageAcls44Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackageAcls", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAcls"), _params44, _returnDesc44, _faults44, null);
        queryPackageAcls44Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryPackageAcls44Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackageAcls44Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryPackageAcls44Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclsResponse"));
        queryPackageAcls44Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclsRequest"));
        queryPackageAcls44Op.setOption("outputName","queryPackageAclsResponse");
        queryPackageAcls44Op.setOption("inputName","queryPackageAclsRequest");
        queryPackageAcls44Op.setOption("buildNum","cf20535.11");
        queryPackageAcls44Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackageAcls44Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list44.add(queryPackageAcls44Op);

        java.util.List list45 = new java.util.ArrayList();
        inner0.put("queryPackageContents", list45);

        com.ibm.ws.webservices.engine.description.OperationDesc queryPackageContents45Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params45 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_37"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params45[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params45[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc45 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageContentsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc45.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc45.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults45 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryPackageContents45Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackageContents", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageContents"), _params45, _returnDesc45, _faults45, null);
        queryPackageContents45Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryPackageContents45Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackageContents45Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryPackageContents45Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageContentsResponse"));
        queryPackageContents45Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageContentsRequest"));
        queryPackageContents45Op.setOption("outputName","queryPackageContentsResponse");
        queryPackageContents45Op.setOption("inputName","queryPackageContentsRequest");
        queryPackageContents45Op.setOption("buildNum","cf20535.11");
        queryPackageContents45Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackageContents45Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list45.add(queryPackageContents45Op);

        java.util.List list46 = new java.util.ArrayList();
        inner0.put("queryPackageFileAcls", list46);

        com.ibm.ws.webservices.engine.description.OperationDesc queryPackageFileAcls46Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params46 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_45"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_45"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params46[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params46[0].setOption("partName","long");
        _params46[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params46[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc46 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageFileAclsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc46.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc46.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults46 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryPackageFileAcls46Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackageFileAcls", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageFileAcls"), _params46, _returnDesc46, _faults46, null);
        queryPackageFileAcls46Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryPackageFileAcls46Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackageFileAcls46Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryPackageFileAcls46Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageFileAclsResponse"));
        queryPackageFileAcls46Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageFileAclsRequest"));
        queryPackageFileAcls46Op.setOption("outputName","queryPackageFileAclsResponse");
        queryPackageFileAcls46Op.setOption("inputName","queryPackageFileAclsRequest");
        queryPackageFileAcls46Op.setOption("buildNum","cf20535.11");
        queryPackageFileAcls46Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackageFileAcls46Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list46.add(queryPackageFileAcls46Op);

        java.util.List list47 = new java.util.ArrayList();
        inner0.put("queryPackages", list47);

        com.ibm.ws.webservices.engine.description.OperationDesc queryPackages47Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params47 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_5_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params47[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params47[0].setOption("partName","string");
        _params47[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params47[1].setOption("partName","boolean");
        _params47[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params47[2].setOption("partName","boolean");
        _params47[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params47[3].setOption("partName","boolean");
        _params47[4].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params47[4].setOption("partName","boolean");
        _params47[5].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params47[5].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc47 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackagesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc47.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc47.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults47 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryPackages47Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackages", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackages"), _params47, _returnDesc47, _faults47, null);
        queryPackages47Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryPackages47Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackages47Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryPackages47Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackagesResponse"));
        queryPackages47Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackagesRequest"));
        queryPackages47Op.setOption("outputName","queryPackagesResponse");
        queryPackages47Op.setOption("inputName","queryPackagesRequest");
        queryPackages47Op.setOption("buildNum","cf20535.11");
        queryPackages47Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackages47Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list47.add(queryPackages47Op);

        com.ibm.ws.webservices.engine.description.OperationDesc queryPackages48Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params48 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_35"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_35"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_35"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_35"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params48[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params48[0].setOption("partName","boolean");
        _params48[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params48[1].setOption("partName","boolean");
        _params48[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params48[2].setOption("partName","boolean");
        _params48[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params48[3].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc48 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackagesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc48.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc48.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults48 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryPackages48Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackages", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackages"), _params48, _returnDesc48, _faults48, null);
        queryPackages48Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryPackages48Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackages48Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryPackages48Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackagesResponse1"));
        queryPackages48Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackagesRequest1"));
        queryPackages48Op.setOption("outputName","queryPackagesResponse1");
        queryPackages48Op.setOption("inputName","queryPackagesRequest1");
        queryPackages48Op.setOption("buildNum","cf20535.11");
        queryPackages48Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryPackages48Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list47.add(queryPackages48Op);

        java.util.List list49 = new java.util.ArrayList();
        inner0.put("queryRepresentedCompanies", list49);

        com.ibm.ws.webservices.engine.description.OperationDesc queryRepresentedCompanies49Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params49 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_43"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_43"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params49[0].setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _params49[0].setOption("partName","Vector");
        _params49[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params49[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc49 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryRepresentedCompaniesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc49.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc49.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults49 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryRepresentedCompanies49Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryRepresentedCompanies", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryRepresentedCompanies"), _params49, _returnDesc49, _faults49, null);
        queryRepresentedCompanies49Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryRepresentedCompanies49Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryRepresentedCompanies49Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryRepresentedCompanies49Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryRepresentedCompaniesResponse"));
        queryRepresentedCompanies49Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryRepresentedCompaniesRequest"));
        queryRepresentedCompanies49Op.setOption("outputName","queryRepresentedCompaniesResponse");
        queryRepresentedCompanies49Op.setOption("inputName","queryRepresentedCompaniesRequest");
        queryRepresentedCompanies49Op.setOption("buildNum","cf20535.11");
        queryRepresentedCompanies49Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryRepresentedCompanies49Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list49.add(queryRepresentedCompanies49Op);

        java.util.List list50 = new java.util.ArrayList();
        inner0.put("queryStoragePoolInformation", list50);

        com.ibm.ws.webservices.engine.description.OperationDesc queryStoragePoolInformation50Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params50 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc50 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryStoragePoolInformationReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc50.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc50.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults50 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        queryStoragePoolInformation50Op = new com.ibm.ws.webservices.engine.description.OperationDesc("queryStoragePoolInformation", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryStoragePoolInformation"), _params50, _returnDesc50, _faults50, null);
        queryStoragePoolInformation50Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        queryStoragePoolInformation50Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryStoragePoolInformation50Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        queryStoragePoolInformation50Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryStoragePoolInformationResponse"));
        queryStoragePoolInformation50Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryStoragePoolInformationRequest"));
        queryStoragePoolInformation50Op.setOption("outputName","queryStoragePoolInformationResponse");
        queryStoragePoolInformation50Op.setOption("inputName","queryStoragePoolInformationRequest");
        queryStoragePoolInformation50Op.setOption("buildNum","cf20535.11");
        queryStoragePoolInformation50Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        queryStoragePoolInformation50Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list50.add(queryStoragePoolInformation50Op);

        java.util.List list51 = new java.util.ArrayList();
        inner0.put("refreshSession", list51);

        com.ibm.ws.webservices.engine.description.OperationDesc refreshSession51Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params51 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc51 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "refreshSessionReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc51.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc51.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults51 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        refreshSession51Op = new com.ibm.ws.webservices.engine.description.OperationDesc("refreshSession", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "refreshSession"), _params51, _returnDesc51, _faults51, null);
        refreshSession51Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        refreshSession51Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        refreshSession51Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        refreshSession51Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "refreshSessionResponse"));
        refreshSession51Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "refreshSessionRequest"));
        refreshSession51Op.setOption("outputName","refreshSessionResponse");
        refreshSession51Op.setOption("inputName","refreshSessionRequest");
        refreshSession51Op.setOption("buildNum","cf20535.11");
        refreshSession51Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        refreshSession51Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list51.add(refreshSession51Op);

        java.util.List list52 = new java.util.ArrayList();
        inner0.put("registerAuditInformation", list52);

        com.ibm.ws.webservices.engine.description.OperationDesc registerAuditInformation52Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params52 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params52[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params52[0].setOption("partName","long");
        _params52[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params52[1].setOption("partName","long");
        _params52[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params52[2].setOption("partName","long");
        _params52[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params52[3].setOption("partName","long");
        _params52[4].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params52[4].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc52 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults52 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        registerAuditInformation52Op = new com.ibm.ws.webservices.engine.description.OperationDesc("registerAuditInformation", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "registerAuditInformation"), _params52, _returnDesc52, _faults52, null);
        registerAuditInformation52Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        registerAuditInformation52Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        registerAuditInformation52Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        registerAuditInformation52Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "registerAuditInformationResponse"));
        registerAuditInformation52Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "registerAuditInformationRequest"));
        registerAuditInformation52Op.setOption("outputName","registerAuditInformationResponse");
        registerAuditInformation52Op.setOption("inputName","registerAuditInformationRequest");
        registerAuditInformation52Op.setOption("buildNum","cf20535.11");
        registerAuditInformation52Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        registerAuditInformation52Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list52.add(registerAuditInformation52Op);

        java.util.List list53 = new java.util.ArrayList();
        inner0.put("releaseFileSlot", list53);

        com.ibm.ws.webservices.engine.description.OperationDesc releaseFileSlot53Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params53 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_53"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_53"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_53"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params53[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params53[0].setOption("partName","long");
        _params53[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params53[1].setOption("partName","long");
        _params53[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params53[2].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc53 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults53 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        releaseFileSlot53Op = new com.ibm.ws.webservices.engine.description.OperationDesc("releaseFileSlot", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "releaseFileSlot"), _params53, _returnDesc53, _faults53, null);
        releaseFileSlot53Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        releaseFileSlot53Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        releaseFileSlot53Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        releaseFileSlot53Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "releaseFileSlotResponse"));
        releaseFileSlot53Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "releaseFileSlotRequest"));
        releaseFileSlot53Op.setOption("outputName","releaseFileSlotResponse");
        releaseFileSlot53Op.setOption("inputName","releaseFileSlotRequest");
        releaseFileSlot53Op.setOption("buildNum","cf20535.11");
        releaseFileSlot53Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        releaseFileSlot53Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list53.add(releaseFileSlot53Op);

        java.util.List list54 = new java.util.ArrayList();
        inner0.put("removeFileSlot", list54);

        com.ibm.ws.webservices.engine.description.OperationDesc removeFileSlot54Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params54 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_52"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_52"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_52"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params54[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params54[0].setOption("partName","long");
        _params54[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params54[1].setOption("partName","long");
        _params54[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params54[2].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc54 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults54 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        removeFileSlot54Op = new com.ibm.ws.webservices.engine.description.OperationDesc("removeFileSlot", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeFileSlot"), _params54, _returnDesc54, _faults54, null);
        removeFileSlot54Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        removeFileSlot54Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeFileSlot54Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        removeFileSlot54Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeFileSlotResponse"));
        removeFileSlot54Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeFileSlotRequest"));
        removeFileSlot54Op.setOption("outputName","removeFileSlotResponse");
        removeFileSlot54Op.setOption("inputName","removeFileSlotRequest");
        removeFileSlot54Op.setOption("buildNum","cf20535.11");
        removeFileSlot54Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeFileSlot54Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list54.add(removeFileSlot54Op);

        java.util.List list55 = new java.util.ArrayList();
        inner0.put("removeGroupAcl", list55);

        com.ibm.ws.webservices.engine.description.OperationDesc removeGroupAcl55Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params55 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_21"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_21"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params55[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params55[0].setOption("partName","long");
        _params55[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params55[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc55 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults55 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        removeGroupAcl55Op = new com.ibm.ws.webservices.engine.description.OperationDesc("removeGroupAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAcl"), _params55, _returnDesc55, _faults55, null);
        removeGroupAcl55Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        removeGroupAcl55Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeGroupAcl55Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        removeGroupAcl55Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAclResponse"));
        removeGroupAcl55Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAclRequest"));
        removeGroupAcl55Op.setOption("outputName","removeGroupAclResponse");
        removeGroupAcl55Op.setOption("inputName","removeGroupAclRequest");
        removeGroupAcl55Op.setOption("buildNum","cf20535.11");
        removeGroupAcl55Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeGroupAcl55Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list55.add(removeGroupAcl55Op);

        com.ibm.ws.webservices.engine.description.OperationDesc removeGroupAcl56Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params56 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_22"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_22"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_22"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params56[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params56[0].setOption("partName","string");
        _params56[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params56[1].setOption("partName","string");
        _params56[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params56[2].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc56 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults56 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        removeGroupAcl56Op = new com.ibm.ws.webservices.engine.description.OperationDesc("removeGroupAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAcl"), _params56, _returnDesc56, _faults56, null);
        removeGroupAcl56Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        removeGroupAcl56Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeGroupAcl56Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        removeGroupAcl56Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAclResponse1"));
        removeGroupAcl56Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAclRequest1"));
        removeGroupAcl56Op.setOption("outputName","removeGroupAclResponse1");
        removeGroupAcl56Op.setOption("inputName","removeGroupAclRequest1");
        removeGroupAcl56Op.setOption("buildNum","cf20535.11");
        removeGroupAcl56Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeGroupAcl56Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list55.add(removeGroupAcl56Op);

        java.util.List list57 = new java.util.ArrayList();
        inner0.put("removeItemFromPackage", list57);

        com.ibm.ws.webservices.engine.description.OperationDesc removeItemFromPackage57Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params57 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_47"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_47"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params57[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params57[0].setOption("partName","long");
        _params57[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params57[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc57 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults57 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        removeItemFromPackage57Op = new com.ibm.ws.webservices.engine.description.OperationDesc("removeItemFromPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeItemFromPackage"), _params57, _returnDesc57, _faults57, null);
        removeItemFromPackage57Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        removeItemFromPackage57Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeItemFromPackage57Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        removeItemFromPackage57Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeItemFromPackageResponse"));
        removeItemFromPackage57Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeItemFromPackageRequest"));
        removeItemFromPackage57Op.setOption("outputName","removeItemFromPackageResponse");
        removeItemFromPackage57Op.setOption("inputName","removeItemFromPackageRequest");
        removeItemFromPackage57Op.setOption("buildNum","cf20535.11");
        removeItemFromPackage57Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeItemFromPackage57Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list57.add(removeItemFromPackage57Op);

        java.util.List list58 = new java.util.ArrayList();
        inner0.put("removePackageAcl", list58);

        com.ibm.ws.webservices.engine.description.OperationDesc removePackageAcl58Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params58 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_15"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_15"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_15"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
          };
        _params58[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params58[0].setOption("partName","long");
        _params58[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params58[1].setOption("partName","string");
        _params58[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params58[2].setOption("partName","byte");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc58 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults58 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        removePackageAcl58Op = new com.ibm.ws.webservices.engine.description.OperationDesc("removePackageAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removePackageAcl"), _params58, _returnDesc58, _faults58, null);
        removePackageAcl58Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        removePackageAcl58Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removePackageAcl58Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        removePackageAcl58Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removePackageAclResponse"));
        removePackageAcl58Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removePackageAclRequest"));
        removePackageAcl58Op.setOption("outputName","removePackageAclResponse");
        removePackageAcl58Op.setOption("inputName","removePackageAclRequest");
        removePackageAcl58Op.setOption("buildNum","cf20535.11");
        removePackageAcl58Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removePackageAcl58Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list58.add(removePackageAcl58Op);

        java.util.List list59 = new java.util.ArrayList();
        inner0.put("removeProjectAcl", list59);

        com.ibm.ws.webservices.engine.description.OperationDesc removeProjectAcl59Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params59 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_23"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_23"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params59[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params59[0].setOption("partName","long");
        _params59[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params59[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc59 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults59 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        removeProjectAcl59Op = new com.ibm.ws.webservices.engine.description.OperationDesc("removeProjectAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeProjectAcl"), _params59, _returnDesc59, _faults59, null);
        removeProjectAcl59Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        removeProjectAcl59Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeProjectAcl59Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        removeProjectAcl59Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeProjectAclResponse"));
        removeProjectAcl59Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeProjectAclRequest"));
        removeProjectAcl59Op.setOption("outputName","removeProjectAclResponse");
        removeProjectAcl59Op.setOption("inputName","removeProjectAclRequest");
        removeProjectAcl59Op.setOption("buildNum","cf20535.11");
        removeProjectAcl59Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeProjectAcl59Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list59.add(removeProjectAcl59Op);

        java.util.List list60 = new java.util.ArrayList();
        inner0.put("removeUserAcl", list60);

        com.ibm.ws.webservices.engine.description.OperationDesc removeUserAcl60Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params60 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_20"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_20"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params60[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params60[0].setOption("partName","long");
        _params60[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params60[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc60 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults60 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        removeUserAcl60Op = new com.ibm.ws.webservices.engine.description.OperationDesc("removeUserAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeUserAcl"), _params60, _returnDesc60, _faults60, null);
        removeUserAcl60Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        removeUserAcl60Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeUserAcl60Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        removeUserAcl60Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeUserAclResponse"));
        removeUserAcl60Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeUserAclRequest"));
        removeUserAcl60Op.setOption("outputName","removeUserAclResponse");
        removeUserAcl60Op.setOption("inputName","removeUserAclRequest");
        removeUserAcl60Op.setOption("buildNum","cf20535.11");
        removeUserAcl60Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        removeUserAcl60Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list60.add(removeUserAcl60Op);

        java.util.List list61 = new java.util.ArrayList();
        inner0.put("setOption", list61);

        com.ibm.ws.webservices.engine.description.OperationDesc setOption61Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params61 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_32"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_32"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params61[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params61[0].setOption("partName","string");
        _params61[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params61[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc61 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults61 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        setOption61Op = new com.ibm.ws.webservices.engine.description.OperationDesc("setOption", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOption"), _params61, _returnDesc61, _faults61, null);
        setOption61Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        setOption61Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        setOption61Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        setOption61Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptionResponse"));
        setOption61Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptionRequest"));
        setOption61Op.setOption("outputName","setOptionResponse");
        setOption61Op.setOption("inputName","setOptionRequest");
        setOption61Op.setOption("buildNum","cf20535.11");
        setOption61Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        setOption61Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list61.add(setOption61Op);

        java.util.List list62 = new java.util.ArrayList();
        inner0.put("setOptions", list62);

        com.ibm.ws.webservices.engine.description.OperationDesc setOptions62Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params62 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_31"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false, false, false, true, false), 
          };
        _params62[0].setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _params62[0].setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc62 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults62 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        setOptions62Op = new com.ibm.ws.webservices.engine.description.OperationDesc("setOptions", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptions"), _params62, _returnDesc62, _faults62, null);
        setOptions62Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        setOptions62Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        setOptions62Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        setOptions62Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptionsResponse"));
        setOptions62Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptionsRequest"));
        setOptions62Op.setOption("outputName","setOptionsResponse");
        setOptions62Op.setOption("inputName","setOptionsRequest");
        setOptions62Op.setOption("buildNum","cf20535.11");
        setOptions62Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        setOptions62Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list62.add(setOptions62Op);

        java.util.List list63 = new java.util.ArrayList();
        inner0.put("setPackageDescription", list63);

        com.ibm.ws.webservices.engine.description.OperationDesc setPackageDescription63Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params63 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_27"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_27"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params63[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params63[0].setOption("partName","long");
        _params63[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params63[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc63 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults63 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        setPackageDescription63Op = new com.ibm.ws.webservices.engine.description.OperationDesc("setPackageDescription", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageDescription"), _params63, _returnDesc63, _faults63, null);
        setPackageDescription63Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        setPackageDescription63Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        setPackageDescription63Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        setPackageDescription63Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageDescriptionResponse"));
        setPackageDescription63Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageDescriptionRequest"));
        setPackageDescription63Op.setOption("outputName","setPackageDescriptionResponse");
        setPackageDescription63Op.setOption("inputName","setPackageDescriptionRequest");
        setPackageDescription63Op.setOption("buildNum","cf20535.11");
        setPackageDescription63Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        setPackageDescription63Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list63.add(setPackageDescription63Op);

        java.util.List list64 = new java.util.ArrayList();
        inner0.put("setPackageFlags", list64);

        com.ibm.ws.webservices.engine.description.OperationDesc setPackageFlags64Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params64 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_9"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_9"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_9"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params64[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params64[0].setOption("partName","long");
        _params64[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params64[1].setOption("partName","int");
        _params64[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params64[2].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc64 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults64 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        setPackageFlags64Op = new com.ibm.ws.webservices.engine.description.OperationDesc("setPackageFlags", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageFlags"), _params64, _returnDesc64, _faults64, null);
        setPackageFlags64Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        setPackageFlags64Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        setPackageFlags64Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        setPackageFlags64Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageFlagsResponse"));
        setPackageFlags64Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageFlagsRequest"));
        setPackageFlags64Op.setOption("outputName","setPackageFlagsResponse");
        setPackageFlags64Op.setOption("inputName","setPackageFlagsRequest");
        setPackageFlags64Op.setOption("buildNum","cf20535.11");
        setPackageFlags64Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        setPackageFlags64Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list64.add(setPackageFlags64Op);

        java.util.List list65 = new java.util.ArrayList();
        inner0.put("uploadFileSlotToPackage", list65);

        com.ibm.ws.webservices.engine.description.OperationDesc uploadFileSlotToPackage65Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params65 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://schemas.xmlsoap.org/soap/encoding/", "base64"), byte[].class, false, false, false, false, true, false), 
          };
        _params65[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params65[0].setOption("partName","long");
        _params65[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params65[1].setOption("partName","long");
        _params65[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params65[2].setOption("partName","long");
        _params65[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params65[3].setOption("partName","boolean");
        _params65[4].setOption("partQNameString","{http://schemas.xmlsoap.org/soap/encoding/}base64");
        _params65[4].setOption("partName","base64");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc65 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "uploadFileSlotToPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileSlot"), oem.edge.ed.odc.dropbox.common.FileSlot.class, true, false, false, false, true, false); 
        _returnDesc65.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}FileSlot");
        _returnDesc65.setOption("partName","FileSlot");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults65 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        uploadFileSlotToPackage65Op = new com.ibm.ws.webservices.engine.description.OperationDesc("uploadFileSlotToPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileSlotToPackage"), _params65, _returnDesc65, _faults65, null);
        uploadFileSlotToPackage65Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        uploadFileSlotToPackage65Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        uploadFileSlotToPackage65Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        uploadFileSlotToPackage65Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileSlotToPackageResponse"));
        uploadFileSlotToPackage65Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileSlotToPackageRequest"));
        uploadFileSlotToPackage65Op.setOption("outputName","uploadFileSlotToPackageResponse");
        uploadFileSlotToPackage65Op.setOption("inputName","uploadFileSlotToPackageRequest");
        uploadFileSlotToPackage65Op.setOption("buildNum","cf20535.11");
        uploadFileSlotToPackage65Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        uploadFileSlotToPackage65Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list65.add(uploadFileSlotToPackage65Op);

        java.util.List list66 = new java.util.ArrayList();
        inner0.put("uploadFileToPackage", list66);

        com.ibm.ws.webservices.engine.description.OperationDesc uploadFileToPackage66Op = null;
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params66 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_48"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_48"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_48"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params66[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params66[0].setOption("partName","long");
        _params66[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params66[1].setOption("partName","string");
        _params66[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params66[2].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc66 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "uploadFileToPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc66.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc66.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults66 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        uploadFileToPackage66Op = new com.ibm.ws.webservices.engine.description.OperationDesc("uploadFileToPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileToPackage"), _params66, _returnDesc66, _faults66, null);
        uploadFileToPackage66Op.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        uploadFileToPackage66Op.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        uploadFileToPackage66Op.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        uploadFileToPackage66Op.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileToPackageResponse"));
        uploadFileToPackage66Op.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileToPackageRequest"));
        uploadFileToPackage66Op.setOption("outputName","uploadFileToPackageResponse");
        uploadFileToPackage66Op.setOption("inputName","uploadFileToPackageRequest");
        uploadFileToPackage66Op.setOption("buildNum","cf20535.11");
        uploadFileToPackage66Op.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        uploadFileToPackage66Op.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
        list66.add(uploadFileToPackage66Op);

        operationDescriptions.put("DropboxAccessWebSvc",inner0);
        operationDescriptions = java.util.Collections.unmodifiableMap(operationDescriptions);
    }

    private static void initTypeMappings() {
        typeMappings = new java.util.HashMap();
        typeMappings.put(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "GroupInfo"),
                         oem.edge.ed.odc.dsmp.common.GroupInfo.class);

        typeMappings.put(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"),
                         oem.edge.ed.odc.dsmp.common.DboxException.class);

        typeMappings.put(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "PoolInfo"),
                         oem.edge.ed.odc.dropbox.common.PoolInfo.class);

        typeMappings.put(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileInfo"),
                         oem.edge.ed.odc.dropbox.common.FileInfo.class);

        typeMappings.put(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileSlot"),
                         oem.edge.ed.odc.dropbox.common.FileSlot.class);

        typeMappings.put(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "ArrayOf_xsd_nillable_string"),
                         java.lang.String[].class);

        typeMappings.put(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "PackageInfo"),
                         oem.edge.ed.odc.dropbox.common.PackageInfo.class);

        typeMappings.put(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "AclInfo"),
                         oem.edge.ed.odc.dropbox.common.AclInfo.class);

        typeMappings = java.util.Collections.unmodifiableMap(typeMappings);
    }

    public java.util.Map getTypeMappings() {
        return typeMappings;
    }

    public Class getJavaType(javax.xml.namespace.QName xmlName) {
        return (Class) typeMappings.get(xmlName);
    }

    public java.util.Map getOperationDescriptions(String portName) {
        return (java.util.Map) operationDescriptions.get(portName);
    }

    public java.util.List getOperationDescriptions(String portName, String operationName) {
        java.util.Map map = (java.util.Map) operationDescriptions.get(portName);
        if (map != null) {
            return (java.util.List) map.get(operationName);
        }
        return null;
    }

}
