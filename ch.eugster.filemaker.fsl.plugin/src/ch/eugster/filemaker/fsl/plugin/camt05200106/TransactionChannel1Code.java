//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:07 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200106;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransactionChannel1Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionChannel1Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MAIL"/>
 *     &lt;enumeration value="TLPH"/>
 *     &lt;enumeration value="ECOM"/>
 *     &lt;enumeration value="TVPY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransactionChannel1Code")
@XmlEnum
public enum TransactionChannel1Code {

    MAIL,
    TLPH,
    ECOM,
    TVPY;

    public String value() {
        return name();
    }

    public static TransactionChannel1Code fromValue(String v) {
        return valueOf(v);
    }

}
