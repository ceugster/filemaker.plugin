//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.19 um 03:10:26 PM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05300103;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für BankToCustomerStatementV03 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="BankToCustomerStatementV03">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GrpHdr" type="{urn:iso:std:iso:20022:tech:xsd:camt.053.001.03}GroupHeader58"/>
 *         &lt;element name="Stmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.053.001.03}AccountStatement3" maxOccurs="unbounded"/>
 *         &lt;element name="SplmtryData" type="{urn:iso:std:iso:20022:tech:xsd:camt.053.001.03}SupplementaryData1" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BankToCustomerStatementV03", propOrder = {
    "grpHdr",
    "stmt",
    "splmtryData"
})
public class BankToCustomerStatementV03 {

    @XmlElement(name = "GrpHdr", required = true)
    protected GroupHeader58 grpHdr;
    @XmlElement(name = "Stmt", required = true)
    protected List<AccountStatement3> stmt;
    @XmlElement(name = "SplmtryData")
    protected List<SupplementaryData1> splmtryData;

    /**
     * Ruft den Wert der grpHdr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GroupHeader58 }
     *     
     */
    public GroupHeader58 getGrpHdr() {
        return grpHdr;
    }

    /**
     * Legt den Wert der grpHdr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupHeader58 }
     *     
     */
    public void setGrpHdr(GroupHeader58 value) {
        this.grpHdr = value;
    }

    /**
     * Gets the value of the stmt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stmt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStmt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AccountStatement3 }
     * 
     * 
     */
    public List<AccountStatement3> getStmt() {
        if (stmt == null) {
            stmt = new ArrayList<AccountStatement3>();
        }
        return this.stmt;
    }

    /**
     * Gets the value of the splmtryData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the splmtryData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSplmtryData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupplementaryData1 }
     * 
     * 
     */
    public List<SupplementaryData1> getSplmtryData() {
        if (splmtryData == null) {
            splmtryData = new ArrayList<SupplementaryData1>();
        }
        return this.splmtryData;
    }

}
