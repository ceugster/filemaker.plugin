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


/**
 * <p>Java-Klasse für ReportEntry1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReportEntry1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Amt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount"/>
 *         &lt;element name="CdtDbtInd" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CreditDebitCode"/>
 *         &lt;element name="RvslInd" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}TrueFalseIndicator" minOccurs="0"/>
 *         &lt;element name="Sts" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}EntryStatus2Code"/>
 *         &lt;element name="BookgDt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}DateAndDateTimeChoice" minOccurs="0"/>
 *         &lt;element name="ValDt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}DateAndDateTimeChoice" minOccurs="0"/>
 *         &lt;element name="AcctSvcrRef" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max35Text" minOccurs="0"/>
 *         &lt;element name="Avlbty" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CashBalanceAvailability1" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="BkTxCd" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}BankTransactionCodeStructure1"/>
 *         &lt;element name="ComssnWvrInd" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}YesNoIndicator" minOccurs="0"/>
 *         &lt;element name="AddtlInfInd" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}MessageIdentification2" minOccurs="0"/>
 *         &lt;element name="Btch" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}BatchInformation1" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="AmtDtls" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}AmountAndCurrencyExchange2" minOccurs="0"/>
 *         &lt;element name="Chrgs" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}ChargesInformation3" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Intrst" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}TransactionInterest1" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="TxDtls" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}EntryTransaction1" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="AddtlNtryInf" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max500Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportEntry1", propOrder = {
    "amt",
    "cdtDbtInd",
    "rvslInd",
    "sts",
    "bookgDt",
    "valDt",
    "acctSvcrRef",
    "avlbty",
    "bkTxCd",
    "comssnWvrInd",
    "addtlInfInd",
    "btch",
    "amtDtls",
    "chrgs",
    "intrst",
    "txDtls",
    "addtlNtryInf"
})
public class ReportEntry1 {

    @XmlElement(name = "Amt", required = true)
    protected CurrencyAndAmount amt;
    @XmlElement(name = "CdtDbtInd", required = true)
    @XmlSchemaType(name = "string")
    protected CreditDebitCode cdtDbtInd;
    @XmlElement(name = "RvslInd")
    protected Boolean rvslInd;
    @XmlElement(name = "Sts", required = true)
    @XmlSchemaType(name = "string")
    protected EntryStatus2Code sts;
    @XmlElement(name = "BookgDt")
    protected DateAndDateTimeChoice bookgDt;
    @XmlElement(name = "ValDt")
    protected DateAndDateTimeChoice valDt;
    @XmlElement(name = "AcctSvcrRef")
    protected String acctSvcrRef;
    @XmlElement(name = "Avlbty")
    protected List<CashBalanceAvailability1> avlbty;
    @XmlElement(name = "BkTxCd", required = true)
    protected BankTransactionCodeStructure1 bkTxCd;
    @XmlElement(name = "ComssnWvrInd")
    protected Boolean comssnWvrInd;
    @XmlElement(name = "AddtlInfInd")
    protected MessageIdentification2 addtlInfInd;
    @XmlElement(name = "Btch")
    protected List<BatchInformation1> btch;
    @XmlElement(name = "AmtDtls")
    protected AmountAndCurrencyExchange2 amtDtls;
    @XmlElement(name = "Chrgs")
    protected List<ChargesInformation3> chrgs;
    @XmlElement(name = "Intrst")
    protected List<TransactionInterest1> intrst;
    @XmlElement(name = "TxDtls")
    protected List<EntryTransaction1> txDtls;
    @XmlElement(name = "AddtlNtryInf")
    protected String addtlNtryInf;

    /**
     * Ruft den Wert der amt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getAmt() {
        return amt;
    }

    /**
     * Legt den Wert der amt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setAmt(CurrencyAndAmount value) {
        this.amt = value;
    }

    /**
     * Ruft den Wert der cdtDbtInd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CreditDebitCode }
     *     
     */
    public CreditDebitCode getCdtDbtInd() {
        return cdtDbtInd;
    }

    /**
     * Legt den Wert der cdtDbtInd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditDebitCode }
     *     
     */
    public void setCdtDbtInd(CreditDebitCode value) {
        this.cdtDbtInd = value;
    }

    /**
     * Ruft den Wert der rvslInd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRvslInd() {
        return rvslInd;
    }

    /**
     * Legt den Wert der rvslInd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRvslInd(Boolean value) {
        this.rvslInd = value;
    }

    /**
     * Ruft den Wert der sts-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EntryStatus2Code }
     *     
     */
    public EntryStatus2Code getSts() {
        return sts;
    }

    /**
     * Legt den Wert der sts-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EntryStatus2Code }
     *     
     */
    public void setSts(EntryStatus2Code value) {
        this.sts = value;
    }

    /**
     * Ruft den Wert der bookgDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateAndDateTimeChoice }
     *     
     */
    public DateAndDateTimeChoice getBookgDt() {
        return bookgDt;
    }

    /**
     * Legt den Wert der bookgDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateAndDateTimeChoice }
     *     
     */
    public void setBookgDt(DateAndDateTimeChoice value) {
        this.bookgDt = value;
    }

    /**
     * Ruft den Wert der valDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateAndDateTimeChoice }
     *     
     */
    public DateAndDateTimeChoice getValDt() {
        return valDt;
    }

    /**
     * Legt den Wert der valDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateAndDateTimeChoice }
     *     
     */
    public void setValDt(DateAndDateTimeChoice value) {
        this.valDt = value;
    }

    /**
     * Ruft den Wert der acctSvcrRef-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcctSvcrRef() {
        return acctSvcrRef;
    }

    /**
     * Legt den Wert der acctSvcrRef-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcctSvcrRef(String value) {
        this.acctSvcrRef = value;
    }

    /**
     * Gets the value of the avlbty property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the avlbty property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvlbty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CashBalanceAvailability1 }
     * 
     * 
     */
    public List<CashBalanceAvailability1> getAvlbty() {
        if (avlbty == null) {
            avlbty = new ArrayList<CashBalanceAvailability1>();
        }
        return this.avlbty;
    }

    /**
     * Ruft den Wert der bkTxCd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BankTransactionCodeStructure1 }
     *     
     */
    public BankTransactionCodeStructure1 getBkTxCd() {
        return bkTxCd;
    }

    /**
     * Legt den Wert der bkTxCd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BankTransactionCodeStructure1 }
     *     
     */
    public void setBkTxCd(BankTransactionCodeStructure1 value) {
        this.bkTxCd = value;
    }

    /**
     * Ruft den Wert der comssnWvrInd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isComssnWvrInd() {
        return comssnWvrInd;
    }

    /**
     * Legt den Wert der comssnWvrInd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setComssnWvrInd(Boolean value) {
        this.comssnWvrInd = value;
    }

    /**
     * Ruft den Wert der addtlInfInd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MessageIdentification2 }
     *     
     */
    public MessageIdentification2 getAddtlInfInd() {
        return addtlInfInd;
    }

    /**
     * Legt den Wert der addtlInfInd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageIdentification2 }
     *     
     */
    public void setAddtlInfInd(MessageIdentification2 value) {
        this.addtlInfInd = value;
    }

    /**
     * Gets the value of the btch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the btch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBtch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BatchInformation1 }
     * 
     * 
     */
    public List<BatchInformation1> getBtch() {
        if (btch == null) {
            btch = new ArrayList<BatchInformation1>();
        }
        return this.btch;
    }

    /**
     * Ruft den Wert der amtDtls-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountAndCurrencyExchange2 }
     *     
     */
    public AmountAndCurrencyExchange2 getAmtDtls() {
        return amtDtls;
    }

    /**
     * Legt den Wert der amtDtls-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountAndCurrencyExchange2 }
     *     
     */
    public void setAmtDtls(AmountAndCurrencyExchange2 value) {
        this.amtDtls = value;
    }

    /**
     * Gets the value of the chrgs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chrgs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChrgs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ChargesInformation3 }
     * 
     * 
     */
    public List<ChargesInformation3> getChrgs() {
        if (chrgs == null) {
            chrgs = new ArrayList<ChargesInformation3>();
        }
        return this.chrgs;
    }

    /**
     * Gets the value of the intrst property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the intrst property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIntrst().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionInterest1 }
     * 
     * 
     */
    public List<TransactionInterest1> getIntrst() {
        if (intrst == null) {
            intrst = new ArrayList<TransactionInterest1>();
        }
        return this.intrst;
    }

    /**
     * Gets the value of the txDtls property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the txDtls property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTxDtls().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntryTransaction1 }
     * 
     * 
     */
    public List<EntryTransaction1> getTxDtls() {
        if (txDtls == null) {
            txDtls = new ArrayList<EntryTransaction1>();
        }
        return this.txDtls;
    }

    /**
     * Ruft den Wert der addtlNtryInf-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddtlNtryInf() {
        return addtlNtryInf;
    }

    /**
     * Legt den Wert der addtlNtryInf-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddtlNtryInf(String value) {
        this.addtlNtryInf = value;
    }

}
