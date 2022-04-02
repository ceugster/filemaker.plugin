//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2022.03.20 um 07:55:49 AM CET 
//


package ch.eugster.filemaker.fsl.plugin.pacs00800109;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Frequency6Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="Frequency6Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="YEAR"/>
 *     &lt;enumeration value="MNTH"/>
 *     &lt;enumeration value="QURT"/>
 *     &lt;enumeration value="MIAN"/>
 *     &lt;enumeration value="WEEK"/>
 *     &lt;enumeration value="DAIL"/>
 *     &lt;enumeration value="ADHO"/>
 *     &lt;enumeration value="INDA"/>
 *     &lt;enumeration value="FRTN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Frequency6Code")
@XmlEnum
public enum Frequency6Code {

    YEAR,
    MNTH,
    QURT,
    MIAN,
    WEEK,
    DAIL,
    ADHO,
    INDA,
    FRTN;

    public String value() {
        return name();
    }

    public static Frequency6Code fromValue(String v) {
        return valueOf(v);
    }

}
