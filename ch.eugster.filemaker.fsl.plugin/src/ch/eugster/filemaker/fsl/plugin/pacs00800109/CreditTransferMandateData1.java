//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:55:49 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.pacs00800109;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für CreditTransferMandateData1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CreditTransferMandateData1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MndtId" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}Max35Text" minOccurs="0"/>
 *         &lt;element name="Tp" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}MandateTypeInformation2" minOccurs="0"/>
 *         &lt;element name="DtOfSgntr" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISODate" minOccurs="0"/>
 *         &lt;element name="DtOfVrfctn" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISODateTime" minOccurs="0"/>
 *         &lt;element name="ElctrncSgntr" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}Max10KBinary" minOccurs="0"/>
 *         &lt;element name="FrstPmtDt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISODate" minOccurs="0"/>
 *         &lt;element name="FnlPmtDt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISODate" minOccurs="0"/>
 *         &lt;element name="Frqcy" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}Frequency36Choice" minOccurs="0"/>
 *         &lt;element name="Rsn" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}MandateSetupReason1Choice" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditTransferMandateData1", propOrder = {
    "mndtId",
    "tp",
    "dtOfSgntr",
    "dtOfVrfctn",
    "elctrncSgntr",
    "frstPmtDt",
    "fnlPmtDt",
    "frqcy",
    "rsn"
})
public class CreditTransferMandateData1 {

    @XmlElement(name = "MndtId")
    protected String mndtId;
    @XmlElement(name = "Tp")
    protected MandateTypeInformation2 tp;
    @XmlElement(name = "DtOfSgntr")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dtOfSgntr;
    @XmlElement(name = "DtOfVrfctn")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dtOfVrfctn;
    @XmlElement(name = "ElctrncSgntr")
    protected byte[] elctrncSgntr;
    @XmlElement(name = "FrstPmtDt")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar frstPmtDt;
    @XmlElement(name = "FnlPmtDt")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fnlPmtDt;
    @XmlElement(name = "Frqcy")
    protected Frequency36Choice frqcy;
    @XmlElement(name = "Rsn")
    protected MandateSetupReason1Choice rsn;

    /**
     * Ruft den Wert der mndtId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMndtId() {
        return mndtId;
    }

    /**
     * Legt den Wert der mndtId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMndtId(String value) {
        this.mndtId = value;
    }

    /**
     * Ruft den Wert der tp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MandateTypeInformation2 }
     *     
     */
    public MandateTypeInformation2 getTp() {
        return tp;
    }

    /**
     * Legt den Wert der tp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MandateTypeInformation2 }
     *     
     */
    public void setTp(MandateTypeInformation2 value) {
        this.tp = value;
    }

    /**
     * Ruft den Wert der dtOfSgntr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDtOfSgntr() {
        return dtOfSgntr;
    }

    /**
     * Legt den Wert der dtOfSgntr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDtOfSgntr(XMLGregorianCalendar value) {
        this.dtOfSgntr = value;
    }

    /**
     * Ruft den Wert der dtOfVrfctn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDtOfVrfctn() {
        return dtOfVrfctn;
    }

    /**
     * Legt den Wert der dtOfVrfctn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDtOfVrfctn(XMLGregorianCalendar value) {
        this.dtOfVrfctn = value;
    }

    /**
     * Ruft den Wert der elctrncSgntr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getElctrncSgntr() {
        return elctrncSgntr;
    }

    /**
     * Legt den Wert der elctrncSgntr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setElctrncSgntr(byte[] value) {
        this.elctrncSgntr = value;
    }

    /**
     * Ruft den Wert der frstPmtDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFrstPmtDt() {
        return frstPmtDt;
    }

    /**
     * Legt den Wert der frstPmtDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFrstPmtDt(XMLGregorianCalendar value) {
        this.frstPmtDt = value;
    }

    /**
     * Ruft den Wert der fnlPmtDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFnlPmtDt() {
        return fnlPmtDt;
    }

    /**
     * Legt den Wert der fnlPmtDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFnlPmtDt(XMLGregorianCalendar value) {
        this.fnlPmtDt = value;
    }

    /**
     * Ruft den Wert der frqcy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Frequency36Choice }
     *     
     */
    public Frequency36Choice getFrqcy() {
        return frqcy;
    }

    /**
     * Legt den Wert der frqcy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Frequency36Choice }
     *     
     */
    public void setFrqcy(Frequency36Choice value) {
        this.frqcy = value;
    }

    /**
     * Ruft den Wert der rsn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MandateSetupReason1Choice }
     *     
     */
    public MandateSetupReason1Choice getRsn() {
        return rsn;
    }

    /**
     * Legt den Wert der rsn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MandateSetupReason1Choice }
     *     
     */
    public void setRsn(MandateSetupReason1Choice value) {
        this.rsn = value;
    }

}
