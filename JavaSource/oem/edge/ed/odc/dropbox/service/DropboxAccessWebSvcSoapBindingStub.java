/**
 * DropboxAccessWebSvcSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf20535.11 v91905174159
 */

package oem.edge.ed.odc.dropbox.service;

public class DropboxAccessWebSvcSoapBindingStub extends com.ibm.ws.webservices.engine.client.Stub implements oem.edge.ed.odc.dropbox.service.DropboxAccess {

   // JMC - Auto added to generated STUB
    public javax.xml.rpc.Service _getService() {
       return super.service;
    }

    public DropboxAccessWebSvcSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws com.ibm.ws.webservices.engine.WebServicesFault {
        if (service == null) {
            super.service = new com.ibm.ws.webservices.engine.client.Service();
        }
        else {
            super.service = service;
        }
        super.engine = ((com.ibm.ws.webservices.engine.client.Service) super.service).getEngine();
        initTypeMapping();
        super.cachedEndpoint = endpointURL;
        super.connection = ((com.ibm.ws.webservices.engine.client.Service) super.service).getConnection(endpointURL);
        super.messageContexts = new com.ibm.ws.webservices.engine.MessageContext[67];
    }

    private void initTypeMapping() {
        javax.xml.rpc.encoding.TypeMapping _tm = super.getTypeMapping(com.ibm.ws.webservices.engine.Constants.URI_SOAP11_ENC);
        java.lang.Class _javaType = null;
        javax.xml.namespace.QName _xmlType = null;
        javax.xml.namespace.QName _compQName = null;
        javax.xml.namespace.QName _compTypeQName = null;
        com.ibm.ws.webservices.engine.encoding.SerializerFactory _sf = null;
        com.ibm.ws.webservices.engine.encoding.DeserializerFactory _df = null;
        _javaType = oem.edge.ed.odc.dropbox.common.AclInfo.class;
        _xmlType = com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "AclInfo");
        _sf = com.ibm.ws.webservices.engine.encoding.ser.BaseSerializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanSerializerFactory.class, _javaType, _xmlType);
        _df = com.ibm.ws.webservices.engine.encoding.ser.BaseDeserializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializerFactory.class, _javaType, _xmlType);
        if (_sf != null || _df != null) {
            _tm.register(_javaType, _xmlType, _sf, _df);
        }

        _javaType = oem.edge.ed.odc.dropbox.common.FileInfo.class;
        _xmlType = com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileInfo");
        _sf = com.ibm.ws.webservices.engine.encoding.ser.BaseSerializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanSerializerFactory.class, _javaType, _xmlType);
        _df = com.ibm.ws.webservices.engine.encoding.ser.BaseDeserializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializerFactory.class, _javaType, _xmlType);
        if (_sf != null || _df != null) {
            _tm.register(_javaType, _xmlType, _sf, _df);
        }

        _javaType = oem.edge.ed.odc.dropbox.common.FileSlot.class;
        _xmlType = com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileSlot");
        _sf = com.ibm.ws.webservices.engine.encoding.ser.BaseSerializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanSerializerFactory.class, _javaType, _xmlType);
        _df = com.ibm.ws.webservices.engine.encoding.ser.BaseDeserializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializerFactory.class, _javaType, _xmlType);
        if (_sf != null || _df != null) {
            _tm.register(_javaType, _xmlType, _sf, _df);
        }

        _javaType = oem.edge.ed.odc.dropbox.common.PackageInfo.class;
        _xmlType = com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "PackageInfo");
        _sf = com.ibm.ws.webservices.engine.encoding.ser.BaseSerializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanSerializerFactory.class, _javaType, _xmlType);
        _df = com.ibm.ws.webservices.engine.encoding.ser.BaseDeserializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializerFactory.class, _javaType, _xmlType);
        if (_sf != null || _df != null) {
            _tm.register(_javaType, _xmlType, _sf, _df);
        }

        _javaType = oem.edge.ed.odc.dropbox.common.PoolInfo.class;
        _xmlType = com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "PoolInfo");
        _sf = com.ibm.ws.webservices.engine.encoding.ser.BaseSerializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanSerializerFactory.class, _javaType, _xmlType);
        _df = com.ibm.ws.webservices.engine.encoding.ser.BaseDeserializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializerFactory.class, _javaType, _xmlType);
        if (_sf != null || _df != null) {
            _tm.register(_javaType, _xmlType, _sf, _df);
        }

        _javaType = oem.edge.ed.odc.dsmp.common.DboxException.class;
        _xmlType = com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException");
        _sf = com.ibm.ws.webservices.engine.encoding.ser.BaseSerializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanSerializerFactory.class, _javaType, _xmlType);
        _df = com.ibm.ws.webservices.engine.encoding.ser.BaseDeserializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializerFactory.class, _javaType, _xmlType);
        if (_sf != null || _df != null) {
            _tm.register(_javaType, _xmlType, _sf, _df);
        }

        _javaType = oem.edge.ed.odc.dsmp.common.GroupInfo.class;
        _xmlType = com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "GroupInfo");
        _sf = com.ibm.ws.webservices.engine.encoding.ser.BaseSerializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanSerializerFactory.class, _javaType, _xmlType);
        _df = com.ibm.ws.webservices.engine.encoding.ser.BaseDeserializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.BeanDeserializerFactory.class, _javaType, _xmlType);
        if (_sf != null || _df != null) {
            _tm.register(_javaType, _xmlType, _sf, _df);
        }

        _javaType = java.lang.String[].class;
        _xmlType = com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "ArrayOf_xsd_nillable_string");
        _compTypeQName = com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string");
        _sf = com.ibm.ws.webservices.engine.encoding.ser.BaseSerializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.ArraySerializerFactory.class, _javaType, _xmlType, null, _compTypeQName);
        _df = com.ibm.ws.webservices.engine.encoding.ser.BaseDeserializerFactory.createFactory(com.ibm.ws.webservices.engine.encoding.ser.ArrayDeserializerFactory.class, _javaType, _xmlType, null, _compTypeQName);
        if (_sf != null || _df != null) {
            _tm.register(_javaType, _xmlType, _sf, _df);
        }

    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _createSessionOperation0;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params0 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_0"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params0[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params0[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc0 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createSessionReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc0.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc0.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults0 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _createSessionOperation0 = new com.ibm.ws.webservices.engine.description.OperationDesc("createSession", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSession"), _params0, _returnDesc0, _faults0, "");
        _createSessionOperation0.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _createSessionOperation0.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createSessionOperation0.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _createSessionOperation0.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSessionResponse"));
        _createSessionOperation0.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSessionRequest"));
        _createSessionOperation0.setOption("outputName","createSessionResponse");
        _createSessionOperation0.setOption("inputName","createSessionRequest");
        _createSessionOperation0.setOption("buildNum","cf20535.11");
        _createSessionOperation0.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createSessionOperation0.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _createSessionOperation0.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _createSessionIndex0 = 0;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcreateSessionInvoke0(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_createSessionIndex0];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._createSessionOperation0);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_createSessionIndex0] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.HashMap createSession(java.lang.String arg_0_0) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getcreateSessionInvoke0(new java.lang.Object[] {arg_0_0}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.HashMap) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.HashMap) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.HashMap.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _createSessionOperation1;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params1 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_1"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_1"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params1[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params1[0].setOption("partName","string");
        _params1[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params1[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc1 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createSessionReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc1.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc1.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults1 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _createSessionOperation1 = new com.ibm.ws.webservices.engine.description.OperationDesc("createSession", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSession"), _params1, _returnDesc1, _faults1, "");
        _createSessionOperation1.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _createSessionOperation1.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createSessionOperation1.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _createSessionOperation1.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSessionResponse1"));
        _createSessionOperation1.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createSessionRequest1"));
        _createSessionOperation1.setOption("outputName","createSessionResponse1");
        _createSessionOperation1.setOption("inputName","createSessionRequest1");
        _createSessionOperation1.setOption("buildNum","cf20535.11");
        _createSessionOperation1.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createSessionOperation1.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _createSessionOperation1.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _createSessionIndex1 = 1;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcreateSessionInvoke1(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_createSessionIndex1];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._createSessionOperation1);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_createSessionIndex1] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.HashMap createSession(java.lang.String arg_0_1, java.lang.String arg_1_1) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getcreateSessionInvoke1(new java.lang.Object[] {arg_0_1, arg_1_1}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.HashMap) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.HashMap) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.HashMap.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _refreshSessionOperation2;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params2 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc2 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "refreshSessionReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc2.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc2.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults2 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _refreshSessionOperation2 = new com.ibm.ws.webservices.engine.description.OperationDesc("refreshSession", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "refreshSession"), _params2, _returnDesc2, _faults2, "");
        _refreshSessionOperation2.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _refreshSessionOperation2.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _refreshSessionOperation2.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _refreshSessionOperation2.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "refreshSessionResponse"));
        _refreshSessionOperation2.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "refreshSessionRequest"));
        _refreshSessionOperation2.setOption("outputName","refreshSessionResponse");
        _refreshSessionOperation2.setOption("inputName","refreshSessionRequest");
        _refreshSessionOperation2.setOption("buildNum","cf20535.11");
        _refreshSessionOperation2.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _refreshSessionOperation2.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _refreshSessionOperation2.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _refreshSessionIndex2 = 2;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getrefreshSessionInvoke2(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_refreshSessionIndex2];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._refreshSessionOperation2);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_refreshSessionIndex2] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.HashMap refreshSession() throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getrefreshSessionInvoke2(new java.lang.Object[] {}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.HashMap) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.HashMap) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.HashMap.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _closeSessionOperation3;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params3 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc3 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults3 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _closeSessionOperation3 = new com.ibm.ws.webservices.engine.description.OperationDesc("closeSession", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "closeSession"), _params3, _returnDesc3, _faults3, "");
        _closeSessionOperation3.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _closeSessionOperation3.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _closeSessionOperation3.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _closeSessionOperation3.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "closeSessionResponse"));
        _closeSessionOperation3.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "closeSessionRequest"));
        _closeSessionOperation3.setOption("outputName","closeSessionResponse");
        _closeSessionOperation3.setOption("inputName","closeSessionRequest");
        _closeSessionOperation3.setOption("buildNum","cf20535.11");
        _closeSessionOperation3.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _closeSessionOperation3.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _closeSessionOperation3.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _closeSessionIndex3 = 3;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcloseSessionInvoke3(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_closeSessionIndex3];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._closeSessionOperation3);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_closeSessionIndex3] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void closeSession() throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getcloseSessionInvoke3(new java.lang.Object[] {}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _createPackageOperation4;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params4 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_5_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_6_4"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params4[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params4[0].setOption("partName","string");
        _params4[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params4[1].setOption("partName","string");
        _params4[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params4[2].setOption("partName","long");
        _params4[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params4[3].setOption("partName","long");
        _params4[4].setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _params4[4].setOption("partName","Vector");
        _params4[5].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params4[5].setOption("partName","int");
        _params4[6].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params4[6].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc4 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc4.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc4.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults4 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _createPackageOperation4 = new com.ibm.ws.webservices.engine.description.OperationDesc("createPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackage"), _params4, _returnDesc4, _faults4, "");
        _createPackageOperation4.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _createPackageOperation4.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createPackageOperation4.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _createPackageOperation4.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageResponse"));
        _createPackageOperation4.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageRequest"));
        _createPackageOperation4.setOption("outputName","createPackageResponse");
        _createPackageOperation4.setOption("inputName","createPackageRequest");
        _createPackageOperation4.setOption("buildNum","cf20535.11");
        _createPackageOperation4.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createPackageOperation4.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _createPackageOperation4.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _createPackageIndex4 = 4;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcreatePackageInvoke4(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_createPackageIndex4];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._createPackageOperation4);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_createPackageIndex4] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public long createPackage(java.lang.String arg_0_4, java.lang.String arg_1_4, long arg_2_4, long arg_3_4, java.util.Vector arg_4_4, int arg_5_4, int arg_6_4) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getcreatePackageInvoke4(new java.lang.Object[] {arg_0_4, arg_1_4, new java.lang.Long(arg_2_4), new java.lang.Long(arg_3_4), arg_4_4, new java.lang.Integer(arg_5_4), new java.lang.Integer(arg_6_4)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return ((java.lang.Long) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue()).longValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Long) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), long.class)).longValue();
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _createPackageOperation5;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params5 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_5_5"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params5[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params5[0].setOption("partName","string");
        _params5[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params5[1].setOption("partName","string");
        _params5[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params5[2].setOption("partName","long");
        _params5[3].setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _params5[3].setOption("partName","Vector");
        _params5[4].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params5[4].setOption("partName","int");
        _params5[5].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params5[5].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc5 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc5.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc5.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults5 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _createPackageOperation5 = new com.ibm.ws.webservices.engine.description.OperationDesc("createPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackage"), _params5, _returnDesc5, _faults5, "");
        _createPackageOperation5.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _createPackageOperation5.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createPackageOperation5.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _createPackageOperation5.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageResponse1"));
        _createPackageOperation5.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageRequest1"));
        _createPackageOperation5.setOption("outputName","createPackageResponse1");
        _createPackageOperation5.setOption("inputName","createPackageRequest1");
        _createPackageOperation5.setOption("buildNum","cf20535.11");
        _createPackageOperation5.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createPackageOperation5.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _createPackageOperation5.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _createPackageIndex5 = 5;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcreatePackageInvoke5(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_createPackageIndex5];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._createPackageOperation5);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_createPackageIndex5] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public long createPackage(java.lang.String arg_0_5, java.lang.String arg_1_5, long arg_2_5, java.util.Vector arg_3_5, int arg_4_5, int arg_5_5) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getcreatePackageInvoke5(new java.lang.Object[] {arg_0_5, arg_1_5, new java.lang.Long(arg_2_5), arg_3_5, new java.lang.Integer(arg_4_5), new java.lang.Integer(arg_5_5)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return ((java.lang.Long) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue()).longValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Long) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), long.class)).longValue();
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _createPackageOperation6;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params6 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_6"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params6[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params6[0].setOption("partName","string");
        _params6[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params6[1].setOption("partName","string");
        _params6[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params6[2].setOption("partName","long");
        _params6[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params6[3].setOption("partName","int");
        _params6[4].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params6[4].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc6 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc6.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc6.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults6 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _createPackageOperation6 = new com.ibm.ws.webservices.engine.description.OperationDesc("createPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackage"), _params6, _returnDesc6, _faults6, "");
        _createPackageOperation6.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _createPackageOperation6.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createPackageOperation6.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _createPackageOperation6.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageResponse2"));
        _createPackageOperation6.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageRequest2"));
        _createPackageOperation6.setOption("outputName","createPackageResponse2");
        _createPackageOperation6.setOption("inputName","createPackageRequest2");
        _createPackageOperation6.setOption("buildNum","cf20535.11");
        _createPackageOperation6.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createPackageOperation6.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _createPackageOperation6.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _createPackageIndex6 = 6;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcreatePackageInvoke6(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_createPackageIndex6];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._createPackageOperation6);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_createPackageIndex6] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public long createPackage(java.lang.String arg_0_6, java.lang.String arg_1_6, long arg_2_6, int arg_3_6, int arg_4_6) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getcreatePackageInvoke6(new java.lang.Object[] {arg_0_6, arg_1_6, new java.lang.Long(arg_2_6), new java.lang.Integer(arg_3_6), new java.lang.Integer(arg_4_6)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return ((java.lang.Long) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue()).longValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Long) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), long.class)).longValue();
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _createPackageOperation7;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params7 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_7"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params7[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params7[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc7 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "createPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc7.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc7.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults7 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _createPackageOperation7 = new com.ibm.ws.webservices.engine.description.OperationDesc("createPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackage"), _params7, _returnDesc7, _faults7, "");
        _createPackageOperation7.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _createPackageOperation7.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createPackageOperation7.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _createPackageOperation7.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageResponse3"));
        _createPackageOperation7.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createPackageRequest3"));
        _createPackageOperation7.setOption("outputName","createPackageResponse3");
        _createPackageOperation7.setOption("inputName","createPackageRequest3");
        _createPackageOperation7.setOption("buildNum","cf20535.11");
        _createPackageOperation7.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createPackageOperation7.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _createPackageOperation7.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _createPackageIndex7 = 7;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcreatePackageInvoke7(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_createPackageIndex7];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._createPackageOperation7);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_createPackageIndex7] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public long createPackage(java.lang.String arg_0_7) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getcreatePackageInvoke7(new java.lang.Object[] {arg_0_7}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return ((java.lang.Long) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue()).longValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Long) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), long.class)).longValue();
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _getLoginMessageOperation8;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params8 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc8 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getLoginMessageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, true, false, false, false, true, false); 
        _returnDesc8.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _returnDesc8.setOption("partName","string");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults8 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _getLoginMessageOperation8 = new com.ibm.ws.webservices.engine.description.OperationDesc("getLoginMessage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getLoginMessage"), _params8, _returnDesc8, _faults8, "");
        _getLoginMessageOperation8.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _getLoginMessageOperation8.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getLoginMessageOperation8.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _getLoginMessageOperation8.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getLoginMessageResponse"));
        _getLoginMessageOperation8.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getLoginMessageRequest"));
        _getLoginMessageOperation8.setOption("outputName","getLoginMessageResponse");
        _getLoginMessageOperation8.setOption("inputName","getLoginMessageRequest");
        _getLoginMessageOperation8.setOption("buildNum","cf20535.11");
        _getLoginMessageOperation8.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getLoginMessageOperation8.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _getLoginMessageOperation8.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _getLoginMessageIndex8 = 8;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getgetLoginMessageInvoke8(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_getLoginMessageIndex8];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._getLoginMessageOperation8);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_getLoginMessageIndex8] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.lang.String getLoginMessage() throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getgetLoginMessageInvoke8(new java.lang.Object[] {}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.lang.String) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.lang.String) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.lang.String.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _setPackageFlagsOperation9;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params9 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_9"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_9"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_9"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params9[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params9[0].setOption("partName","long");
        _params9[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params9[1].setOption("partName","int");
        _params9[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params9[2].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc9 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults9 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _setPackageFlagsOperation9 = new com.ibm.ws.webservices.engine.description.OperationDesc("setPackageFlags", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageFlags"), _params9, _returnDesc9, _faults9, "");
        _setPackageFlagsOperation9.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _setPackageFlagsOperation9.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _setPackageFlagsOperation9.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _setPackageFlagsOperation9.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageFlagsResponse"));
        _setPackageFlagsOperation9.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageFlagsRequest"));
        _setPackageFlagsOperation9.setOption("outputName","setPackageFlagsResponse");
        _setPackageFlagsOperation9.setOption("inputName","setPackageFlagsRequest");
        _setPackageFlagsOperation9.setOption("buildNum","cf20535.11");
        _setPackageFlagsOperation9.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _setPackageFlagsOperation9.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _setPackageFlagsOperation9.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _setPackageFlagsIndex9 = 9;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getsetPackageFlagsInvoke9(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_setPackageFlagsIndex9];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._setPackageFlagsOperation9);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_setPackageFlagsIndex9] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void setPackageFlags(long arg_0_9, int arg_1_9, int arg_2_9) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getsetPackageFlagsInvoke9(new java.lang.Object[] {new java.lang.Long(arg_0_9), new java.lang.Integer(arg_1_9), new java.lang.Integer(arg_2_9)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _deletePackageOperation10;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params10 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_10"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params10[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params10[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc10 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults10 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _deletePackageOperation10 = new com.ibm.ws.webservices.engine.description.OperationDesc("deletePackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deletePackage"), _params10, _returnDesc10, _faults10, "");
        _deletePackageOperation10.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _deletePackageOperation10.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _deletePackageOperation10.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _deletePackageOperation10.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deletePackageResponse"));
        _deletePackageOperation10.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deletePackageRequest"));
        _deletePackageOperation10.setOption("outputName","deletePackageResponse");
        _deletePackageOperation10.setOption("inputName","deletePackageRequest");
        _deletePackageOperation10.setOption("buildNum","cf20535.11");
        _deletePackageOperation10.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _deletePackageOperation10.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _deletePackageOperation10.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _deletePackageIndex10 = 10;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getdeletePackageInvoke10(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_deletePackageIndex10];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._deletePackageOperation10);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_deletePackageIndex10] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void deletePackage(long arg_0_10) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getdeletePackageInvoke10(new java.lang.Object[] {new java.lang.Long(arg_0_10)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _commitPackageOperation11;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params11 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_11"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params11[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params11[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc11 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults11 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _commitPackageOperation11 = new com.ibm.ws.webservices.engine.description.OperationDesc("commitPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitPackage"), _params11, _returnDesc11, _faults11, "");
        _commitPackageOperation11.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _commitPackageOperation11.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _commitPackageOperation11.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _commitPackageOperation11.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitPackageResponse"));
        _commitPackageOperation11.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitPackageRequest"));
        _commitPackageOperation11.setOption("outputName","commitPackageResponse");
        _commitPackageOperation11.setOption("inputName","commitPackageRequest");
        _commitPackageOperation11.setOption("buildNum","cf20535.11");
        _commitPackageOperation11.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _commitPackageOperation11.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _commitPackageOperation11.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _commitPackageIndex11 = 11;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcommitPackageInvoke11(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_commitPackageIndex11];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._commitPackageOperation11);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_commitPackageIndex11] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void commitPackage(long arg_0_11) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getcommitPackageInvoke11(new java.lang.Object[] {new java.lang.Long(arg_0_11)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _markPackageOperation12;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params12 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_12"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_12"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params12[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params12[0].setOption("partName","long");
        _params12[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params12[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc12 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults12 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _markPackageOperation12 = new com.ibm.ws.webservices.engine.description.OperationDesc("markPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "markPackage"), _params12, _returnDesc12, _faults12, "");
        _markPackageOperation12.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _markPackageOperation12.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _markPackageOperation12.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _markPackageOperation12.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "markPackageResponse"));
        _markPackageOperation12.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "markPackageRequest"));
        _markPackageOperation12.setOption("outputName","markPackageResponse");
        _markPackageOperation12.setOption("inputName","markPackageRequest");
        _markPackageOperation12.setOption("buildNum","cf20535.11");
        _markPackageOperation12.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _markPackageOperation12.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _markPackageOperation12.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _markPackageIndex12 = 12;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getmarkPackageInvoke12(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_markPackageIndex12];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._markPackageOperation12);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_markPackageIndex12] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void markPackage(long arg_0_12, boolean arg_1_12) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getmarkPackageInvoke12(new java.lang.Object[] {new java.lang.Long(arg_0_12), new java.lang.Boolean(arg_1_12)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _addPackageAclOperation13;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params13 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_13"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_13"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_13"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
          };
        _params13[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params13[0].setOption("partName","long");
        _params13[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params13[1].setOption("partName","string");
        _params13[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params13[2].setOption("partName","byte");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc13 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults13 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _addPackageAclOperation13 = new com.ibm.ws.webservices.engine.description.OperationDesc("addPackageAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAcl"), _params13, _returnDesc13, _faults13, "");
        _addPackageAclOperation13.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _addPackageAclOperation13.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addPackageAclOperation13.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _addPackageAclOperation13.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAclResponse"));
        _addPackageAclOperation13.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAclRequest"));
        _addPackageAclOperation13.setOption("outputName","addPackageAclResponse");
        _addPackageAclOperation13.setOption("inputName","addPackageAclRequest");
        _addPackageAclOperation13.setOption("buildNum","cf20535.11");
        _addPackageAclOperation13.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addPackageAclOperation13.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _addPackageAclOperation13.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _addPackageAclIndex13 = 13;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getaddPackageAclInvoke13(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_addPackageAclIndex13];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._addPackageAclOperation13);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_addPackageAclIndex13] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void addPackageAcl(long arg_0_13, java.lang.String arg_1_13, byte arg_2_13) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getaddPackageAclInvoke13(new java.lang.Object[] {new java.lang.Long(arg_0_13), arg_1_13, new java.lang.Byte(arg_2_13)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _addPackageAclOperation14;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params14 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_14"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_14"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "AclInfo"), oem.edge.ed.odc.dropbox.common.AclInfo.class, false, false, false, false, true, false), 
          };
        _params14[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params14[0].setOption("partName","long");
        _params14[1].setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}AclInfo");
        _params14[1].setOption("partName","AclInfo");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc14 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults14 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _addPackageAclOperation14 = new com.ibm.ws.webservices.engine.description.OperationDesc("addPackageAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAcl"), _params14, _returnDesc14, _faults14, "");
        _addPackageAclOperation14.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _addPackageAclOperation14.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addPackageAclOperation14.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _addPackageAclOperation14.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAclResponse1"));
        _addPackageAclOperation14.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addPackageAclRequest1"));
        _addPackageAclOperation14.setOption("outputName","addPackageAclResponse1");
        _addPackageAclOperation14.setOption("inputName","addPackageAclRequest1");
        _addPackageAclOperation14.setOption("buildNum","cf20535.11");
        _addPackageAclOperation14.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addPackageAclOperation14.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _addPackageAclOperation14.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _addPackageAclIndex14 = 14;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getaddPackageAclInvoke14(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_addPackageAclIndex14];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._addPackageAclOperation14);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_addPackageAclIndex14] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void addPackageAcl(long arg_0_14, oem.edge.ed.odc.dropbox.common.AclInfo arg_1_14) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getaddPackageAclInvoke14(new java.lang.Object[] {new java.lang.Long(arg_0_14), arg_1_14}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _removePackageAclOperation15;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params15 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_15"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_15"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_15"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
          };
        _params15[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params15[0].setOption("partName","long");
        _params15[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params15[1].setOption("partName","string");
        _params15[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params15[2].setOption("partName","byte");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc15 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults15 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _removePackageAclOperation15 = new com.ibm.ws.webservices.engine.description.OperationDesc("removePackageAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removePackageAcl"), _params15, _returnDesc15, _faults15, "");
        _removePackageAclOperation15.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _removePackageAclOperation15.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removePackageAclOperation15.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _removePackageAclOperation15.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removePackageAclResponse"));
        _removePackageAclOperation15.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removePackageAclRequest"));
        _removePackageAclOperation15.setOption("outputName","removePackageAclResponse");
        _removePackageAclOperation15.setOption("inputName","removePackageAclRequest");
        _removePackageAclOperation15.setOption("buildNum","cf20535.11");
        _removePackageAclOperation15.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removePackageAclOperation15.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _removePackageAclOperation15.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _removePackageAclIndex15 = 15;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getremovePackageAclInvoke15(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_removePackageAclIndex15];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._removePackageAclOperation15);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_removePackageAclIndex15] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void removePackageAcl(long arg_0_15, java.lang.String arg_1_15, byte arg_2_15) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getremovePackageAclInvoke15(new java.lang.Object[] {new java.lang.Long(arg_0_15), arg_1_15, new java.lang.Byte(arg_2_15)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _addUserAclOperation16;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params16 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_16"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_16"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params16[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params16[0].setOption("partName","long");
        _params16[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params16[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc16 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults16 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _addUserAclOperation16 = new com.ibm.ws.webservices.engine.description.OperationDesc("addUserAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addUserAcl"), _params16, _returnDesc16, _faults16, "");
        _addUserAclOperation16.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _addUserAclOperation16.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addUserAclOperation16.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _addUserAclOperation16.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addUserAclResponse"));
        _addUserAclOperation16.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addUserAclRequest"));
        _addUserAclOperation16.setOption("outputName","addUserAclResponse");
        _addUserAclOperation16.setOption("inputName","addUserAclRequest");
        _addUserAclOperation16.setOption("buildNum","cf20535.11");
        _addUserAclOperation16.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addUserAclOperation16.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _addUserAclOperation16.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _addUserAclIndex16 = 16;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getaddUserAclInvoke16(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_addUserAclIndex16];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._addUserAclOperation16);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_addUserAclIndex16] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void addUserAcl(long arg_0_16, java.lang.String arg_1_16) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getaddUserAclInvoke16(new java.lang.Object[] {new java.lang.Long(arg_0_16), arg_1_16}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _addGroupAclOperation17;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params17 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_17"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_17"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params17[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params17[0].setOption("partName","long");
        _params17[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params17[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc17 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults17 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _addGroupAclOperation17 = new com.ibm.ws.webservices.engine.description.OperationDesc("addGroupAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAcl"), _params17, _returnDesc17, _faults17, "");
        _addGroupAclOperation17.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _addGroupAclOperation17.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addGroupAclOperation17.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _addGroupAclOperation17.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAclResponse"));
        _addGroupAclOperation17.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAclRequest"));
        _addGroupAclOperation17.setOption("outputName","addGroupAclResponse");
        _addGroupAclOperation17.setOption("inputName","addGroupAclRequest");
        _addGroupAclOperation17.setOption("buildNum","cf20535.11");
        _addGroupAclOperation17.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addGroupAclOperation17.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _addGroupAclOperation17.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _addGroupAclIndex17 = 17;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getaddGroupAclInvoke17(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_addGroupAclIndex17];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._addGroupAclOperation17);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_addGroupAclIndex17] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void addGroupAcl(long arg_0_17, java.lang.String arg_1_17) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getaddGroupAclInvoke17(new java.lang.Object[] {new java.lang.Long(arg_0_17), arg_1_17}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _addGroupAclOperation18;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params18 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_18"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_18"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_18"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params18[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params18[0].setOption("partName","string");
        _params18[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params18[1].setOption("partName","string");
        _params18[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params18[2].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc18 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults18 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _addGroupAclOperation18 = new com.ibm.ws.webservices.engine.description.OperationDesc("addGroupAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAcl"), _params18, _returnDesc18, _faults18, "");
        _addGroupAclOperation18.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _addGroupAclOperation18.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addGroupAclOperation18.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _addGroupAclOperation18.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAclResponse1"));
        _addGroupAclOperation18.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addGroupAclRequest1"));
        _addGroupAclOperation18.setOption("outputName","addGroupAclResponse1");
        _addGroupAclOperation18.setOption("inputName","addGroupAclRequest1");
        _addGroupAclOperation18.setOption("buildNum","cf20535.11");
        _addGroupAclOperation18.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addGroupAclOperation18.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _addGroupAclOperation18.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _addGroupAclIndex18 = 18;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getaddGroupAclInvoke18(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_addGroupAclIndex18];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._addGroupAclOperation18);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_addGroupAclIndex18] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void addGroupAcl(java.lang.String arg_0_18, java.lang.String arg_1_18, boolean arg_2_18) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getaddGroupAclInvoke18(new java.lang.Object[] {arg_0_18, arg_1_18, new java.lang.Boolean(arg_2_18)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _addProjectAclOperation19;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params19 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_19"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_19"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params19[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params19[0].setOption("partName","long");
        _params19[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params19[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc19 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults19 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _addProjectAclOperation19 = new com.ibm.ws.webservices.engine.description.OperationDesc("addProjectAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addProjectAcl"), _params19, _returnDesc19, _faults19, "");
        _addProjectAclOperation19.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _addProjectAclOperation19.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addProjectAclOperation19.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _addProjectAclOperation19.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addProjectAclResponse"));
        _addProjectAclOperation19.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addProjectAclRequest"));
        _addProjectAclOperation19.setOption("outputName","addProjectAclResponse");
        _addProjectAclOperation19.setOption("inputName","addProjectAclRequest");
        _addProjectAclOperation19.setOption("buildNum","cf20535.11");
        _addProjectAclOperation19.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addProjectAclOperation19.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _addProjectAclOperation19.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _addProjectAclIndex19 = 19;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getaddProjectAclInvoke19(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_addProjectAclIndex19];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._addProjectAclOperation19);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_addProjectAclIndex19] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void addProjectAcl(long arg_0_19, java.lang.String arg_1_19) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getaddProjectAclInvoke19(new java.lang.Object[] {new java.lang.Long(arg_0_19), arg_1_19}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _removeUserAclOperation20;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params20 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_20"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_20"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params20[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params20[0].setOption("partName","long");
        _params20[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params20[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc20 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults20 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _removeUserAclOperation20 = new com.ibm.ws.webservices.engine.description.OperationDesc("removeUserAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeUserAcl"), _params20, _returnDesc20, _faults20, "");
        _removeUserAclOperation20.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _removeUserAclOperation20.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeUserAclOperation20.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _removeUserAclOperation20.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeUserAclResponse"));
        _removeUserAclOperation20.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeUserAclRequest"));
        _removeUserAclOperation20.setOption("outputName","removeUserAclResponse");
        _removeUserAclOperation20.setOption("inputName","removeUserAclRequest");
        _removeUserAclOperation20.setOption("buildNum","cf20535.11");
        _removeUserAclOperation20.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeUserAclOperation20.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _removeUserAclOperation20.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _removeUserAclIndex20 = 20;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getremoveUserAclInvoke20(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_removeUserAclIndex20];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._removeUserAclOperation20);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_removeUserAclIndex20] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void removeUserAcl(long arg_0_20, java.lang.String arg_1_20) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getremoveUserAclInvoke20(new java.lang.Object[] {new java.lang.Long(arg_0_20), arg_1_20}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _removeGroupAclOperation21;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params21 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_21"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_21"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params21[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params21[0].setOption("partName","long");
        _params21[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params21[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc21 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults21 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _removeGroupAclOperation21 = new com.ibm.ws.webservices.engine.description.OperationDesc("removeGroupAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAcl"), _params21, _returnDesc21, _faults21, "");
        _removeGroupAclOperation21.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _removeGroupAclOperation21.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeGroupAclOperation21.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _removeGroupAclOperation21.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAclResponse"));
        _removeGroupAclOperation21.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAclRequest"));
        _removeGroupAclOperation21.setOption("outputName","removeGroupAclResponse");
        _removeGroupAclOperation21.setOption("inputName","removeGroupAclRequest");
        _removeGroupAclOperation21.setOption("buildNum","cf20535.11");
        _removeGroupAclOperation21.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeGroupAclOperation21.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _removeGroupAclOperation21.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _removeGroupAclIndex21 = 21;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getremoveGroupAclInvoke21(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_removeGroupAclIndex21];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._removeGroupAclOperation21);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_removeGroupAclIndex21] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void removeGroupAcl(long arg_0_21, java.lang.String arg_1_21) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getremoveGroupAclInvoke21(new java.lang.Object[] {new java.lang.Long(arg_0_21), arg_1_21}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _removeGroupAclOperation22;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params22 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_22"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_22"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_22"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params22[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params22[0].setOption("partName","string");
        _params22[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params22[1].setOption("partName","string");
        _params22[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params22[2].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc22 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults22 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _removeGroupAclOperation22 = new com.ibm.ws.webservices.engine.description.OperationDesc("removeGroupAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAcl"), _params22, _returnDesc22, _faults22, "");
        _removeGroupAclOperation22.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _removeGroupAclOperation22.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeGroupAclOperation22.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _removeGroupAclOperation22.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAclResponse1"));
        _removeGroupAclOperation22.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeGroupAclRequest1"));
        _removeGroupAclOperation22.setOption("outputName","removeGroupAclResponse1");
        _removeGroupAclOperation22.setOption("inputName","removeGroupAclRequest1");
        _removeGroupAclOperation22.setOption("buildNum","cf20535.11");
        _removeGroupAclOperation22.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeGroupAclOperation22.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _removeGroupAclOperation22.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _removeGroupAclIndex22 = 22;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getremoveGroupAclInvoke22(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_removeGroupAclIndex22];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._removeGroupAclOperation22);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_removeGroupAclIndex22] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void removeGroupAcl(java.lang.String arg_0_22, java.lang.String arg_1_22, boolean arg_2_22) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getremoveGroupAclInvoke22(new java.lang.Object[] {arg_0_22, arg_1_22, new java.lang.Boolean(arg_2_22)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _removeProjectAclOperation23;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params23 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_23"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_23"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params23[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params23[0].setOption("partName","long");
        _params23[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params23[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc23 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults23 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _removeProjectAclOperation23 = new com.ibm.ws.webservices.engine.description.OperationDesc("removeProjectAcl", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeProjectAcl"), _params23, _returnDesc23, _faults23, "");
        _removeProjectAclOperation23.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _removeProjectAclOperation23.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeProjectAclOperation23.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _removeProjectAclOperation23.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeProjectAclResponse"));
        _removeProjectAclOperation23.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeProjectAclRequest"));
        _removeProjectAclOperation23.setOption("outputName","removeProjectAclResponse");
        _removeProjectAclOperation23.setOption("inputName","removeProjectAclRequest");
        _removeProjectAclOperation23.setOption("buildNum","cf20535.11");
        _removeProjectAclOperation23.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeProjectAclOperation23.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _removeProjectAclOperation23.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _removeProjectAclIndex23 = 23;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getremoveProjectAclInvoke23(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_removeProjectAclIndex23];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._removeProjectAclOperation23);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_removeProjectAclIndex23] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void removeProjectAcl(long arg_0_23, java.lang.String arg_1_23) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getremoveProjectAclInvoke23(new java.lang.Object[] {new java.lang.Long(arg_0_23), arg_1_23}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _changePackageExpirationOperation24;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params24 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_24"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_24"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params24[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params24[0].setOption("partName","long");
        _params24[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params24[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc24 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults24 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _changePackageExpirationOperation24 = new com.ibm.ws.webservices.engine.description.OperationDesc("changePackageExpiration", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "changePackageExpiration"), _params24, _returnDesc24, _faults24, "");
        _changePackageExpirationOperation24.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _changePackageExpirationOperation24.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _changePackageExpirationOperation24.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _changePackageExpirationOperation24.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "changePackageExpirationResponse"));
        _changePackageExpirationOperation24.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "changePackageExpirationRequest"));
        _changePackageExpirationOperation24.setOption("outputName","changePackageExpirationResponse");
        _changePackageExpirationOperation24.setOption("inputName","changePackageExpirationRequest");
        _changePackageExpirationOperation24.setOption("buildNum","cf20535.11");
        _changePackageExpirationOperation24.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _changePackageExpirationOperation24.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _changePackageExpirationOperation24.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _changePackageExpirationIndex24 = 24;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getchangePackageExpirationInvoke24(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_changePackageExpirationIndex24];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._changePackageExpirationOperation24);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_changePackageExpirationIndex24] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void changePackageExpiration(long arg_0_24, long arg_1_24) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getchangePackageExpirationInvoke24(new java.lang.Object[] {new java.lang.Long(arg_0_24), new java.lang.Long(arg_1_24)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _getStoragePoolInstanceOperation25;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params25 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_25"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params25[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params25[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc25 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getStoragePoolInstanceReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "PoolInfo"), oem.edge.ed.odc.dropbox.common.PoolInfo.class, true, false, false, false, true, false); 
        _returnDesc25.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}PoolInfo");
        _returnDesc25.setOption("partName","PoolInfo");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults25 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _getStoragePoolInstanceOperation25 = new com.ibm.ws.webservices.engine.description.OperationDesc("getStoragePoolInstance", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getStoragePoolInstance"), _params25, _returnDesc25, _faults25, "");
        _getStoragePoolInstanceOperation25.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _getStoragePoolInstanceOperation25.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getStoragePoolInstanceOperation25.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _getStoragePoolInstanceOperation25.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getStoragePoolInstanceResponse"));
        _getStoragePoolInstanceOperation25.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getStoragePoolInstanceRequest"));
        _getStoragePoolInstanceOperation25.setOption("outputName","getStoragePoolInstanceResponse");
        _getStoragePoolInstanceOperation25.setOption("inputName","getStoragePoolInstanceRequest");
        _getStoragePoolInstanceOperation25.setOption("buildNum","cf20535.11");
        _getStoragePoolInstanceOperation25.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getStoragePoolInstanceOperation25.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _getStoragePoolInstanceOperation25.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _getStoragePoolInstanceIndex25 = 25;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getgetStoragePoolInstanceInvoke25(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_getStoragePoolInstanceIndex25];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._getStoragePoolInstanceOperation25);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_getStoragePoolInstanceIndex25] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public oem.edge.ed.odc.dropbox.common.PoolInfo getStoragePoolInstance(long arg_0_25) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getgetStoragePoolInstanceInvoke25(new java.lang.Object[] {new java.lang.Long(arg_0_25)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (oem.edge.ed.odc.dropbox.common.PoolInfo) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (oem.edge.ed.odc.dropbox.common.PoolInfo) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), oem.edge.ed.odc.dropbox.common.PoolInfo.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryStoragePoolInformationOperation26;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params26 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc26 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryStoragePoolInformationReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc26.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc26.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults26 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryStoragePoolInformationOperation26 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryStoragePoolInformation", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryStoragePoolInformation"), _params26, _returnDesc26, _faults26, "");
        _queryStoragePoolInformationOperation26.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryStoragePoolInformationOperation26.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryStoragePoolInformationOperation26.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryStoragePoolInformationOperation26.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryStoragePoolInformationResponse"));
        _queryStoragePoolInformationOperation26.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryStoragePoolInformationRequest"));
        _queryStoragePoolInformationOperation26.setOption("outputName","queryStoragePoolInformationResponse");
        _queryStoragePoolInformationOperation26.setOption("inputName","queryStoragePoolInformationRequest");
        _queryStoragePoolInformationOperation26.setOption("buildNum","cf20535.11");
        _queryStoragePoolInformationOperation26.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryStoragePoolInformationOperation26.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryStoragePoolInformationOperation26.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryStoragePoolInformationIndex26 = 26;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryStoragePoolInformationInvoke26(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryStoragePoolInformationIndex26];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryStoragePoolInformationOperation26);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryStoragePoolInformationIndex26] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryStoragePoolInformation() throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryStoragePoolInformationInvoke26(new java.lang.Object[] {}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _setPackageDescriptionOperation27;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params27 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_27"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_27"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params27[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params27[0].setOption("partName","long");
        _params27[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params27[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc27 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults27 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _setPackageDescriptionOperation27 = new com.ibm.ws.webservices.engine.description.OperationDesc("setPackageDescription", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageDescription"), _params27, _returnDesc27, _faults27, "");
        _setPackageDescriptionOperation27.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _setPackageDescriptionOperation27.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _setPackageDescriptionOperation27.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _setPackageDescriptionOperation27.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageDescriptionResponse"));
        _setPackageDescriptionOperation27.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setPackageDescriptionRequest"));
        _setPackageDescriptionOperation27.setOption("outputName","setPackageDescriptionResponse");
        _setPackageDescriptionOperation27.setOption("inputName","setPackageDescriptionRequest");
        _setPackageDescriptionOperation27.setOption("buildNum","cf20535.11");
        _setPackageDescriptionOperation27.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _setPackageDescriptionOperation27.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _setPackageDescriptionOperation27.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _setPackageDescriptionIndex27 = 27;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getsetPackageDescriptionInvoke27(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_setPackageDescriptionIndex27];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._setPackageDescriptionOperation27);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_setPackageDescriptionIndex27] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void setPackageDescription(long arg_0_27, java.lang.String arg_1_27) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getsetPackageDescriptionInvoke27(new java.lang.Object[] {new java.lang.Long(arg_0_27), arg_1_27}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _getOptionsOperation28;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params28 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc28 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getOptionsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc28.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc28.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults28 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _getOptionsOperation28 = new com.ibm.ws.webservices.engine.description.OperationDesc("getOptions", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptions"), _params28, _returnDesc28, _faults28, "");
        _getOptionsOperation28.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _getOptionsOperation28.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getOptionsOperation28.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _getOptionsOperation28.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionsResponse"));
        _getOptionsOperation28.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionsRequest"));
        _getOptionsOperation28.setOption("outputName","getOptionsResponse");
        _getOptionsOperation28.setOption("inputName","getOptionsRequest");
        _getOptionsOperation28.setOption("buildNum","cf20535.11");
        _getOptionsOperation28.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getOptionsOperation28.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _getOptionsOperation28.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _getOptionsIndex28 = 28;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getgetOptionsInvoke28(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_getOptionsIndex28];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._getOptionsOperation28);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_getOptionsIndex28] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.HashMap getOptions() throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getgetOptionsInvoke28(new java.lang.Object[] {}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.HashMap) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.HashMap) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.HashMap.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _getOptionsOperation29;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params29 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_29"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, false, false, false, false, true, false), 
          };
        _params29[0].setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _params29[0].setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc29 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getOptionsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc29.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc29.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults29 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _getOptionsOperation29 = new com.ibm.ws.webservices.engine.description.OperationDesc("getOptions", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptions"), _params29, _returnDesc29, _faults29, "");
        _getOptionsOperation29.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _getOptionsOperation29.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getOptionsOperation29.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _getOptionsOperation29.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionsResponse1"));
        _getOptionsOperation29.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionsRequest1"));
        _getOptionsOperation29.setOption("outputName","getOptionsResponse1");
        _getOptionsOperation29.setOption("inputName","getOptionsRequest1");
        _getOptionsOperation29.setOption("buildNum","cf20535.11");
        _getOptionsOperation29.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getOptionsOperation29.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _getOptionsOperation29.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _getOptionsIndex29 = 29;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getgetOptionsInvoke29(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_getOptionsIndex29];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._getOptionsOperation29);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_getOptionsIndex29] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.HashMap getOptions(java.util.Vector arg_0_29) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getgetOptionsInvoke29(new java.lang.Object[] {arg_0_29}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.HashMap) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.HashMap) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.HashMap.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _getOptionOperation30;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params30 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_30"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params30[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params30[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc30 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getOptionReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, true, false, false, false, true, false); 
        _returnDesc30.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _returnDesc30.setOption("partName","string");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults30 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _getOptionOperation30 = new com.ibm.ws.webservices.engine.description.OperationDesc("getOption", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOption"), _params30, _returnDesc30, _faults30, "");
        _getOptionOperation30.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _getOptionOperation30.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getOptionOperation30.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _getOptionOperation30.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionResponse"));
        _getOptionOperation30.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getOptionRequest"));
        _getOptionOperation30.setOption("outputName","getOptionResponse");
        _getOptionOperation30.setOption("inputName","getOptionRequest");
        _getOptionOperation30.setOption("buildNum","cf20535.11");
        _getOptionOperation30.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getOptionOperation30.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _getOptionOperation30.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _getOptionIndex30 = 30;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getgetOptionInvoke30(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_getOptionIndex30];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._getOptionOperation30);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_getOptionIndex30] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.lang.String getOption(java.lang.String arg_0_30) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getgetOptionInvoke30(new java.lang.Object[] {arg_0_30}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.lang.String) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.lang.String) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.lang.String.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _setOptionsOperation31;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params31 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_31"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false, false, false, true, false), 
          };
        _params31[0].setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _params31[0].setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc31 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults31 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _setOptionsOperation31 = new com.ibm.ws.webservices.engine.description.OperationDesc("setOptions", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptions"), _params31, _returnDesc31, _faults31, "");
        _setOptionsOperation31.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _setOptionsOperation31.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _setOptionsOperation31.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _setOptionsOperation31.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptionsResponse"));
        _setOptionsOperation31.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptionsRequest"));
        _setOptionsOperation31.setOption("outputName","setOptionsResponse");
        _setOptionsOperation31.setOption("inputName","setOptionsRequest");
        _setOptionsOperation31.setOption("buildNum","cf20535.11");
        _setOptionsOperation31.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _setOptionsOperation31.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _setOptionsOperation31.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _setOptionsIndex31 = 31;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getsetOptionsInvoke31(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_setOptionsIndex31];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._setOptionsOperation31);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_setOptionsIndex31] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void setOptions(java.util.HashMap arg_0_31) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getsetOptionsInvoke31(new java.lang.Object[] {arg_0_31}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _setOptionOperation32;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params32 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_32"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_32"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params32[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params32[0].setOption("partName","string");
        _params32[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params32[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc32 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults32 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _setOptionOperation32 = new com.ibm.ws.webservices.engine.description.OperationDesc("setOption", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOption"), _params32, _returnDesc32, _faults32, "");
        _setOptionOperation32.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _setOptionOperation32.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _setOptionOperation32.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _setOptionOperation32.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptionResponse"));
        _setOptionOperation32.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "setOptionRequest"));
        _setOptionOperation32.setOption("outputName","setOptionResponse");
        _setOptionOperation32.setOption("inputName","setOptionRequest");
        _setOptionOperation32.setOption("buildNum","cf20535.11");
        _setOptionOperation32.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _setOptionOperation32.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _setOptionOperation32.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _setOptionIndex32 = 32;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getsetOptionInvoke32(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_setOptionIndex32];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._setOptionOperation32);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_setOptionIndex32] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void setOption(java.lang.String arg_0_32, java.lang.String arg_1_32) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getsetOptionInvoke32(new java.lang.Object[] {arg_0_32, arg_1_32}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _getProjectListOperation33;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params33 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
          };
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc33 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getProjectListReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc33.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc33.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults33 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _getProjectListOperation33 = new com.ibm.ws.webservices.engine.description.OperationDesc("getProjectList", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getProjectList"), _params33, _returnDesc33, _faults33, "");
        _getProjectListOperation33.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _getProjectListOperation33.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getProjectListOperation33.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _getProjectListOperation33.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getProjectListResponse"));
        _getProjectListOperation33.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getProjectListRequest"));
        _getProjectListOperation33.setOption("outputName","getProjectListResponse");
        _getProjectListOperation33.setOption("inputName","getProjectListRequest");
        _getProjectListOperation33.setOption("buildNum","cf20535.11");
        _getProjectListOperation33.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getProjectListOperation33.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _getProjectListOperation33.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _getProjectListIndex33 = 33;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getgetProjectListInvoke33(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_getProjectListIndex33];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._getProjectListOperation33);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_getProjectListIndex33] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector getProjectList() throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getgetProjectListInvoke33(new java.lang.Object[] {}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryPackagesOperation34;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params34 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_5_34"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params34[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params34[0].setOption("partName","string");
        _params34[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params34[1].setOption("partName","boolean");
        _params34[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params34[2].setOption("partName","boolean");
        _params34[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params34[3].setOption("partName","boolean");
        _params34[4].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params34[4].setOption("partName","boolean");
        _params34[5].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params34[5].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc34 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackagesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc34.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc34.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults34 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryPackagesOperation34 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackages", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackages"), _params34, _returnDesc34, _faults34, "");
        _queryPackagesOperation34.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryPackagesOperation34.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackagesOperation34.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryPackagesOperation34.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackagesResponse"));
        _queryPackagesOperation34.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackagesRequest"));
        _queryPackagesOperation34.setOption("outputName","queryPackagesResponse");
        _queryPackagesOperation34.setOption("inputName","queryPackagesRequest");
        _queryPackagesOperation34.setOption("buildNum","cf20535.11");
        _queryPackagesOperation34.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackagesOperation34.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryPackagesOperation34.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryPackagesIndex34 = 34;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryPackagesInvoke34(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryPackagesIndex34];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryPackagesOperation34);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryPackagesIndex34] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryPackages(java.lang.String arg_0_34, boolean arg_1_34, boolean arg_2_34, boolean arg_3_34, boolean arg_4_34, boolean arg_5_34) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryPackagesInvoke34(new java.lang.Object[] {arg_0_34, new java.lang.Boolean(arg_1_34), new java.lang.Boolean(arg_2_34), new java.lang.Boolean(arg_3_34), new java.lang.Boolean(arg_4_34), new java.lang.Boolean(arg_5_34)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryPackagesOperation35;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params35 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_35"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_35"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_35"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_35"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params35[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params35[0].setOption("partName","boolean");
        _params35[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params35[1].setOption("partName","boolean");
        _params35[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params35[2].setOption("partName","boolean");
        _params35[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params35[3].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc35 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackagesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc35.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc35.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults35 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryPackagesOperation35 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackages", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackages"), _params35, _returnDesc35, _faults35, "");
        _queryPackagesOperation35.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryPackagesOperation35.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackagesOperation35.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryPackagesOperation35.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackagesResponse1"));
        _queryPackagesOperation35.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackagesRequest1"));
        _queryPackagesOperation35.setOption("outputName","queryPackagesResponse1");
        _queryPackagesOperation35.setOption("inputName","queryPackagesRequest1");
        _queryPackagesOperation35.setOption("buildNum","cf20535.11");
        _queryPackagesOperation35.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackagesOperation35.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryPackagesOperation35.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryPackagesIndex35 = 35;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryPackagesInvoke35(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryPackagesIndex35];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryPackagesOperation35);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryPackagesIndex35] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryPackages(boolean arg_0_35, boolean arg_1_35, boolean arg_2_35, boolean arg_3_35) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryPackagesInvoke35(new java.lang.Object[] {new java.lang.Boolean(arg_0_35), new java.lang.Boolean(arg_1_35), new java.lang.Boolean(arg_2_35), new java.lang.Boolean(arg_3_35)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryPackageOperation36;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params36 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_36"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_36"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params36[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params36[0].setOption("partName","long");
        _params36[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params36[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc36 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "PackageInfo"), oem.edge.ed.odc.dropbox.common.PackageInfo.class, true, false, false, false, true, false); 
        _returnDesc36.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}PackageInfo");
        _returnDesc36.setOption("partName","PackageInfo");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults36 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryPackageOperation36 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackage"), _params36, _returnDesc36, _faults36, "");
        _queryPackageOperation36.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryPackageOperation36.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageOperation36.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryPackageOperation36.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageResponse"));
        _queryPackageOperation36.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageRequest"));
        _queryPackageOperation36.setOption("outputName","queryPackageResponse");
        _queryPackageOperation36.setOption("inputName","queryPackageRequest");
        _queryPackageOperation36.setOption("buildNum","cf20535.11");
        _queryPackageOperation36.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageOperation36.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryPackageOperation36.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryPackageIndex36 = 36;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryPackageInvoke36(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryPackageIndex36];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryPackageOperation36);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryPackageIndex36] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public oem.edge.ed.odc.dropbox.common.PackageInfo queryPackage(long arg_0_36, boolean arg_1_36) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryPackageInvoke36(new java.lang.Object[] {new java.lang.Long(arg_0_36), new java.lang.Boolean(arg_1_36)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (oem.edge.ed.odc.dropbox.common.PackageInfo) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (oem.edge.ed.odc.dropbox.common.PackageInfo) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), oem.edge.ed.odc.dropbox.common.PackageInfo.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryPackageContentsOperation37;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params37 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_37"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params37[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params37[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc37 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageContentsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc37.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc37.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults37 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryPackageContentsOperation37 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackageContents", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageContents"), _params37, _returnDesc37, _faults37, "");
        _queryPackageContentsOperation37.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryPackageContentsOperation37.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageContentsOperation37.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryPackageContentsOperation37.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageContentsResponse"));
        _queryPackageContentsOperation37.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageContentsRequest"));
        _queryPackageContentsOperation37.setOption("outputName","queryPackageContentsResponse");
        _queryPackageContentsOperation37.setOption("inputName","queryPackageContentsRequest");
        _queryPackageContentsOperation37.setOption("buildNum","cf20535.11");
        _queryPackageContentsOperation37.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageContentsOperation37.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryPackageContentsOperation37.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryPackageContentsIndex37 = 37;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryPackageContentsInvoke37(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryPackageContentsIndex37];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryPackageContentsOperation37);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryPackageContentsIndex37] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryPackageContents(long arg_0_37) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryPackageContentsInvoke37(new java.lang.Object[] {new java.lang.Long(arg_0_37)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryFilesOperation38;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params38 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_38"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_38"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_38"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params38[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params38[0].setOption("partName","string");
        _params38[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params38[1].setOption("partName","boolean");
        _params38[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params38[2].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc38 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryFilesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc38.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc38.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults38 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryFilesOperation38 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryFiles", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFiles"), _params38, _returnDesc38, _faults38, "");
        _queryFilesOperation38.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryFilesOperation38.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryFilesOperation38.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryFilesOperation38.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFilesResponse"));
        _queryFilesOperation38.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFilesRequest"));
        _queryFilesOperation38.setOption("outputName","queryFilesResponse");
        _queryFilesOperation38.setOption("inputName","queryFilesRequest");
        _queryFilesOperation38.setOption("buildNum","cf20535.11");
        _queryFilesOperation38.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryFilesOperation38.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryFilesOperation38.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryFilesIndex38 = 38;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryFilesInvoke38(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryFilesIndex38];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryFilesOperation38);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryFilesIndex38] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryFiles(java.lang.String arg_0_38, boolean arg_1_38, boolean arg_2_38) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryFilesInvoke38(new java.lang.Object[] {arg_0_38, new java.lang.Boolean(arg_1_38), new java.lang.Boolean(arg_2_38)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryFilesOperation39;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params39 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_39"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params39[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params39[0].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc39 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryFilesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc39.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc39.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults39 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryFilesOperation39 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryFiles", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFiles"), _params39, _returnDesc39, _faults39, "");
        _queryFilesOperation39.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryFilesOperation39.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryFilesOperation39.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryFilesOperation39.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFilesResponse1"));
        _queryFilesOperation39.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFilesRequest1"));
        _queryFilesOperation39.setOption("outputName","queryFilesResponse1");
        _queryFilesOperation39.setOption("inputName","queryFilesRequest1");
        _queryFilesOperation39.setOption("buildNum","cf20535.11");
        _queryFilesOperation39.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryFilesOperation39.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryFilesOperation39.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryFilesIndex39 = 39;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryFilesInvoke39(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryFilesIndex39];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryFilesOperation39);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryFilesIndex39] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryFiles(boolean arg_0_39) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryFilesInvoke39(new java.lang.Object[] {new java.lang.Boolean(arg_0_39)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryFileOperation40;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params40 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_40"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params40[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params40[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc40 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryFileReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileInfo"), oem.edge.ed.odc.dropbox.common.FileInfo.class, true, false, false, false, true, false); 
        _returnDesc40.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}FileInfo");
        _returnDesc40.setOption("partName","FileInfo");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults40 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryFileOperation40 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryFile", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFile"), _params40, _returnDesc40, _faults40, "");
        _queryFileOperation40.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryFileOperation40.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryFileOperation40.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryFileOperation40.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileResponse"));
        _queryFileOperation40.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileRequest"));
        _queryFileOperation40.setOption("outputName","queryFileResponse");
        _queryFileOperation40.setOption("inputName","queryFileRequest");
        _queryFileOperation40.setOption("buildNum","cf20535.11");
        _queryFileOperation40.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryFileOperation40.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryFileOperation40.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryFileIndex40 = 40;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryFileInvoke40(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryFileIndex40];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryFileOperation40);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryFileIndex40] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public oem.edge.ed.odc.dropbox.common.FileInfo queryFile(long arg_0_40) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryFileInvoke40(new java.lang.Object[] {new java.lang.Long(arg_0_40)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (oem.edge.ed.odc.dropbox.common.FileInfo) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (oem.edge.ed.odc.dropbox.common.FileInfo) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), oem.edge.ed.odc.dropbox.common.FileInfo.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryPackageAclsOperation41;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params41 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_41"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_41"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params41[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params41[0].setOption("partName","long");
        _params41[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params41[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc41 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageAclsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc41.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc41.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults41 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryPackageAclsOperation41 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackageAcls", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAcls"), _params41, _returnDesc41, _faults41, "");
        _queryPackageAclsOperation41.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryPackageAclsOperation41.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageAclsOperation41.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryPackageAclsOperation41.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclsResponse"));
        _queryPackageAclsOperation41.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclsRequest"));
        _queryPackageAclsOperation41.setOption("outputName","queryPackageAclsResponse");
        _queryPackageAclsOperation41.setOption("inputName","queryPackageAclsRequest");
        _queryPackageAclsOperation41.setOption("buildNum","cf20535.11");
        _queryPackageAclsOperation41.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageAclsOperation41.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryPackageAclsOperation41.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryPackageAclsIndex41 = 41;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryPackageAclsInvoke41(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryPackageAclsIndex41];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryPackageAclsOperation41);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryPackageAclsIndex41] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryPackageAcls(long arg_0_41, boolean arg_1_41) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryPackageAclsInvoke41(new java.lang.Object[] {new java.lang.Long(arg_0_41), new java.lang.Boolean(arg_1_41)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryPackageAclCompaniesOperation42;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params42 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_42"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params42[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params42[0].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc42 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageAclCompaniesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc42.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc42.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults42 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryPackageAclCompaniesOperation42 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackageAclCompanies", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclCompanies"), _params42, _returnDesc42, _faults42, "");
        _queryPackageAclCompaniesOperation42.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryPackageAclCompaniesOperation42.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageAclCompaniesOperation42.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryPackageAclCompaniesOperation42.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclCompaniesResponse"));
        _queryPackageAclCompaniesOperation42.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageAclCompaniesRequest"));
        _queryPackageAclCompaniesOperation42.setOption("outputName","queryPackageAclCompaniesResponse");
        _queryPackageAclCompaniesOperation42.setOption("inputName","queryPackageAclCompaniesRequest");
        _queryPackageAclCompaniesOperation42.setOption("buildNum","cf20535.11");
        _queryPackageAclCompaniesOperation42.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageAclCompaniesOperation42.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryPackageAclCompaniesOperation42.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryPackageAclCompaniesIndex42 = 42;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryPackageAclCompaniesInvoke42(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryPackageAclCompaniesIndex42];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryPackageAclCompaniesOperation42);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryPackageAclCompaniesIndex42] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryPackageAclCompanies(long arg_0_42) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryPackageAclCompaniesInvoke42(new java.lang.Object[] {new java.lang.Long(arg_0_42)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryRepresentedCompaniesOperation43;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params43 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_43"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_43"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params43[0].setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _params43[0].setOption("partName","Vector");
        _params43[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params43[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc43 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryRepresentedCompaniesReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc43.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc43.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults43 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryRepresentedCompaniesOperation43 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryRepresentedCompanies", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryRepresentedCompanies"), _params43, _returnDesc43, _faults43, "");
        _queryRepresentedCompaniesOperation43.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryRepresentedCompaniesOperation43.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryRepresentedCompaniesOperation43.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryRepresentedCompaniesOperation43.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryRepresentedCompaniesResponse"));
        _queryRepresentedCompaniesOperation43.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryRepresentedCompaniesRequest"));
        _queryRepresentedCompaniesOperation43.setOption("outputName","queryRepresentedCompaniesResponse");
        _queryRepresentedCompaniesOperation43.setOption("inputName","queryRepresentedCompaniesRequest");
        _queryRepresentedCompaniesOperation43.setOption("buildNum","cf20535.11");
        _queryRepresentedCompaniesOperation43.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryRepresentedCompaniesOperation43.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryRepresentedCompaniesOperation43.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryRepresentedCompaniesIndex43 = 43;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryRepresentedCompaniesInvoke43(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryRepresentedCompaniesIndex43];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryRepresentedCompaniesOperation43);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryRepresentedCompaniesIndex43] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryRepresentedCompanies(java.util.Vector arg_0_43, boolean arg_1_43) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryRepresentedCompaniesInvoke43(new java.lang.Object[] {arg_0_43, new java.lang.Boolean(arg_1_43)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _lookupUserOperation44;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params44 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_44"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_44"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params44[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params44[0].setOption("partName","string");
        _params44[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params44[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc44 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "lookupUserReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc44.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc44.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults44 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _lookupUserOperation44 = new com.ibm.ws.webservices.engine.description.OperationDesc("lookupUser", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "lookupUser"), _params44, _returnDesc44, _faults44, "");
        _lookupUserOperation44.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _lookupUserOperation44.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _lookupUserOperation44.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _lookupUserOperation44.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "lookupUserResponse"));
        _lookupUserOperation44.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "lookupUserRequest"));
        _lookupUserOperation44.setOption("outputName","lookupUserResponse");
        _lookupUserOperation44.setOption("inputName","lookupUserRequest");
        _lookupUserOperation44.setOption("buildNum","cf20535.11");
        _lookupUserOperation44.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _lookupUserOperation44.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _lookupUserOperation44.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _lookupUserIndex44 = 44;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getlookupUserInvoke44(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_lookupUserIndex44];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._lookupUserOperation44);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_lookupUserIndex44] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector lookupUser(java.lang.String arg_0_44, boolean arg_1_44) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getlookupUserInvoke44(new java.lang.Object[] {arg_0_44, new java.lang.Boolean(arg_1_44)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryPackageFileAclsOperation45;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params45 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_45"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_45"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params45[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params45[0].setOption("partName","long");
        _params45[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params45[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc45 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryPackageFileAclsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc45.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc45.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults45 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryPackageFileAclsOperation45 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryPackageFileAcls", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageFileAcls"), _params45, _returnDesc45, _faults45, "");
        _queryPackageFileAclsOperation45.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryPackageFileAclsOperation45.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageFileAclsOperation45.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryPackageFileAclsOperation45.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageFileAclsResponse"));
        _queryPackageFileAclsOperation45.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryPackageFileAclsRequest"));
        _queryPackageFileAclsOperation45.setOption("outputName","queryPackageFileAclsResponse");
        _queryPackageFileAclsOperation45.setOption("inputName","queryPackageFileAclsRequest");
        _queryPackageFileAclsOperation45.setOption("buildNum","cf20535.11");
        _queryPackageFileAclsOperation45.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryPackageFileAclsOperation45.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryPackageFileAclsOperation45.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryPackageFileAclsIndex45 = 45;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryPackageFileAclsInvoke45(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryPackageFileAclsIndex45];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryPackageFileAclsOperation45);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryPackageFileAclsIndex45] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryPackageFileAcls(long arg_0_45, long arg_1_45) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryPackageFileAclsInvoke45(new java.lang.Object[] {new java.lang.Long(arg_0_45), new java.lang.Long(arg_1_45)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _addItemToPackageOperation46;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params46 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_46"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_46"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params46[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params46[0].setOption("partName","long");
        _params46[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params46[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc46 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults46 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _addItemToPackageOperation46 = new com.ibm.ws.webservices.engine.description.OperationDesc("addItemToPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addItemToPackage"), _params46, _returnDesc46, _faults46, "");
        _addItemToPackageOperation46.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _addItemToPackageOperation46.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addItemToPackageOperation46.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _addItemToPackageOperation46.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addItemToPackageResponse"));
        _addItemToPackageOperation46.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "addItemToPackageRequest"));
        _addItemToPackageOperation46.setOption("outputName","addItemToPackageResponse");
        _addItemToPackageOperation46.setOption("inputName","addItemToPackageRequest");
        _addItemToPackageOperation46.setOption("buildNum","cf20535.11");
        _addItemToPackageOperation46.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _addItemToPackageOperation46.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _addItemToPackageOperation46.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _addItemToPackageIndex46 = 46;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getaddItemToPackageInvoke46(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_addItemToPackageIndex46];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._addItemToPackageOperation46);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_addItemToPackageIndex46] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void addItemToPackage(long arg_0_46, long arg_1_46) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getaddItemToPackageInvoke46(new java.lang.Object[] {new java.lang.Long(arg_0_46), new java.lang.Long(arg_1_46)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _removeItemFromPackageOperation47;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params47 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_47"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_47"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params47[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params47[0].setOption("partName","long");
        _params47[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params47[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc47 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults47 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _removeItemFromPackageOperation47 = new com.ibm.ws.webservices.engine.description.OperationDesc("removeItemFromPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeItemFromPackage"), _params47, _returnDesc47, _faults47, "");
        _removeItemFromPackageOperation47.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _removeItemFromPackageOperation47.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeItemFromPackageOperation47.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _removeItemFromPackageOperation47.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeItemFromPackageResponse"));
        _removeItemFromPackageOperation47.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeItemFromPackageRequest"));
        _removeItemFromPackageOperation47.setOption("outputName","removeItemFromPackageResponse");
        _removeItemFromPackageOperation47.setOption("inputName","removeItemFromPackageRequest");
        _removeItemFromPackageOperation47.setOption("buildNum","cf20535.11");
        _removeItemFromPackageOperation47.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeItemFromPackageOperation47.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _removeItemFromPackageOperation47.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _removeItemFromPackageIndex47 = 47;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getremoveItemFromPackageInvoke47(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_removeItemFromPackageIndex47];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._removeItemFromPackageOperation47);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_removeItemFromPackageIndex47] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void removeItemFromPackage(long arg_0_47, long arg_1_47) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getremoveItemFromPackageInvoke47(new java.lang.Object[] {new java.lang.Long(arg_0_47), new java.lang.Long(arg_1_47)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _uploadFileToPackageOperation48;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params48 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_48"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_48"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_48"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params48[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params48[0].setOption("partName","long");
        _params48[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params48[1].setOption("partName","string");
        _params48[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params48[2].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc48 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "uploadFileToPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, true, false, false, false, true, false); 
        _returnDesc48.setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _returnDesc48.setOption("partName","long");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults48 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _uploadFileToPackageOperation48 = new com.ibm.ws.webservices.engine.description.OperationDesc("uploadFileToPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileToPackage"), _params48, _returnDesc48, _faults48, "");
        _uploadFileToPackageOperation48.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _uploadFileToPackageOperation48.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _uploadFileToPackageOperation48.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _uploadFileToPackageOperation48.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileToPackageResponse"));
        _uploadFileToPackageOperation48.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileToPackageRequest"));
        _uploadFileToPackageOperation48.setOption("outputName","uploadFileToPackageResponse");
        _uploadFileToPackageOperation48.setOption("inputName","uploadFileToPackageRequest");
        _uploadFileToPackageOperation48.setOption("buildNum","cf20535.11");
        _uploadFileToPackageOperation48.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _uploadFileToPackageOperation48.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _uploadFileToPackageOperation48.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _uploadFileToPackageIndex48 = 48;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getuploadFileToPackageInvoke48(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_uploadFileToPackageIndex48];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._uploadFileToPackageOperation48);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_uploadFileToPackageIndex48] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public long uploadFileToPackage(long arg_0_48, java.lang.String arg_1_48, long arg_2_48) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getuploadFileToPackageInvoke48(new java.lang.Object[] {new java.lang.Long(arg_0_48), arg_1_48, new java.lang.Long(arg_2_48)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return ((java.lang.Long) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue()).longValue();
        } catch (java.lang.Exception _exception) {
            return ((java.lang.Long) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), long.class)).longValue();
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _allocateUploadFileSlotOperation49;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params49 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_49"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_49"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_49"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false, false, false, true, false), 
          };
        _params49[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params49[0].setOption("partName","long");
        _params49[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params49[1].setOption("partName","long");
        _params49[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}int");
        _params49[2].setOption("partName","int");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc49 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "allocateUploadFileSlotReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileSlot"), oem.edge.ed.odc.dropbox.common.FileSlot.class, true, false, false, false, true, false); 
        _returnDesc49.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}FileSlot");
        _returnDesc49.setOption("partName","FileSlot");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults49 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _allocateUploadFileSlotOperation49 = new com.ibm.ws.webservices.engine.description.OperationDesc("allocateUploadFileSlot", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "allocateUploadFileSlot"), _params49, _returnDesc49, _faults49, "");
        _allocateUploadFileSlotOperation49.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _allocateUploadFileSlotOperation49.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _allocateUploadFileSlotOperation49.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _allocateUploadFileSlotOperation49.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "allocateUploadFileSlotResponse"));
        _allocateUploadFileSlotOperation49.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "allocateUploadFileSlotRequest"));
        _allocateUploadFileSlotOperation49.setOption("outputName","allocateUploadFileSlotResponse");
        _allocateUploadFileSlotOperation49.setOption("inputName","allocateUploadFileSlotRequest");
        _allocateUploadFileSlotOperation49.setOption("buildNum","cf20535.11");
        _allocateUploadFileSlotOperation49.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _allocateUploadFileSlotOperation49.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _allocateUploadFileSlotOperation49.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _allocateUploadFileSlotIndex49 = 49;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getallocateUploadFileSlotInvoke49(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_allocateUploadFileSlotIndex49];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._allocateUploadFileSlotOperation49);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_allocateUploadFileSlotIndex49] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public oem.edge.ed.odc.dropbox.common.FileSlot allocateUploadFileSlot(long arg_0_49, long arg_1_49, int arg_2_49) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getallocateUploadFileSlotInvoke49(new java.lang.Object[] {new java.lang.Long(arg_0_49), new java.lang.Long(arg_1_49), new java.lang.Integer(arg_2_49)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (oem.edge.ed.odc.dropbox.common.FileSlot) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (oem.edge.ed.odc.dropbox.common.FileSlot) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), oem.edge.ed.odc.dropbox.common.FileSlot.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _uploadFileSlotToPackageOperation50;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params50 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_50"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://schemas.xmlsoap.org/soap/encoding/", "base64"), byte[].class, false, false, false, false, true, false), 
          };
        _params50[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params50[0].setOption("partName","long");
        _params50[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params50[1].setOption("partName","long");
        _params50[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params50[2].setOption("partName","long");
        _params50[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params50[3].setOption("partName","boolean");
        _params50[4].setOption("partQNameString","{http://schemas.xmlsoap.org/soap/encoding/}base64");
        _params50[4].setOption("partName","base64");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc50 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "uploadFileSlotToPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dropbox.odc.ed.edge.oem", "FileSlot"), oem.edge.ed.odc.dropbox.common.FileSlot.class, true, false, false, false, true, false); 
        _returnDesc50.setOption("partQNameString","{http://common.dropbox.odc.ed.edge.oem}FileSlot");
        _returnDesc50.setOption("partName","FileSlot");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults50 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _uploadFileSlotToPackageOperation50 = new com.ibm.ws.webservices.engine.description.OperationDesc("uploadFileSlotToPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileSlotToPackage"), _params50, _returnDesc50, _faults50, "");
        _uploadFileSlotToPackageOperation50.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _uploadFileSlotToPackageOperation50.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _uploadFileSlotToPackageOperation50.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _uploadFileSlotToPackageOperation50.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileSlotToPackageResponse"));
        _uploadFileSlotToPackageOperation50.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "uploadFileSlotToPackageRequest"));
        _uploadFileSlotToPackageOperation50.setOption("outputName","uploadFileSlotToPackageResponse");
        _uploadFileSlotToPackageOperation50.setOption("inputName","uploadFileSlotToPackageRequest");
        _uploadFileSlotToPackageOperation50.setOption("buildNum","cf20535.11");
        _uploadFileSlotToPackageOperation50.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _uploadFileSlotToPackageOperation50.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _uploadFileSlotToPackageOperation50.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _uploadFileSlotToPackageIndex50 = 50;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getuploadFileSlotToPackageInvoke50(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_uploadFileSlotToPackageIndex50];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._uploadFileSlotToPackageOperation50);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_uploadFileSlotToPackageIndex50] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public oem.edge.ed.odc.dropbox.common.FileSlot uploadFileSlotToPackage(long arg_0_50, long arg_1_50, long arg_2_50, boolean arg_3_50, byte[] arg_4_50) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getuploadFileSlotToPackageInvoke50(new java.lang.Object[] {new java.lang.Long(arg_0_50), new java.lang.Long(arg_1_50), new java.lang.Long(arg_2_50), new java.lang.Boolean(arg_3_50), arg_4_50}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (oem.edge.ed.odc.dropbox.common.FileSlot) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (oem.edge.ed.odc.dropbox.common.FileSlot) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), oem.edge.ed.odc.dropbox.common.FileSlot.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryFileSlotsOperation51;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params51 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_51"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_51"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params51[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params51[0].setOption("partName","long");
        _params51[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params51[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc51 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryFileSlotsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Vector"), java.util.Vector.class, true, false, false, false, true, false); 
        _returnDesc51.setOption("partQNameString","{http://xml.apache.org/xml-soap}Vector");
        _returnDesc51.setOption("partName","Vector");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults51 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryFileSlotsOperation51 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryFileSlots", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileSlots"), _params51, _returnDesc51, _faults51, "");
        _queryFileSlotsOperation51.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryFileSlotsOperation51.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryFileSlotsOperation51.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryFileSlotsOperation51.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileSlotsResponse"));
        _queryFileSlotsOperation51.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryFileSlotsRequest"));
        _queryFileSlotsOperation51.setOption("outputName","queryFileSlotsResponse");
        _queryFileSlotsOperation51.setOption("inputName","queryFileSlotsRequest");
        _queryFileSlotsOperation51.setOption("buildNum","cf20535.11");
        _queryFileSlotsOperation51.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryFileSlotsOperation51.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryFileSlotsOperation51.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryFileSlotsIndex51 = 51;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryFileSlotsInvoke51(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryFileSlotsIndex51];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryFileSlotsOperation51);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryFileSlotsIndex51] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.Vector queryFileSlots(long arg_0_51, long arg_1_51) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryFileSlotsInvoke51(new java.lang.Object[] {new java.lang.Long(arg_0_51), new java.lang.Long(arg_1_51)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.Vector) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.Vector) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.Vector.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _removeFileSlotOperation52;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params52 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_52"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_52"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_52"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params52[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params52[0].setOption("partName","long");
        _params52[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params52[1].setOption("partName","long");
        _params52[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params52[2].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc52 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults52 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _removeFileSlotOperation52 = new com.ibm.ws.webservices.engine.description.OperationDesc("removeFileSlot", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeFileSlot"), _params52, _returnDesc52, _faults52, "");
        _removeFileSlotOperation52.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _removeFileSlotOperation52.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeFileSlotOperation52.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _removeFileSlotOperation52.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeFileSlotResponse"));
        _removeFileSlotOperation52.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "removeFileSlotRequest"));
        _removeFileSlotOperation52.setOption("outputName","removeFileSlotResponse");
        _removeFileSlotOperation52.setOption("inputName","removeFileSlotRequest");
        _removeFileSlotOperation52.setOption("buildNum","cf20535.11");
        _removeFileSlotOperation52.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _removeFileSlotOperation52.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _removeFileSlotOperation52.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _removeFileSlotIndex52 = 52;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getremoveFileSlotInvoke52(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_removeFileSlotIndex52];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._removeFileSlotOperation52);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_removeFileSlotIndex52] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void removeFileSlot(long arg_0_52, long arg_1_52, long arg_2_52) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getremoveFileSlotInvoke52(new java.lang.Object[] {new java.lang.Long(arg_0_52), new java.lang.Long(arg_1_52), new java.lang.Long(arg_2_52)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _releaseFileSlotOperation53;
    static {
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
        _releaseFileSlotOperation53 = new com.ibm.ws.webservices.engine.description.OperationDesc("releaseFileSlot", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "releaseFileSlot"), _params53, _returnDesc53, _faults53, "");
        _releaseFileSlotOperation53.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _releaseFileSlotOperation53.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _releaseFileSlotOperation53.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _releaseFileSlotOperation53.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "releaseFileSlotResponse"));
        _releaseFileSlotOperation53.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "releaseFileSlotRequest"));
        _releaseFileSlotOperation53.setOption("outputName","releaseFileSlotResponse");
        _releaseFileSlotOperation53.setOption("inputName","releaseFileSlotRequest");
        _releaseFileSlotOperation53.setOption("buildNum","cf20535.11");
        _releaseFileSlotOperation53.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _releaseFileSlotOperation53.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _releaseFileSlotOperation53.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _releaseFileSlotIndex53 = 53;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getreleaseFileSlotInvoke53(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_releaseFileSlotIndex53];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._releaseFileSlotOperation53);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_releaseFileSlotIndex53] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void releaseFileSlot(long arg_0_53, long arg_1_53, long arg_2_53) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getreleaseFileSlotInvoke53(new java.lang.Object[] {new java.lang.Long(arg_0_53), new java.lang.Long(arg_1_53), new java.lang.Long(arg_2_53)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _registerAuditInformationOperation54;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params54 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_4_54"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params54[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params54[0].setOption("partName","long");
        _params54[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params54[1].setOption("partName","long");
        _params54[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params54[2].setOption("partName","long");
        _params54[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params54[3].setOption("partName","long");
        _params54[4].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params54[4].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc54 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults54 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _registerAuditInformationOperation54 = new com.ibm.ws.webservices.engine.description.OperationDesc("registerAuditInformation", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "registerAuditInformation"), _params54, _returnDesc54, _faults54, "");
        _registerAuditInformationOperation54.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _registerAuditInformationOperation54.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _registerAuditInformationOperation54.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _registerAuditInformationOperation54.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "registerAuditInformationResponse"));
        _registerAuditInformationOperation54.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "registerAuditInformationRequest"));
        _registerAuditInformationOperation54.setOption("outputName","registerAuditInformationResponse");
        _registerAuditInformationOperation54.setOption("inputName","registerAuditInformationRequest");
        _registerAuditInformationOperation54.setOption("buildNum","cf20535.11");
        _registerAuditInformationOperation54.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _registerAuditInformationOperation54.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _registerAuditInformationOperation54.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _registerAuditInformationIndex54 = 54;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getregisterAuditInformationInvoke54(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_registerAuditInformationIndex54];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._registerAuditInformationOperation54);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_registerAuditInformationIndex54] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void registerAuditInformation(long arg_0_54, long arg_1_54, long arg_2_54, long arg_3_54, boolean arg_4_54) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getregisterAuditInformationInvoke54(new java.lang.Object[] {new java.lang.Long(arg_0_54), new java.lang.Long(arg_1_54), new java.lang.Long(arg_2_54), new java.lang.Long(arg_3_54), new java.lang.Boolean(arg_4_54)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _commitUploadedFileOperation55;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params55 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_55"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_55"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_55"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_55"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params55[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params55[0].setOption("partName","long");
        _params55[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params55[1].setOption("partName","long");
        _params55[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params55[2].setOption("partName","long");
        _params55[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params55[3].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc55 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults55 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _commitUploadedFileOperation55 = new com.ibm.ws.webservices.engine.description.OperationDesc("commitUploadedFile", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitUploadedFile"), _params55, _returnDesc55, _faults55, "");
        _commitUploadedFileOperation55.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _commitUploadedFileOperation55.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _commitUploadedFileOperation55.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _commitUploadedFileOperation55.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitUploadedFileResponse"));
        _commitUploadedFileOperation55.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "commitUploadedFileRequest"));
        _commitUploadedFileOperation55.setOption("outputName","commitUploadedFileResponse");
        _commitUploadedFileOperation55.setOption("inputName","commitUploadedFileRequest");
        _commitUploadedFileOperation55.setOption("buildNum","cf20535.11");
        _commitUploadedFileOperation55.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _commitUploadedFileOperation55.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _commitUploadedFileOperation55.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _commitUploadedFileIndex55 = 55;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcommitUploadedFileInvoke55(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_commitUploadedFileIndex55];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._commitUploadedFileOperation55);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_commitUploadedFileIndex55] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void commitUploadedFile(long arg_0_55, long arg_1_55, long arg_2_55, java.lang.String arg_3_55) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getcommitUploadedFileInvoke55(new java.lang.Object[] {new java.lang.Long(arg_0_55), new java.lang.Long(arg_1_55), new java.lang.Long(arg_2_55), arg_3_55}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _downloadPackageOperation56;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params56 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_56"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_56"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params56[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params56[0].setOption("partName","long");
        _params56[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params56[1].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc56 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "downloadPackageReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://schemas.xmlsoap.org/soap/encoding/", "base64"), byte[].class, true, false, false, false, true, false); 
        _returnDesc56.setOption("partQNameString","{http://schemas.xmlsoap.org/soap/encoding/}base64");
        _returnDesc56.setOption("partName","base64");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults56 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _downloadPackageOperation56 = new com.ibm.ws.webservices.engine.description.OperationDesc("downloadPackage", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackage"), _params56, _returnDesc56, _faults56, "");
        _downloadPackageOperation56.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _downloadPackageOperation56.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _downloadPackageOperation56.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _downloadPackageOperation56.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageResponse"));
        _downloadPackageOperation56.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageRequest"));
        _downloadPackageOperation56.setOption("outputName","downloadPackageResponse");
        _downloadPackageOperation56.setOption("inputName","downloadPackageRequest");
        _downloadPackageOperation56.setOption("buildNum","cf20535.11");
        _downloadPackageOperation56.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _downloadPackageOperation56.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _downloadPackageOperation56.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _downloadPackageIndex56 = 56;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getdownloadPackageInvoke56(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_downloadPackageIndex56];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._downloadPackageOperation56);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_downloadPackageIndex56] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public byte[] downloadPackage(long arg_0_56, java.lang.String arg_1_56) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getdownloadPackageInvoke56(new java.lang.Object[] {new java.lang.Long(arg_0_56), arg_1_56}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (byte[]) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (byte[]) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), byte[].class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _downloadPackageItemOperation57;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params57 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_57"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_57"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params57[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params57[0].setOption("partName","long");
        _params57[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params57[1].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc57 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "downloadPackageItemReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://schemas.xmlsoap.org/soap/encoding/", "base64"), byte[].class, true, false, false, false, true, false); 
        _returnDesc57.setOption("partQNameString","{http://schemas.xmlsoap.org/soap/encoding/}base64");
        _returnDesc57.setOption("partName","base64");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults57 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _downloadPackageItemOperation57 = new com.ibm.ws.webservices.engine.description.OperationDesc("downloadPackageItem", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItem"), _params57, _returnDesc57, _faults57, "");
        _downloadPackageItemOperation57.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _downloadPackageItemOperation57.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _downloadPackageItemOperation57.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _downloadPackageItemOperation57.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItemResponse"));
        _downloadPackageItemOperation57.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItemRequest"));
        _downloadPackageItemOperation57.setOption("outputName","downloadPackageItemResponse");
        _downloadPackageItemOperation57.setOption("inputName","downloadPackageItemRequest");
        _downloadPackageItemOperation57.setOption("buildNum","cf20535.11");
        _downloadPackageItemOperation57.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _downloadPackageItemOperation57.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _downloadPackageItemOperation57.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _downloadPackageItemIndex57 = 57;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getdownloadPackageItemInvoke57(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_downloadPackageItemIndex57];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._downloadPackageItemOperation57);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_downloadPackageItemIndex57] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public byte[] downloadPackageItem(long arg_0_57, long arg_1_57) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getdownloadPackageItemInvoke57(new java.lang.Object[] {new java.lang.Long(arg_0_57), new java.lang.Long(arg_1_57)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (byte[]) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (byte[]) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), byte[].class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _downloadPackageItemOperation58;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params58 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_58"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_58"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_58"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_58"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params58[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params58[0].setOption("partName","long");
        _params58[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params58[1].setOption("partName","long");
        _params58[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params58[2].setOption("partName","long");
        _params58[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params58[3].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc58 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "downloadPackageItemReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://schemas.xmlsoap.org/soap/encoding/", "base64"), byte[].class, true, false, false, false, true, false); 
        _returnDesc58.setOption("partQNameString","{http://schemas.xmlsoap.org/soap/encoding/}base64");
        _returnDesc58.setOption("partName","base64");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults58 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _downloadPackageItemOperation58 = new com.ibm.ws.webservices.engine.description.OperationDesc("downloadPackageItem", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItem"), _params58, _returnDesc58, _faults58, "");
        _downloadPackageItemOperation58.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _downloadPackageItemOperation58.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _downloadPackageItemOperation58.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _downloadPackageItemOperation58.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItemResponse1"));
        _downloadPackageItemOperation58.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "downloadPackageItemRequest1"));
        _downloadPackageItemOperation58.setOption("outputName","downloadPackageItemResponse1");
        _downloadPackageItemOperation58.setOption("inputName","downloadPackageItemRequest1");
        _downloadPackageItemOperation58.setOption("buildNum","cf20535.11");
        _downloadPackageItemOperation58.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _downloadPackageItemOperation58.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _downloadPackageItemOperation58.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _downloadPackageItemIndex58 = 58;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getdownloadPackageItemInvoke58(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_downloadPackageItemIndex58];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._downloadPackageItemOperation58);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_downloadPackageItemIndex58] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public byte[] downloadPackageItem(long arg_0_58, long arg_1_58, long arg_2_58, long arg_3_58) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getdownloadPackageItemInvoke58(new java.lang.Object[] {new java.lang.Long(arg_0_58), new java.lang.Long(arg_1_58), new java.lang.Long(arg_2_58), new java.lang.Long(arg_3_58)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (byte[]) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (byte[]) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), byte[].class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _getPackageItemMD5Operation59;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params59 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_59"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_59"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_59"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false, false, false, true, false), 
          };
        _params59[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params59[0].setOption("partName","long");
        _params59[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params59[1].setOption("partName","long");
        _params59[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}long");
        _params59[2].setOption("partName","long");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc59 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "getPackageItemMD5Return"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "ArrayOf_xsd_nillable_string"), java.lang.String[].class, true, false, false, false, true, false); 
        _returnDesc59.setOption("partQNameString","{http://service.dropbox.odc.ed.edge.oem}ArrayOf_xsd_nillable_string");
        _returnDesc59.setOption("partName","ArrayOf_xsd_nillable_string");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults59 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _getPackageItemMD5Operation59 = new com.ibm.ws.webservices.engine.description.OperationDesc("getPackageItemMD5", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getPackageItemMD5"), _params59, _returnDesc59, _faults59, "");
        _getPackageItemMD5Operation59.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _getPackageItemMD5Operation59.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getPackageItemMD5Operation59.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _getPackageItemMD5Operation59.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getPackageItemMD5Response"));
        _getPackageItemMD5Operation59.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "getPackageItemMD5Request"));
        _getPackageItemMD5Operation59.setOption("outputName","getPackageItemMD5Response");
        _getPackageItemMD5Operation59.setOption("inputName","getPackageItemMD5Request");
        _getPackageItemMD5Operation59.setOption("buildNum","cf20535.11");
        _getPackageItemMD5Operation59.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _getPackageItemMD5Operation59.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _getPackageItemMD5Operation59.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _getPackageItemMD5Index59 = 59;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getgetPackageItemMD5Invoke59(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_getPackageItemMD5Index59];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._getPackageItemMD5Operation59);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_getPackageItemMD5Index59] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.lang.String[] getPackageItemMD5(long arg_0_59, long arg_1_59, long arg_2_59) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getgetPackageItemMD5Invoke59(new java.lang.Object[] {new java.lang.Long(arg_0_59), new java.lang.Long(arg_1_59), new java.lang.Long(arg_2_59)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.lang.String[]) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.lang.String[]) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.lang.String[].class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _createGroupOperation60;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params60 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_60"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_60"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_60"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
          };
        _params60[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params60[0].setOption("partName","string");
        _params60[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params60[1].setOption("partName","byte");
        _params60[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params60[2].setOption("partName","byte");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc60 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults60 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _createGroupOperation60 = new com.ibm.ws.webservices.engine.description.OperationDesc("createGroup", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroup"), _params60, _returnDesc60, _faults60, "");
        _createGroupOperation60.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _createGroupOperation60.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createGroupOperation60.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _createGroupOperation60.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroupResponse"));
        _createGroupOperation60.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroupRequest"));
        _createGroupOperation60.setOption("outputName","createGroupResponse");
        _createGroupOperation60.setOption("inputName","createGroupRequest");
        _createGroupOperation60.setOption("buildNum","cf20535.11");
        _createGroupOperation60.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createGroupOperation60.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _createGroupOperation60.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _createGroupIndex60 = 60;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcreateGroupInvoke60(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_createGroupIndex60];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._createGroupOperation60);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_createGroupIndex60] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void createGroup(java.lang.String arg_0_60, byte arg_1_60, byte arg_2_60) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getcreateGroupInvoke60(new java.lang.Object[] {arg_0_60, new java.lang.Byte(arg_1_60), new java.lang.Byte(arg_2_60)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _createGroupOperation61;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params61 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_61"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params61[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params61[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc61 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults61 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _createGroupOperation61 = new com.ibm.ws.webservices.engine.description.OperationDesc("createGroup", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroup"), _params61, _returnDesc61, _faults61, "");
        _createGroupOperation61.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _createGroupOperation61.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createGroupOperation61.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _createGroupOperation61.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroupResponse1"));
        _createGroupOperation61.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "createGroupRequest1"));
        _createGroupOperation61.setOption("outputName","createGroupResponse1");
        _createGroupOperation61.setOption("inputName","createGroupRequest1");
        _createGroupOperation61.setOption("buildNum","cf20535.11");
        _createGroupOperation61.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _createGroupOperation61.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _createGroupOperation61.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _createGroupIndex61 = 61;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getcreateGroupInvoke61(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_createGroupIndex61];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._createGroupOperation61);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_createGroupIndex61] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void createGroup(java.lang.String arg_0_61) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getcreateGroupInvoke61(new java.lang.Object[] {arg_0_61}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _deleteGroupOperation62;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params62 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_62"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params62[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params62[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc62 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults62 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _deleteGroupOperation62 = new com.ibm.ws.webservices.engine.description.OperationDesc("deleteGroup", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deleteGroup"), _params62, _returnDesc62, _faults62, "");
        _deleteGroupOperation62.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _deleteGroupOperation62.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _deleteGroupOperation62.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _deleteGroupOperation62.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deleteGroupResponse"));
        _deleteGroupOperation62.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "deleteGroupRequest"));
        _deleteGroupOperation62.setOption("outputName","deleteGroupResponse");
        _deleteGroupOperation62.setOption("inputName","deleteGroupRequest");
        _deleteGroupOperation62.setOption("buildNum","cf20535.11");
        _deleteGroupOperation62.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _deleteGroupOperation62.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _deleteGroupOperation62.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _deleteGroupIndex62 = 62;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getdeleteGroupInvoke62(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_deleteGroupIndex62];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._deleteGroupOperation62);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_deleteGroupIndex62] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void deleteGroup(java.lang.String arg_0_62) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getdeleteGroupInvoke62(new java.lang.Object[] {arg_0_62}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _modifyGroupAttributesOperation63;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params63 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_63"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_63"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_63"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "byte"), byte.class, false, false, false, false, true, false), 
          };
        _params63[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params63[0].setOption("partName","string");
        _params63[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params63[1].setOption("partName","byte");
        _params63[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}byte");
        _params63[2].setOption("partName","byte");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc63 = new com.ibm.ws.webservices.engine.description.ParameterDesc(null, com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://websphere.ibm.com/webservices/", "Void"), void.class, true, false, false, false, true, true); 
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults63 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _modifyGroupAttributesOperation63 = new com.ibm.ws.webservices.engine.description.OperationDesc("modifyGroupAttributes", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "modifyGroupAttributes"), _params63, _returnDesc63, _faults63, "");
        _modifyGroupAttributesOperation63.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _modifyGroupAttributesOperation63.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _modifyGroupAttributesOperation63.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _modifyGroupAttributesOperation63.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "modifyGroupAttributesResponse"));
        _modifyGroupAttributesOperation63.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "modifyGroupAttributesRequest"));
        _modifyGroupAttributesOperation63.setOption("outputName","modifyGroupAttributesResponse");
        _modifyGroupAttributesOperation63.setOption("inputName","modifyGroupAttributesRequest");
        _modifyGroupAttributesOperation63.setOption("buildNum","cf20535.11");
        _modifyGroupAttributesOperation63.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _modifyGroupAttributesOperation63.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _modifyGroupAttributesOperation63.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _modifyGroupAttributesIndex63 = 63;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getmodifyGroupAttributesInvoke63(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_modifyGroupAttributesIndex63];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._modifyGroupAttributesOperation63);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_modifyGroupAttributesIndex63] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public void modifyGroupAttributes(java.lang.String arg_0_63, byte arg_1_63, byte arg_2_63) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        try {
            _getmodifyGroupAttributesInvoke63(new java.lang.Object[] {arg_0_63, new java.lang.Byte(arg_1_63), new java.lang.Byte(arg_2_63)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryGroupsOperation64;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params64 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_64"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_64"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_2_64"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_3_64"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params64[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params64[0].setOption("partName","string");
        _params64[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params64[1].setOption("partName","boolean");
        _params64[2].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params64[2].setOption("partName","boolean");
        _params64[3].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params64[3].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc64 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryGroupsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc64.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc64.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults64 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryGroupsOperation64 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryGroups", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroups"), _params64, _returnDesc64, _faults64, "");
        _queryGroupsOperation64.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryGroupsOperation64.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryGroupsOperation64.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryGroupsOperation64.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupsResponse"));
        _queryGroupsOperation64.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupsRequest"));
        _queryGroupsOperation64.setOption("outputName","queryGroupsResponse");
        _queryGroupsOperation64.setOption("inputName","queryGroupsRequest");
        _queryGroupsOperation64.setOption("buildNum","cf20535.11");
        _queryGroupsOperation64.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryGroupsOperation64.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryGroupsOperation64.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryGroupsIndex64 = 64;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryGroupsInvoke64(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryGroupsIndex64];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryGroupsOperation64);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryGroupsIndex64] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.HashMap queryGroups(java.lang.String arg_0_64, boolean arg_1_64, boolean arg_2_64, boolean arg_3_64) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryGroupsInvoke64(new java.lang.Object[] {arg_0_64, new java.lang.Boolean(arg_1_64), new java.lang.Boolean(arg_2_64), new java.lang.Boolean(arg_3_64)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.HashMap) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.HashMap) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.HashMap.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryGroupsOperation65;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params65 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_65"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_1_65"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false, false, false, true, false), 
          };
        _params65[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params65[0].setOption("partName","boolean");
        _params65[1].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}boolean");
        _params65[1].setOption("partName","boolean");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc65 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryGroupsReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, true, false, false, false, true, false); 
        _returnDesc65.setOption("partQNameString","{http://xml.apache.org/xml-soap}Map");
        _returnDesc65.setOption("partName","Map");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults65 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryGroupsOperation65 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryGroups", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroups"), _params65, _returnDesc65, _faults65, "");
        _queryGroupsOperation65.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryGroupsOperation65.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryGroupsOperation65.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryGroupsOperation65.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupsResponse1"));
        _queryGroupsOperation65.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupsRequest1"));
        _queryGroupsOperation65.setOption("outputName","queryGroupsResponse1");
        _queryGroupsOperation65.setOption("inputName","queryGroupsRequest1");
        _queryGroupsOperation65.setOption("buildNum","cf20535.11");
        _queryGroupsOperation65.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryGroupsOperation65.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryGroupsOperation65.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryGroupsIndex65 = 65;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryGroupsInvoke65(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryGroupsIndex65];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryGroupsOperation65);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryGroupsIndex65] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public java.util.HashMap queryGroups(boolean arg_0_65, boolean arg_1_65) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryGroupsInvoke65(new java.lang.Object[] {new java.lang.Boolean(arg_0_65), new java.lang.Boolean(arg_1_65)}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (java.util.HashMap) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (java.util.HashMap) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), java.util.HashMap.class);
        }
    }

    private static final com.ibm.ws.webservices.engine.description.OperationDesc _queryGroupOperation66;
    static {
        com.ibm.ws.webservices.engine.description.ParameterDesc[]  _params66 = new com.ibm.ws.webservices.engine.description.ParameterDesc[] {
         new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "arg_0_66"), com.ibm.ws.webservices.engine.description.ParameterDesc.IN, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false, false, false, true, false), 
          };
        _params66[0].setOption("partQNameString","{http://www.w3.org/2001/XMLSchema}string");
        _params66[0].setOption("partName","string");
        com.ibm.ws.webservices.engine.description.ParameterDesc  _returnDesc66 = new com.ibm.ws.webservices.engine.description.ParameterDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "queryGroupReturn"), com.ibm.ws.webservices.engine.description.ParameterDesc.OUT, com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "GroupInfo"), oem.edge.ed.odc.dsmp.common.GroupInfo.class, true, false, false, false, true, false); 
        _returnDesc66.setOption("partQNameString","{http://common.dsmp.odc.ed.edge.oem}GroupInfo");
        _returnDesc66.setOption("partName","GroupInfo");
        com.ibm.ws.webservices.engine.description.FaultDesc[]  _faults66 = new com.ibm.ws.webservices.engine.description.FaultDesc[] {
         new com.ibm.ws.webservices.engine.description.FaultDesc(com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DboxException"), "oem.edge.ed.odc.dsmp.common.DboxException", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("", "fault"), com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://common.dsmp.odc.ed.edge.oem", "DboxException")), 
          };
        _queryGroupOperation66 = new com.ibm.ws.webservices.engine.description.OperationDesc("queryGroup", com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroup"), _params66, _returnDesc66, _faults66, "");
        _queryGroupOperation66.setOption("targetNamespace","http://service.dropbox.odc.ed.edge.oem");
        _queryGroupOperation66.setOption("outputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryGroupOperation66.setOption("portTypeQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccess"));
        _queryGroupOperation66.setOption("outputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupResponse"));
        _queryGroupOperation66.setOption("inputMessageQName",com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "queryGroupRequest"));
        _queryGroupOperation66.setOption("outputName","queryGroupResponse");
        _queryGroupOperation66.setOption("inputName","queryGroupRequest");
        _queryGroupOperation66.setOption("buildNum","cf20535.11");
        _queryGroupOperation66.setOption("inputEncodingStyle","http://schemas.xmlsoap.org/soap/encoding/");
        _queryGroupOperation66.setUse(com.ibm.ws.webservices.engine.enum.Use.ENCODED);
        _queryGroupOperation66.setStyle(com.ibm.ws.webservices.engine.enum.Style.RPC);
    }

    private int _queryGroupIndex66 = 66;
    private synchronized com.ibm.ws.webservices.engine.client.Stub.Invoke _getqueryGroupInvoke66(Object[] _parameters) throws com.ibm.ws.webservices.engine.WebServicesFault  {
        com.ibm.ws.webservices.engine.MessageContext _mc = super.messageContexts[_queryGroupIndex66];
        if (_mc == null) {
            _mc = new com.ibm.ws.webservices.engine.MessageContext(super.engine);
            _mc.setOperation(DropboxAccessWebSvcSoapBindingStub._queryGroupOperation66);
            _mc.setUseSOAPAction(true);
            _mc.setSOAPActionURI("");
            super.primeMessageContext(_mc);
            super.messageContexts[_queryGroupIndex66] = _mc;
        }
        try {
            _mc = (com.ibm.ws.webservices.engine.MessageContext) _mc.clone();
        }
        catch (CloneNotSupportedException _cnse) {
            throw com.ibm.ws.webservices.engine.WebServicesFault.makeFault(_cnse);
        }
        return new com.ibm.ws.webservices.engine.client.Stub.Invoke(connection, _mc, _parameters);
    }

    public oem.edge.ed.odc.dsmp.common.GroupInfo queryGroup(java.lang.String arg_0_66) throws java.rmi.RemoteException, oem.edge.ed.odc.dsmp.common.DboxException {
        if (super.cachedEndpoint == null) {
            throw new com.ibm.ws.webservices.engine.NoEndPointException();
        }
        java.util.Vector _resp = null;
        try {
            _resp = _getqueryGroupInvoke66(new java.lang.Object[] {arg_0_66}).invoke();

        } catch (com.ibm.ws.webservices.engine.WebServicesFault _wsf) {
            Exception _e = _wsf.getUserException();
            if (_e != null) {
                if (_e instanceof oem.edge.ed.odc.dsmp.common.DboxException) {
                    throw (oem.edge.ed.odc.dsmp.common.DboxException) _e;
                }
            }
            throw _wsf;
        } 
        try {
            return (oem.edge.ed.odc.dsmp.common.GroupInfo) ((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue();
        } catch (java.lang.Exception _exception) {
            return (oem.edge.ed.odc.dsmp.common.GroupInfo) super.convert(((com.ibm.ws.webservices.engine.xmlsoap.ext.ParamValue) _resp.get(0)).getValue(), oem.edge.ed.odc.dsmp.common.GroupInfo.class);
        }
    }

}
