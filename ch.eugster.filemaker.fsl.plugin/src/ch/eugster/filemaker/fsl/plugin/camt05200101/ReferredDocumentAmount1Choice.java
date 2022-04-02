//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:54 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ReferredDocumentAmount1Choice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReferredDocumentAmount1Choice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="DuePyblAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount"/>
 *           &lt;element name="DscntApldAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount"/>
 *           &lt;element name="RmtdAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount"/>
 *           &lt;element name="CdtNoteAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount"/>
 *           &lt;element name="TaxAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferredDocumentAmount1Choice", propOrder = {
    "duePyblAmt",
    "dscntApldAmt",
    "rmtdAmt",
    "cdtNoteAmt",
    "taxAmt"
})
public class ReferredDocumentAmount1Choice {

    @XmlElement(name = "DuePyblAmt")
    protected CurrencyAndAmount duePyblAmt;
    @XmlElement(name = "DscntApldAmt")
    protected CurrencyAndAmount dscntApldAmt;
    @XmlElement(name = "RmtdAmt")
    protected CurrencyAndAmount rmtdAmt;
    @XmlElement(name = "CdtNoteAmt")
    protected CurrencyAndAmount cdtNoteAmt;
    @XmlElement(name = "TaxAmt")
    protected CurrencyAndAmount taxAmt;

    /**
     * Ruft den Wert der duePyblAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getDuePyblAmt() {
        return duePyblAmt;
    }

    /**
     * Legt den Wert der duePyblAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setDuePyblAmt(CurrencyAndAmount value) {
        this.duePyblAmt = value;
    }

    /**
     * Ruft den Wert der dscntApldAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getDscntApldAmt() {
        return dscntApldAmt;
    }

    /**
     * Legt den Wert der dscntApldAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setDscntApldAmt(CurrencyAndAmount value) {
        this.dscntApldAmt = value;
    }

    /**
     * Ruft den Wert der rmtdAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getRmtdAmt() {
        return rmtdAmt;
    }

    /**
     * Legt den Wert der rmtdAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setRmtdAmt(CurrencyAndAmount value) {
        this.rmtdAmt = value;
    }

    /**
     * Ruft den Wert der cdtNoteAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getCdtNoteAmt() {
        return cdtNoteAmt;
    }

    /**
     * Legt den Wert der cdtNoteAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setCdtNoteAmt(CurrencyAndAmount value) {
        this.cdtNoteAmt = value;
    }

    /**
     * Ruft den Wert der taxAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getTaxAmt() {
        return taxAmt;
    }

    /**
     * Legt den Wert der taxAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setTaxAmt(CurrencyAndAmount value) {
        this.taxAmt = value;
    }

}
