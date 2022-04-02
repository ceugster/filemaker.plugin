//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:54 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200101;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für CurrencyExchange3 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CurrencyExchange3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SrcCcy" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyCode"/>
 *         &lt;element name="TrgtCcy" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyCode" minOccurs="0"/>
 *         &lt;element name="UnitCcy" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyCode" minOccurs="0"/>
 *         &lt;element name="XchgRate" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}BaseOneRate"/>
 *         &lt;element name="CtrctId" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max35Text" minOccurs="0"/>
 *         &lt;element name="QtnDt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}ISODateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurrencyExchange3", propOrder = {
    "srcCcy",
    "trgtCcy",
    "unitCcy",
    "xchgRate",
    "ctrctId",
    "qtnDt"
})
public class CurrencyExchange3 {

    @XmlElement(name = "SrcCcy", required = true)
    protected String srcCcy;
    @XmlElement(name = "TrgtCcy")
    protected String trgtCcy;
    @XmlElement(name = "UnitCcy")
    protected String unitCcy;
    @XmlElement(name = "XchgRate", required = true)
    protected BigDecimal xchgRate;
    @XmlElement(name = "CtrctId")
    protected String ctrctId;
    @XmlElement(name = "QtnDt")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar qtnDt;

    /**
     * Ruft den Wert der srcCcy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcCcy() {
        return srcCcy;
    }

    /**
     * Legt den Wert der srcCcy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcCcy(String value) {
        this.srcCcy = value;
    }

    /**
     * Ruft den Wert der trgtCcy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrgtCcy() {
        return trgtCcy;
    }

    /**
     * Legt den Wert der trgtCcy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrgtCcy(String value) {
        this.trgtCcy = value;
    }

    /**
     * Ruft den Wert der unitCcy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitCcy() {
        return unitCcy;
    }

    /**
     * Legt den Wert der unitCcy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitCcy(String value) {
        this.unitCcy = value;
    }

    /**
     * Ruft den Wert der xchgRate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getXchgRate() {
        return xchgRate;
    }

    /**
     * Legt den Wert der xchgRate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setXchgRate(BigDecimal value) {
        this.xchgRate = value;
    }

    /**
     * Ruft den Wert der ctrctId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtrctId() {
        return ctrctId;
    }

    /**
     * Legt den Wert der ctrctId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtrctId(String value) {
        this.ctrctId = value;
    }

    /**
     * Ruft den Wert der qtnDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getQtnDt() {
        return qtnDt;
    }

    /**
     * Legt den Wert der qtnDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setQtnDt(XMLGregorianCalendar value) {
        this.qtnDt = value;
    }

}
