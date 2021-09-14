/**
 * DropboxAccessServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf20535.11 v91905174159
 */

package oem.edge.ed.odc.dropbox.service;

public class DropboxAccessServiceLocator extends com.ibm.ws.webservices.multiprotocol.AgnosticService implements com.ibm.ws.webservices.multiprotocol.GeneratedService, oem.edge.ed.odc.dropbox.service.DropboxAccessService {

    public DropboxAccessServiceLocator() {
        super(com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
           "http://service.dropbox.odc.ed.edge.oem",
           "DropboxAccessService"));

        context.setLocatorName("oem.edge.ed.odc.dropbox.service.DropboxAccessServiceLocator");
    }

    public DropboxAccessServiceLocator(com.ibm.ws.webservices.multiprotocol.ServiceContext ctx) {
        super(ctx);
        context.setLocatorName("oem.edge.ed.odc.dropbox.service.DropboxAccessServiceLocator");
    }

    // Use to get a proxy class for dropboxAccessWebSvc
    private final java.lang.String dropboxAccessWebSvc_address = "http://edesign5.fishkill.ibm.com/technologyconnect/dev/DropboxAccessWebSvc";

    public java.lang.String getDropboxAccessWebSvcAddress() {
        if (context.getOverriddingEndpointURIs() == null) {
            return dropboxAccessWebSvc_address;
        }
        String overriddingEndpoint = (String) context.getOverriddingEndpointURIs().get("DropboxAccessWebSvc");
        if (overriddingEndpoint != null) {
            return overriddingEndpoint;
        }
        else {
            return dropboxAccessWebSvc_address;
        }
    }

    private java.lang.String dropboxAccessWebSvcPortName = "DropboxAccessWebSvc";

    // The WSDD port name defaults to the port name.
    private java.lang.String dropboxAccessWebSvcWSDDPortName = "DropboxAccessWebSvc";

    public java.lang.String getDropboxAccessWebSvcWSDDPortName() {
        return dropboxAccessWebSvcWSDDPortName;
    }

    public void setDropboxAccessWebSvcWSDDPortName(java.lang.String name) {
        dropboxAccessWebSvcWSDDPortName = name;
    }

    public oem.edge.ed.odc.dropbox.service.DropboxAccess getDropboxAccessWebSvc() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(getDropboxAccessWebSvcAddress());
        }
        catch (java.net.MalformedURLException e) {
            return null; // unlikely as URL was validated in WSDL2Java
        }
        return getDropboxAccessWebSvc(endpoint);
    }

    public oem.edge.ed.odc.dropbox.service.DropboxAccess getDropboxAccessWebSvc(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        oem.edge.ed.odc.dropbox.service.DropboxAccess _stub =
            (oem.edge.ed.odc.dropbox.service.DropboxAccess) getStub(
                dropboxAccessWebSvcPortName,
                (String) getPort2NamespaceMap().get(dropboxAccessWebSvcPortName),
                oem.edge.ed.odc.dropbox.service.DropboxAccess.class,
                "oem.edge.ed.odc.dropbox.service.DropboxAccessWebSvcSoapBindingStub",
                portAddress.toString());
        if (_stub instanceof com.ibm.ws.webservices.engine.client.Stub) {
            ((com.ibm.ws.webservices.engine.client.Stub) _stub).setPortName(dropboxAccessWebSvcWSDDPortName);
        }
        return _stub;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (oem.edge.ed.odc.dropbox.service.DropboxAccess.class.isAssignableFrom(serviceEndpointInterface)) {
                return getDropboxAccessWebSvc();
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("WSWS3273E: Error: There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        String inputPortName = portName.getLocalPart();
        if ("DropboxAccessWebSvc".equals(inputPortName)) {
            return getDropboxAccessWebSvc();
        }
        else  {
            throw new javax.xml.rpc.ServiceException();
        }
    }

    public void setPortNamePrefix(java.lang.String prefix) {
        dropboxAccessWebSvcWSDDPortName = prefix + "/" + dropboxAccessWebSvcPortName;
    }

    public javax.xml.namespace.QName getServiceName() {
        return com.ibm.ws.webservices.engine.utils.QNameTable.createQName("http://service.dropbox.odc.ed.edge.oem", "DropboxAccessService");
    }

    private java.util.Map port2NamespaceMap = null;

    protected synchronized java.util.Map getPort2NamespaceMap() {
        if (port2NamespaceMap == null) {
            port2NamespaceMap = new java.util.HashMap();
            port2NamespaceMap.put(
               "DropboxAccessWebSvc",
               "http://schemas.xmlsoap.org/wsdl/soap/");
        }
        return port2NamespaceMap;
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            String serviceNamespace = getServiceName().getNamespaceURI();
            for (java.util.Iterator i = getPort2NamespaceMap().keySet().iterator(); i.hasNext(); ) {
                ports.add(
                    com.ibm.ws.webservices.engine.utils.QNameTable.createQName(
                        serviceNamespace,
                        (String) i.next()));
            }
        }
        return ports.iterator();
    }

    public javax.xml.rpc.Call[] getCalls(javax.xml.namespace.QName portName) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            throw new javax.xml.rpc.ServiceException("WSWS3062E: Error: portName should not be null.");
        }
        if  (portName.getLocalPart().equals("DropboxAccessWebSvc")) {
            return new javax.xml.rpc.Call[] {
                createCall(portName, "createSession", "createSessionRequest"),
                createCall(portName, "createSession", "createSessionRequest1"),
                createCall(portName, "refreshSession", "refreshSessionRequest"),
                createCall(portName, "closeSession", "closeSessionRequest"),
                createCall(portName, "createPackage", "createPackageRequest"),
                createCall(portName, "createPackage", "createPackageRequest1"),
                createCall(portName, "createPackage", "createPackageRequest2"),
                createCall(portName, "createPackage", "createPackageRequest3"),
                createCall(portName, "getLoginMessage", "getLoginMessageRequest"),
                createCall(portName, "setPackageFlags", "setPackageFlagsRequest"),
                createCall(portName, "deletePackage", "deletePackageRequest"),
                createCall(portName, "commitPackage", "commitPackageRequest"),
                createCall(portName, "markPackage", "markPackageRequest"),
                createCall(portName, "addPackageAcl", "addPackageAclRequest"),
                createCall(portName, "addPackageAcl", "addPackageAclRequest1"),
                createCall(portName, "removePackageAcl", "removePackageAclRequest"),
                createCall(portName, "addUserAcl", "addUserAclRequest"),
                createCall(portName, "addGroupAcl", "addGroupAclRequest"),
                createCall(portName, "addGroupAcl", "addGroupAclRequest1"),
                createCall(portName, "addProjectAcl", "addProjectAclRequest"),
                createCall(portName, "removeUserAcl", "removeUserAclRequest"),
                createCall(portName, "removeGroupAcl", "removeGroupAclRequest"),
                createCall(portName, "removeGroupAcl", "removeGroupAclRequest1"),
                createCall(portName, "removeProjectAcl", "removeProjectAclRequest"),
                createCall(portName, "changePackageExpiration", "changePackageExpirationRequest"),
                createCall(portName, "getStoragePoolInstance", "getStoragePoolInstanceRequest"),
                createCall(portName, "queryStoragePoolInformation", "queryStoragePoolInformationRequest"),
                createCall(portName, "setPackageDescription", "setPackageDescriptionRequest"),
                createCall(portName, "getOptions", "getOptionsRequest"),
                createCall(portName, "getOptions", "getOptionsRequest1"),
                createCall(portName, "getOption", "getOptionRequest"),
                createCall(portName, "setOptions", "setOptionsRequest"),
                createCall(portName, "setOption", "setOptionRequest"),
                createCall(portName, "getProjectList", "getProjectListRequest"),
                createCall(portName, "queryPackages", "queryPackagesRequest"),
                createCall(portName, "queryPackages", "queryPackagesRequest1"),
                createCall(portName, "queryPackage", "queryPackageRequest"),
                createCall(portName, "queryPackageContents", "queryPackageContentsRequest"),
                createCall(portName, "queryFiles", "queryFilesRequest"),
                createCall(portName, "queryFiles", "queryFilesRequest1"),
                createCall(portName, "queryFile", "queryFileRequest"),
                createCall(portName, "queryPackageAcls", "queryPackageAclsRequest"),
                createCall(portName, "queryPackageAclCompanies", "queryPackageAclCompaniesRequest"),
                createCall(portName, "queryRepresentedCompanies", "queryRepresentedCompaniesRequest"),
                createCall(portName, "lookupUser", "lookupUserRequest"),
                createCall(portName, "queryPackageFileAcls", "queryPackageFileAclsRequest"),
                createCall(portName, "addItemToPackage", "addItemToPackageRequest"),
                createCall(portName, "removeItemFromPackage", "removeItemFromPackageRequest"),
                createCall(portName, "uploadFileToPackage", "uploadFileToPackageRequest"),
                createCall(portName, "allocateUploadFileSlot", "allocateUploadFileSlotRequest"),
                createCall(portName, "uploadFileSlotToPackage", "uploadFileSlotToPackageRequest"),
                createCall(portName, "queryFileSlots", "queryFileSlotsRequest"),
                createCall(portName, "removeFileSlot", "removeFileSlotRequest"),
                createCall(portName, "releaseFileSlot", "releaseFileSlotRequest"),
                createCall(portName, "registerAuditInformation", "registerAuditInformationRequest"),
                createCall(portName, "commitUploadedFile", "commitUploadedFileRequest"),
                createCall(portName, "downloadPackage", "downloadPackageRequest"),
                createCall(portName, "downloadPackageItem", "downloadPackageItemRequest"),
                createCall(portName, "downloadPackageItem", "downloadPackageItemRequest1"),
                createCall(portName, "getPackageItemMD5", "getPackageItemMD5Request"),
                createCall(portName, "createGroup", "createGroupRequest"),
                createCall(portName, "createGroup", "createGroupRequest1"),
                createCall(portName, "deleteGroup", "deleteGroupRequest"),
                createCall(portName, "modifyGroupAttributes", "modifyGroupAttributesRequest"),
                createCall(portName, "queryGroups", "queryGroupsRequest"),
                createCall(portName, "queryGroups", "queryGroupsRequest1"),
                createCall(portName, "queryGroup", "queryGroupRequest"),
            };
        }
        else {
            throw new javax.xml.rpc.ServiceException("WSWS3062E: Error: portName should not be null.");
        }
    }
}
