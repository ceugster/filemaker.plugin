//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:55:49 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.pacs00800109;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für GroupHeader93 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="GroupHeader93">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MsgId" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}Max35Text"/>
 *         &lt;element name="CreDtTm" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISODateTime"/>
 *         &lt;element name="BtchBookg" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}BatchBookingIndicator" minOccurs="0"/>
 *         &lt;element name="NbOfTxs" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}Max15NumericText"/>
 *         &lt;element name="CtrlSum" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}DecimalNumber" minOccurs="0"/>
 *         &lt;element name="TtlIntrBkSttlmAmt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ActiveCurrencyAndAmount" minOccurs="0"/>
 *         &lt;element name="IntrBkSttlmDt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}ISODate" minOccurs="0"/>
 *         &lt;element name="SttlmInf" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}SettlementInstruction7"/>
 *         &lt;element name="PmtTpInf" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}PaymentTypeInformation28" minOccurs="0"/>
 *         &lt;element name="InstgAgt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}BranchAndFinancialInstitutionIdentification6" minOccurs="0"/>
 *         &lt;element name="InstdAgt" type="{urn:iso:std:iso:20022:tech:xsd:pacs.008.001.09}BranchAndFinancialInstitutionIdentification6" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroupHeader93", propOrder = {
    "msgId",
    "creDtTm",
    "btchBookg",
    "nbOfTxs",
    "ctrlSum",
    "ttlIntrBkSttlmAmt",
    "intrBkSttlmDt",
    "sttlmInf",
    "pmtTpInf",
    "instgAgt",
    "instdAgt"
})
public class GroupHeader93 {

    @XmlElement(name = "MsgId", required = true)
    protected String msgId;
    @XmlElement(name = "CreDtTm", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creDtTm;
    @XmlElement(name = "BtchBookg")
    protected Boolean btchBookg;
    @XmlElement(name = "NbOfTxs", required = true)
    protected String nbOfTxs;
    @XmlElement(name = "CtrlSum")
    protected BigDecimal ctrlSum;
    @XmlElement(name = "TtlIntrBkSttlmAmt")
    protected ActiveCurrencyAndAmount ttlIntrBkSttlmAmt;
    @XmlElement(name = "IntrBkSttlmDt")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar intrBkSttlmDt;
    @XmlElement(name = "SttlmInf", required = true)
    protected SettlementInstruction7 sttlmInf;
    @XmlElement(name = "PmtTpInf")
    protected PaymentTypeInformation28 pmtTpInf;
    @XmlElement(name = "InstgAgt")
    protected BranchAndFinancialInstitutionIdentification6 instgAgt;
    @XmlElement(name = "InstdAgt")
    protected BranchAndFinancialInstitutionIdentification6 instdAgt;

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
     * Ruft den Wert der btchBookg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBtchBookg() {
        return btchBookg;
    }

    /**
     * Legt den Wert der btchBookg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBtchBookg(Boolean value) {
        this.btchBookg = value;
    }

    /**
     * Ruft den Wert der nbOfTxs-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNbOfTxs() {
        return nbOfTxs;
    }

    /**
     * Legt den Wert der nbOfTxs-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNbOfTxs(String value) {
        this.nbOfTxs = value;
    }

    /**
     * Ruft den Wert der ctrlSum-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCtrlSum() {
        return ctrlSum;
    }

    /**
     * Legt den Wert der ctrlSum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCtrlSum(BigDecimal value) {
        this.ctrlSum = value;
    }

    /**
     * Ruft den Wert der ttlIntrBkSttlmAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ActiveCurrencyAndAmount }
     *     
     */
    public ActiveCurrencyAndAmount getTtlIntrBkSttlmAmt() {
        return ttlIntrBkSttlmAmt;
    }

    /**
     * Legt den Wert der ttlIntrBkSttlmAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ActiveCurrencyAndAmount }
     *     
     */
    public void setTtlIntrBkSttlmAmt(ActiveCurrencyAndAmount value) {
        this.ttlIntrBkSttlmAmt = value;
    }

    /**
     * Ruft den Wert der intrBkSttlmDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIntrBkSttlmDt() {
        return intrBkSttlmDt;
    }

    /**
     * Legt den Wert der intrBkSttlmDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIntrBkSttlmDt(XMLGregorianCalendar value) {
        this.intrBkSttlmDt = value;
    }

    /**
     * Ruft den Wert der sttlmInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SettlementInstruction7 }
     *     
     */
    public SettlementInstruction7 getSttlmInf() {
        return sttlmInf;
    }

    /**
     * Legt den Wert der sttlmInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SettlementInstruction7 }
     *     
     */
    public void setSttlmInf(SettlementInstruction7 value) {
        this.sttlmInf = value;
    }

    /**
     * Ruft den Wert der pmtTpInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTypeInformation28 }
     *     
     */
    public PaymentTypeInformation28 getPmtTpInf() {
        return pmtTpInf;
    }

    /**
     * Legt den Wert der pmtTpInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTypeInformation28 }
     *     
     */
    public void setPmtTpInf(PaymentTypeInformation28 value) {
        this.pmtTpInf = value;
    }

    /**
     * Ruft den Wert der instgAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public BranchAndFinancialInstitutionIdentification6 getInstgAgt() {
        return instgAgt;
    }

    /**
     * Legt den Wert der instgAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public void setInstgAgt(BranchAndFinancialInstitutionIdentification6 value) {
        this.instgAgt = value;
    }

    /**
     * Ruft den Wert der instdAgt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public BranchAndFinancialInstitutionIdentification6 getInstdAgt() {
        return instdAgt;
    }

    /**
     * Legt den Wert der instdAgt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentification6 }
     *     
     */
    public void setInstdAgt(BranchAndFinancialInstitutionIdentification6 value) {
        this.instdAgt = value;
    }

}
