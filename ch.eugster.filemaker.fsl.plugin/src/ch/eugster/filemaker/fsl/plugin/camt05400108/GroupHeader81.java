//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:56:34 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05400108;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für GroupHeader81 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="GroupHeader81">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MsgId" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}Max35Text"/>
 *         &lt;element name="CreDtTm" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}ISODateTime"/>
 *         &lt;element name="MsgRcpt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}PartyIdentification135" minOccurs="0"/>
 *         &lt;element name="MsgPgntn" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}Pagination1" minOccurs="0"/>
 *         &lt;element name="OrgnlBizQry" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}OriginalBusinessQuery1" minOccurs="0"/>
 *         &lt;element name="AddtlInf" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.08}Max500Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupHeader81", propOrder = {
    "msgId",
    "creDtTm",
    "msgRcpt",
    "msgPgntn",
    "orgnlBizQry",
    "addtlInf"
})
public class GroupHeader81 {

    @XmlElement(name = "MsgId", required = true)
    protected String msgId;
    @XmlElement(name = "CreDtTm", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creDtTm;
    @XmlElement(name = "MsgRcpt")
    protected PartyIdentification135 msgRcpt;
    @XmlElement(name = "MsgPgntn")
    protected Pagination1 msgPgntn;
    @XmlElement(name = "OrgnlBizQry")
    protected OriginalBusinessQuery1 orgnlBizQry;
    @XmlElement(name = "AddtlInf")
    protected String addtlInf;

    /**
     * Ruft den Wert der msgId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Legt den Wert der msgId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgId(String value) {
        this.msgId = value;
    }

    /**
     * Ruft den Wert der creDtTm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreDtTm() {
        return creDtTm;
    }

    /**
     * Legt den Wert der creDtTm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreDtTm(XMLGregorianCalendar value) {
        this.creDtTm = value;
    }

    /**
     * Ruft den Wert der msgRcpt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification135 }
     *     
     */
    public PartyIdentification135 getMsgRcpt() {
        return msgRcpt;
    }

    /**
     * Legt den Wert der msgRcpt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification135 }
     *     
     */
    public void setMsgRcpt(PartyIdentification135 value) {
        this.msgRcpt = value;
    }

    /**
     * Ruft den Wert der msgPgntn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Pagination1 }
     *     
     */
    public Pagination1 getMsgPgntn() {
        return msgPgntn;
    }

    /**
     * Legt den Wert der msgPgntn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Pagination1 }
     *     
     */
    public void setMsgPgntn(Pagination1 value) {
        this.msgPgntn = value;
    }

    /**
     * Ruft den Wert der orgnlBizQry-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OriginalBusinessQuery1 }
     *     
     */
    public OriginalBusinessQuery1 getOrgnlBizQry() {
        return orgnlBizQry;
    }

    /**
     * Legt den Wert der orgnlBizQry-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginalBusinessQuery1 }
     *     
     */
    public void setOrgnlBizQry(OriginalBusinessQuery1 value) {
        this.orgnlBizQry = value;
    }

    /**
     * Ruft den Wert der addtlInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddtlInf() {
        return addtlInf;
    }

    /**
     * Legt den Wert der addtlInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddtlInf(String value) {
        this.addtlInf = value;
    }

}
