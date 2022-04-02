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


/**
 * <p>Java-Klasse für ChargesInformation3 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ChargesInformation3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TtlChrgsAndTaxAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount" minOccurs="0"/>
 *         &lt;element name="Amt" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}CurrencyAndAmount"/>
 *         &lt;element name="Tp" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}ChargeTypeChoice" minOccurs="0"/>
 *         &lt;element name="Rate" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PercentageRate" minOccurs="0"/>
 *         &lt;element name="Br" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}ChargeBearerType1Code" minOccurs="0"/>
 *         &lt;element name="Pty" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}BranchAndFinancialInstitutionIdentification3" minOccurs="0"/>
 *         &lt;element name="Tax" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}TaxCharges1" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargesInformation3", propOrder = {
    "ttlChrgsAndTaxAmt",
    "amt",
    "tp",
    "rate",
    "br",
    "pty",
    "tax"
})
public class ChargesInformation3 {

    @XmlElement(name = "TtlChrgsAndTaxAmt")
    protected CurrencyAndAmount ttlChrgsAndTaxAmt;
    @XmlElement(name = "Amt", required = true)
    protected CurrencyAndAmount amt;
    @XmlElement(name = "Tp")
    protected ChargeTypeChoice tp;
    @XmlElement(name = "Rate")
    protected BigDecimal rate;
    @XmlElement(name = "Br")
    @XmlSchemaType(name = "string")
    protected ChargeBearerType1Code br;
    @XmlElement(name = "Pty")
    protected BranchAndFinancialInstitutionIdentification3 pty;
    @XmlElement(name = "Tax")
    protected TaxCharges1 tax;

    /**
     * Ruft den Wert der ttlChrgsAndTaxAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public CurrencyAndAmount getTtlChrgsAndTaxAmt() {
        return ttlChrgsAndTaxAmt;
    }

    /**
     * Legt den Wert der ttlChrgsAndTaxAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyAndAmount }
     *     
     */
    public void setTtlChrgsAndTaxAmt(CurrencyAndAmount value) {
        this.ttlChrgsAndTaxAmt = value;
    }

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
     * Ruft den Wert der tp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ChargeTypeChoice }
     *     
     */
    public ChargeTypeChoice getTp() {
        return tp;
    }

    /**
     * Legt den Wert der tp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeTypeChoice }
     *     
     */
    public void setTp(ChargeTypeChoice value) {
        this.tp = value;
    }

    /**
     * Ruft den Wert der rate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Legt den Wert der rate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setRate(BigDecimal value) {
        this.rate = value;
    }

    /**
     * Ruft den Wert der br-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ChargeBearerType1Code }
     *     
     */
    public ChargeBearerType1Code getBr() {
        return br;
    }

    /**
     * Legt den Wert der br-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeBearerType1Code }
     *     
     */
    public void setBr(ChargeBearerType1Code value) {
        this.br = value;
    }

    /**
     * Ruft den Wert der pty-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BranchAndFinancialInstitutionIdentification3 }
     *     
     */
    public BranchAndFinancialInstitutionIdentification3 getPty() {
        return pty;
    }

    /**
     * Legt den Wert der pty-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BranchAndFinancialInstitutionIdentification3 }
     *     
     */
    public void setPty(BranchAndFinancialInstitutionIdentification3 value) {
        this.pty = value;
    }

    /**
     * Ruft den Wert der tax-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TaxCharges1 }
     *     
     */
    public TaxCharges1 getTax() {
        return tax;
    }

    /**
     * Legt den Wert der tax-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxCharges1 }
     *     
     */
    public void setTax(TaxCharges1 value) {
        this.tax = value;
    }

}
