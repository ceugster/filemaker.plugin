//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:54 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für TaxInformation2 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TaxInformation2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CdtrTaxId" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max35Text" minOccurs="0"/>
 *         &lt;element name="CdtrTaxTp" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max35Text" minOccurs="0"/>
 *         &lt;element name="DbtrTaxId" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max35Text" minOccurs="0"/>
 *         &lt;element name="TaxRefNb" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max140Text" minOccurs="0"/>
 *         &lt;element name="TtlTaxblBaseAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount" minOccurs="0"/>
 *         &lt;element name="TtlTaxAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount" minOccurs="0"/>
 *         &lt;element name="TaxDt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}ISODate" minOccurs="0"/>
 *         &lt;element name="TaxTpInf" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}TaxDetails" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaxInformation2", propOrder = {
    "cdtrTaxId",
    "cdtrTaxTp",
    "dbtrTaxId",
    "taxRefNb",
    "ttlTaxblBaseAmt",
    "ttlTaxAmt",
    "taxDt",
    "taxTpInf"
})
public class TaxInformation2 {

    @XmlElement(name = "CdtrTaxId")
    protected String cdtrTaxId;
    @XmlElement(name = "CdtrTaxTp")
    protected String cdtrTaxTp;
    @XmlElement(name = "DbtrTaxId")
    protected String dbtrTaxId;
    @XmlElement(name = "TaxRefNb")
    protected String taxRefNb;
    @XmlElement(name = "TtlTaxblBaseAmt")
    protected CurrencyAndAmount ttlTaxblBaseAmt;
    @XmlElement(name = "TtlTaxAmt")
    protected CurrencyAndAmount ttlTaxAmt;
    @XmlElement(name = "TaxDt")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar taxDt;
    @XmlElement(name = "TaxTpInf")
    protected List<TaxDetails> taxTpInf;

    /**
     * Ruft den Wert der cdtrTaxId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCdtrTaxId() {
        return cdtrTaxId;
    }

    /**
     * Legt den Wert der cdtrTaxId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCdtrTaxId(String value) {
        this.cdtrTaxId = value;
    }

    /**
     * Ruft den Wert der cdtrTaxTp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCdtrTaxTp() {
        return cdtrTaxTp;
    }

    /**
     * Legt den Wert der cdtrTaxTp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCdtrTaxTp(String value) {
        this.cdtrTaxTp = value;
    }

    /**
     * Ruft den Wert der dbtrTaxId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDbtrTaxId() {
        return dbtrTaxId;
    }

    /**
     * Legt den Wert der dbtrTaxId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDbtrTaxId(String value) {
        this.dbtrTaxId = value;
    }

    /**
     * Ruft den Wert der taxRefNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxRefNb() {
        return taxRefNb;
    }

    /**
     * Legt den Wert der taxRefNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxRefNb(String value) {
        this.taxRefNb = value;
    }

    /**
     * Ruft den Wert der ttlTaxblBaseAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getTtlTaxblBaseAmt() {
        return ttlTaxblBaseAmt;
    }

    /**
     * Legt den Wert der ttlTaxblBaseAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setTtlTaxblBaseAmt(CurrencyAndAmount value) {
        this.ttlTaxblBaseAmt = value;
    }

    /**
     * Ruft den Wert der ttlTaxAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getTtlTaxAmt() {
        return ttlTaxAmt;
    }

    /**
     * Legt den Wert der ttlTaxAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setTtlTaxAmt(CurrencyAndAmount value) {
        this.ttlTaxAmt = value;
    }

    /**
     * Ruft den Wert der taxDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTaxDt() {
        return taxDt;
    }

    /**
     * Legt den Wert der taxDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTaxDt(XMLGregorianCalendar value) {
        this.taxDt = value;
    }

    /**
     * Gets the value of the taxTpInf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taxTpInf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaxTpInf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaxDetails }
     * 
     * 
     */
    public List<TaxDetails> getTaxTpInf() {
        if (taxTpInf == null) {
            taxTpInf = new ArrayList<TaxDetails>();
        }
        return this.taxTpInf;
    }

}
