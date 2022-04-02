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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ReturnReasonInformation5 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReturnReasonInformation5">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OrgnlBkTxCd" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}BankTransactionCodeStructure1" minOccurs="0"/>
 *         &lt;element name="RtrOrgtr" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}PartyIdentification8" minOccurs="0"/>
 *         &lt;element name="RtrRsn" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}ReturnReason1Choice" minOccurs="0"/>
 *         &lt;element name="AddtlRtrRsnInf" type="{urn:iso:std:iso:20022:tech:xsd:camt.052.001.01}Max105Text" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnReasonInformation5", propOrder = {
    "orgnlBkTxCd",
    "rtrOrgtr",
    "rtrRsn",
    "addtlRtrRsnInf"
})
public class ReturnReasonInformation5 {

    @XmlElement(name = "OrgnlBkTxCd")
    protected BankTransactionCodeStructure1 orgnlBkTxCd;
    @XmlElement(name = "RtrOrgtr")
    protected PartyIdentification8 rtrOrgtr;
    @XmlElement(name = "RtrRsn")
    protected ReturnReason1Choice rtrRsn;
    @XmlElement(name = "AddtlRtrRsnInf")
    protected List<String> addtlRtrRsnInf;

    /**
     * Ruft den Wert der orgnlBkTxCd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BankTransactionCodeStructure1 }
     *     
     */
    public BankTransactionCodeStructure1 getOrgnlBkTxCd() {
        return orgnlBkTxCd;
    }

    /**
     * Legt den Wert der orgnlBkTxCd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BankTransactionCodeStructure1 }
     *     
     */
    public void setOrgnlBkTxCd(BankTransactionCodeStructure1 value) {
        this.orgnlBkTxCd = value;
    }

    /**
     * Ruft den Wert der rtrOrgtr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyIdentification8 }
     *     
     */
    public PartyIdentification8 getRtrOrgtr() {
        return rtrOrgtr;
    }

    /**
     * Legt den Wert der rtrOrgtr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyIdentification8 }
     *     
     */
    public void setRtrOrgtr(PartyIdentification8 value) {
        this.rtrOrgtr = value;
    }

    /**
     * Ruft den Wert der rtrRsn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReturnReason1Choice }
     *     
     */
    public ReturnReason1Choice getRtrRsn() {
        return rtrRsn;
    }

    /**
     * Legt den Wert der rtrRsn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnReason1Choice }
     *     
     */
    public void setRtrRsn(ReturnReason1Choice value) {
        this.rtrRsn = value;
    }

    /**
     * Gets the value of the addtlRtrRsnInf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addtlRtrRsnInf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddtlRtrRsnInf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAddtlRtrRsnInf() {
        if (addtlRtrRsnInf == null) {
            addtlRtrRsnInf = new ArrayList<String>();
        }
        return this.addtlRtrRsnInf;
    }

}
