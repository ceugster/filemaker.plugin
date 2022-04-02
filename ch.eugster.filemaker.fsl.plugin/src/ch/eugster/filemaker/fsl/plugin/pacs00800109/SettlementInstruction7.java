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


/**
 * <p>Java-Klasse für SettlementInstruction7 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SettlementInstruction7">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SttlmMtd" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}SettlementMethod1Code"/>
 *         &lt;element name="SttlmAcct" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}CashAccount38" minOccurs="0"/>
 *         &lt;element name="ClrSys" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ClearingSystemIdentification3Choice" minOccurs="0"/>
 *         &lt;element name="InstgRmbrsmntAgt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}BranchAndFinancialInstitutionIdentification6" minOccurs="0"/>
 *         &lt;element name="InstgRmbrsmntAgtAcct" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}CashAccount38" minOccurs="0"/>
 *         &lt;element name="InstdRmbrsmntAgt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}BranchAndFinancialInstitutionIdentification6" minOccurs="0"/>
 *         &lt;element name="InstdRmbrsmntAgtAcct" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}CashAccount38" minOccurs="0"/>
 *         &lt;element name="ThrdRmbrsmntAgt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}BranchAndFinancialInstitutionIdentification6" minOccurs="0"/>
 *         &lt;element name="ThrdRmbrsmntAgtAcct" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}CashAccount38" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SettlementInstruction7", propOrder = {
    "sttlmMtd",
    "sttlmAcct",
    "clrSys",
    "instgRmbrsmntAgt",
    "instgRmbrsmntAgtAcct",
    "instdRmbrsmntAgt",
    "instdRmbrsmntAgtAcct",
    "thrdRmbrsmntAgt",
    "thrdRmbrsmntAgtAcct"
})
public class SettlementInstruction7 {

    @XmlElement(name = "SttlmMtd", required = true)
    @XmlSchemaType(name = "string")
    protected SettlementMethod1Code sttlmMtd;
    @XmlElement(name = "SttlmAcct")
    protected CashAccount38 sttlmAcct;
    @XmlElement(name = "ClrSys")
    protected ClearingSystemIdentification3Choice clrSys;
    @XmlElement(name = "InstgRmbrsmntAgt")
    protected BranchAndFinancialInstitutionIdentification6 instgRmbrsmntAgt;
    @XmlElement(name = "InstgRmbrsmntAgtAcct")
    protected CashAccount38 instgRmbrsmntAgtAcct;
    @XmlElement(name = "InstdRmbrsmntAgt")
    protected BranchAndFinancialInstitutionIdentification6 instdRmbrsmntAgt;
    @XmlElement(name = "InstdRmbrsmntAgtAcct")
    protected CashAccount38 instdRmbrsmntAgtAcct;
    @XmlElement(name = "ThrdRmbrsmntAgt")
    protected BranchAndFinancialInstitutionIdentification6 thrdRmbrsmntAgt;
    @XmlElement(name = "ThrdRmbrsmntAgtAcct")
    protected CashAccount38 thrdRmbrsmntAgtAcct;

    /**
     * Ruft den Wert der sttlmMtd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SettlementMethod1Code }
     *     
     */
    public SettlementMethod1Code getSttlmMtd() {
        return sttlmMtd;
    }

    /**
     * Legt den Wert der sttlmMtd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SettlementMethod1Code }
     *     
     */
    public void setSttlmMtd(SettlementMethod1Code value) {
        this.sttlmMtd = value;
    }

    /**
     * Ruft den Wert der sttlmAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount38 }
     *     
     */
    public CashAccount38 getSttlmAcct() {
        return sttlmAcct;
    }

    /**
     * Legt den Wert der sttlmAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount38 }
     *     
     */
    public void setSttlmAcct(CashAccount38 value) {
        this.sttlmAcct = value;
    }

    /**
     * Ruft den Wert der clrSys-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ClearingSystemIdentification3Choice }
     *     
     */
    public ClearingSystemIdentification3Choice getClrSys() {
        return clrSys;
    }

    /**
     * Legt den Wert der clrSys-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ClearingSystemIdentification3Choice }
     *     
     */
    public void setClrSys(ClearingSystemIdentification3Choice value) {
        this.clrSys = value;
    }

    /**
     * Ruft den Wert der instgRmbrsmntAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public BranchAndFinancialInstitutionIdentification6 getInstgRmbrsmntAgt() {
        return instgRmbrsmntAgt;
    }

    /**
     * Legt den Wert der instgRmbrsmntAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public void setInstgRmbrsmntAgt(BranchAndFinancialInstitutionIdentification6 value) {
        this.instgRmbrsmntAgt = value;
    }

    /**
     * Ruft den Wert der instgRmbrsmntAgtAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount38 }
     *     
     */
    public CashAccount38 getInstgRmbrsmntAgtAcct() {
        return instgRmbrsmntAgtAcct;
    }

    /**
     * Legt den Wert der instgRmbrsmntAgtAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount38 }
     *     
     */
    public void setInstgRmbrsmntAgtAcct(CashAccount38 value) {
        this.instgRmbrsmntAgtAcct = value;
    }

    /**
     * Ruft den Wert der instdRmbrsmntAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public BranchAndFinancialInstitutionIdentification6 getInstdRmbrsmntAgt() {
        return instdRmbrsmntAgt;
    }

    /**
     * Legt den Wert der instdRmbrsmntAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public void setInstdRmbrsmntAgt(BranchAndFinancialInstitutionIdentification6 value) {
        this.instdRmbrsmntAgt = value;
    }

    /**
     * Ruft den Wert der instdRmbrsmntAgtAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount38 }
     *     
     */
    public CashAccount38 getInstdRmbrsmntAgtAcct() {
        return instdRmbrsmntAgtAcct;
    }

    /**
     * Legt den Wert der instdRmbrsmntAgtAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount38 }
     *     
     */
    public void setInstdRmbrsmntAgtAcct(CashAccount38 value) {
        this.instdRmbrsmntAgtAcct = value;
    }

    /**
     * Ruft den Wert der thrdRmbrsmntAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public BranchAndFinancialInstitutionIdentification6 getThrdRmbrsmntAgt() {
        return thrdRmbrsmntAgt;
    }

    /**
     * Legt den Wert der thrdRmbrsmntAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public void setThrdRmbrsmntAgt(BranchAndFinancialInstitutionIdentification6 value) {
        this.thrdRmbrsmntAgt = value;
    }

    /**
     * Ruft den Wert der thrdRmbrsmntAgtAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount38 }
     *     
     */
    public CashAccount38 getThrdRmbrsmntAgtAcct() {
        return thrdRmbrsmntAgtAcct;
    }

    /**
     * Legt den Wert der thrdRmbrsmntAgtAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount38 }
     *     
     */
    public void setThrdRmbrsmntAgtAcct(CashAccount38 value) {
        this.thrdRmbrsmntAgtAcct = value;
    }

}
