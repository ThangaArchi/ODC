/**
 * ETSWorkspaceEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */


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

package oem.edge.ets.fe.event;

public class ETSWorkspaceEvent  extends oem.edge.webservice.eventservice.Event  implements java.io.Serializable {
    private java.lang.String identity;
    private java.lang.String projectid;
    private java.lang.String projectdescription;
    private java.lang.String projectname;
    private java.util.Calendar startdate;
    private java.util.Calendar enddate;
    private java.lang.String projectorproposal;
    private java.lang.String company;
    private java.lang.String projectstatus;
    private java.lang.String ibmonly;
    private java.lang.String isitar;
    private java.lang.String projecttype;
    private java.lang.String isprivate;
    private java.lang.String deliveryteam;
    private java.lang.String geography;
    private java.lang.String industry;
    private java.lang.String brand;
    private java.lang.String scesector;
    private java.lang.String sector;
    private java.lang.String subsector;
    private java.lang.String process;

    public ETSWorkspaceEvent() {
    }

    public java.lang.String getIdentity() {
        return identity;
    }

    public void setIdentity(java.lang.String identity) {
        this.identity = identity;
    }

    public java.lang.String getProjectid() {
        return projectid;
    }

    public void setProjectid(java.lang.String projectid) {
        this.projectid = projectid;
    }

    public java.lang.String getProjectdescription() {
        return projectdescription;
    }

    public void setProjectdescription(java.lang.String projectdescription) {
        this.projectdescription = projectdescription;
    }

    public java.lang.String getProjectname() {
        return projectname;
    }

    public void setProjectname(java.lang.String projectname) {
        this.projectname = projectname;
    }

    public java.util.Calendar getStartdate() {
        return startdate;
    }

    public void setStartdate(java.util.Calendar startdate) {
        this.startdate = startdate;
    }

    public java.util.Calendar getEnddate() {
        return enddate;
    }

    public void setEnddate(java.util.Calendar enddate) {
        this.enddate = enddate;
    }

    public java.lang.String getProjectorproposal() {
        return projectorproposal;
    }

    public void setProjectorproposal(java.lang.String projectorproposal) {
        this.projectorproposal = projectorproposal;
    }

    public java.lang.String getCompany() {
        return company;
    }

    public void setCompany(java.lang.String company) {
        this.company = company;
    }

    public java.lang.String getProjectstatus() {
        return projectstatus;
    }

    public void setProjectstatus(java.lang.String projectstatus) {
        this.projectstatus = projectstatus;
    }

    public java.lang.String getIbmonly() {
        return ibmonly;
    }

    public void setIbmonly(java.lang.String ibmonly) {
        this.ibmonly = ibmonly;
    }

    public java.lang.String getIsitar() {
        return isitar;
    }

    public void setIsitar(java.lang.String isitar) {
        this.isitar = isitar;
    }

    public java.lang.String getProjecttype() {
        return projecttype;
    }

    public void setProjecttype(java.lang.String projecttype) {
        this.projecttype = projecttype;
    }

    public java.lang.String getIsprivate() {
        return isprivate;
    }

    public void setIsprivate(java.lang.String isprivate) {
        this.isprivate = isprivate;
    }

    public java.lang.String getDeliveryteam() {
        return deliveryteam;
    }

    public void setDeliveryteam(java.lang.String deliveryteam) {
        this.deliveryteam = deliveryteam;
    }

    public java.lang.String getGeography() {
        return geography;
    }

    public void setGeography(java.lang.String geography) {
        this.geography = geography;
    }

    public java.lang.String getIndustry() {
        return industry;
    }

    public void setIndustry(java.lang.String industry) {
        this.industry = industry;
    }

    public java.lang.String getBrand() {
        return brand;
    }

    public void setBrand(java.lang.String brand) {
        this.brand = brand;
    }

    public java.lang.String getScesector() {
        return scesector;
    }

    public void setScesector(java.lang.String scesector) {
        this.scesector = scesector;
    }

    public java.lang.String getSector() {
        return sector;
    }

    public void setSector(java.lang.String sector) {
        this.sector = sector;
    }

    public java.lang.String getSubsector() {
        return subsector;
    }

    public void setSubsector(java.lang.String subsector) {
        this.subsector = subsector;
    }

    public java.lang.String getProcess() {
        return process;
    }

