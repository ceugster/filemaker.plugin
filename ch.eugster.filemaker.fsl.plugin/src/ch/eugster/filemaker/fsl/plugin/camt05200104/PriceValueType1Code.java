//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:57:24 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.camt05200104;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PriceValueType1Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="PriceValueType1Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DISC"/>
 *     &lt;enumeration value="PREM"/>
 *     &lt;enumeration value="PARV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PriceValueType1Code")
@XmlEnum
public enum PriceValueType1Code {

    DISC,
    PREM,
    PARV;

    public String value() {
        return name();
    }

    public static PriceValueType1Code fromValue(String v) {
        return valueOf(v);
    }

}