    public void setProcess(java.lang.String process) {
        this.process = process;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ETSWorkspaceEvent)) return false;
        ETSWorkspaceEvent other = (ETSWorkspaceEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((identity==null && other.getIdentity()==null) || 
             (identity!=null &&
              identity.equals(other.getIdentity()))) &&
            ((projectid==null && other.getProjectid()==null) || 
             (projectid!=null &&
              projectid.equals(other.getProjectid()))) &&
            ((projectdescription==null && other.getProjectdescription()==null) || 
             (projectdescription!=null &&
              projectdescription.equals(other.getProjectdescription()))) &&
            ((projectname==null && other.getProjectname()==null) || 
             (projectname!=null &&
              projectname.equals(other.getProjectname()))) &&
            ((startdate==null && other.getStartdate()==null) || 
             (startdate!=null &&
              startdate.equals(other.getStartdate()))) &&
            ((enddate==null && other.getEnddate()==null) || 
             (enddate!=null &&
              enddate.equals(other.getEnddate()))) &&
            ((projectorproposal==null && other.getProjectorproposal()==null) || 
             (projectorproposal!=null &&
              projectorproposal.equals(other.getProjectorproposal()))) &&
            ((company==null && other.getCompany()==null) || 
             (company!=null &&
              company.equals(other.getCompany()))) &&
            ((projectstatus==null && other.getProjectstatus()==null) || 
             (projectstatus!=null &&
              projectstatus.equals(other.getProjectstatus()))) &&
            ((ibmonly==null && other.getIbmonly()==null) || 
             (ibmonly!=null &&
              ibmonly.equals(other.getIbmonly()))) &&
            ((isitar==null && other.getIsitar()==null) || 
             (isitar!=null &&
              isitar.equals(other.getIsitar()))) &&
            ((projecttype==null && other.getProjecttype()==null) || 
             (projecttype!=null &&
              projecttype.equals(other.getProjecttype()))) &&
            ((isprivate==null && other.getIsprivate()==null) || 
             (isprivate!=null &&
              isprivate.equals(other.getIsprivate()))) &&
            ((deliveryteam==null && other.getDeliveryteam()==null) || 
             (deliveryteam!=null &&
              deliveryteam.equals(other.getDeliveryteam()))) &&
            ((geography==null && other.getGeography()==null) || 
             (geography!=null &&
              geography.equals(other.getGeography()))) &&
            ((industry==null && other.getIndustry()==null) || 
             (industry!=null &&
              industry.equals(other.getIndustry()))) &&
            ((brand==null && other.getBrand()==null) || 
             (brand!=null &&
              brand.equals(other.getBrand()))) &&
            ((scesector==null && other.getScesector()==null) || 
             (scesector!=null &&
              scesector.equals(other.getScesector()))) &&
            ((sector==null && other.getSector()==null) || 
             (sector!=null &&
              sector.equals(other.getSector()))) &&
            ((subsector==null && other.getSubsector()==null) || 
             (subsector!=null &&
              subsector.equals(other.getSubsector()))) &&
            ((process==null && other.getProcess()==null) || 
             (process!=null &&
              process.equals(other.getProcess())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getIdentity() != null) {
            _hashCode += getIdentity().hashCode();
        }
        if (getProjectid() != null) {
            _hashCode += getProjectid().hashCode();
        }
        if (getProjectdescription() != null) {
            _hashCode += getProjectdescription().hashCode();
        }
        if (getProjectname() != null) {
            _hashCode += getProjectname().hashCode();
        }
        if (getStartdate() != null) {
            _hashCode += getStartdate().hashCode();
        }
        if (getEnddate() != null) {
            _hashCode += getEnddate().hashCode();
        }
        if (getProjectorproposal() != null) {
            _hashCode += getProjectorproposal().hashCode();
        }
        if (getCompany() != null) {
            _hashCode += getCompany().hashCode();
        }
        if (getProjectstatus() != null) {
            _hashCode += getProjectstatus().hashCode();
        }
        if (getIbmonly() != null) {
            _hashCode += getIbmonly().hashCode();
        }
        if (getIsitar() != null) {
            _hashCode += getIsitar().hashCode();
        }
        if (getProjecttype() != null) {
            _hashCode += getProjecttype().hashCode();
        }
        if (getIsprivate() != null) {
            _hashCode += getIsprivate().hashCode();
        }
        if (getDeliveryteam() != null) {
            _hashCode += getDeliveryteam().hashCode();
        }
        if (getGeography() != null) {
            _hashCode += getGeography().hashCode();
        }
        if (getIndustry() != null) {
            _hashCode += getIndustry().hashCode();
        }
        if (getBrand() != null) {
            _hashCode += getBrand().hashCode();
        }
        if (getScesector() != null) {
            _hashCode += getScesector().hashCode();
        }
        if (getSector() != null) {
            _hashCode += getSector().hashCode();
        }
        if (getSubsector() != null) {
            _hashCode += getSubsector().hashCode();
        }
        if (getProcess() != null) {
            _hashCode += getProcess().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ETSWorkspaceEvent.class);

    static {
        org.apache.axis.description.FieldDesc field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("identity");
        field.setXmlName(new javax.xml.namespace.QName("", "identity"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("projectid");
        field.setXmlName(new javax.xml.namespace.QName("", "projectid"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("projectdescription");
        field.setXmlName(new javax.xml.namespace.QName("", "projectdescription"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("projectname");
        field.setXmlName(new javax.xml.namespace.QName("", "projectname"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("startdate");
        field.setXmlName(new javax.xml.namespace.QName("", "startdate"));
        field.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("enddate");
        field.setXmlName(new javax.xml.namespace.QName("", "enddate"));
        field.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("projectorproposal");
        field.setXmlName(new javax.xml.namespace.QName("", "projectorproposal"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("company");
        field.setXmlName(new javax.xml.namespace.QName("", "company"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("projectstatus");
        field.setXmlName(new javax.xml.namespace.QName("", "projectstatus"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("ibmonly");
        field.setXmlName(new javax.xml.namespace.QName("", "ibmonly"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("isitar");
        field.setXmlName(new javax.xml.namespace.QName("", "isitar"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("projecttype");
        field.setXmlName(new javax.xml.namespace.QName("", "projecttype"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("isprivate");
        field.setXmlName(new javax.xml.namespace.QName("", "isprivate"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("deliveryteam");
        field.setXmlName(new javax.xml.namespace.QName("", "deliveryteam"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("geography");
        field.setXmlName(new javax.xml.namespace.QName("", "geography"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("industry");
        field.setXmlName(new javax.xml.namespace.QName("", "industry"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("brand");
        field.setXmlName(new javax.xml.namespace.QName("", "brand"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("scesector");
        field.setXmlName(new javax.xml.namespace.QName("", "scesector"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("sector");
        field.setXmlName(new javax.xml.namespace.QName("", "sector"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("subsector");
        field.setXmlName(new javax.xml.namespace.QName("", "subsector"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("process");
        field.setXmlName(new javax.xml.namespace.QName("", "process"));
        field.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        typeDesc.addFieldDesc(field);
    };

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
