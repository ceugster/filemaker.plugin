package ch.eugster.filemaker.fsl.plugin.camt;

import java.io.StringReader;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import ch.eugster.filemaker.fsl.plugin.camt05300104.Document;
import ch.eugster.filemaker.fsl.plugin.camt05300104.ObjectFactory;

public class Camt05300104Parser {

    /**
     * Parse a CAMT.053 formatted bank statement from the given input stream.
     *
     * @param string containing the CAMT.053 formatted bank statement as xml
     * @return json node holding CAMT.053 parsed bank statement
     * @throws JAXBException
     */
    public JsonNode parse(String xml) throws Exception 
    {
    	StringReader reader = null;
    	try
    	{
    		reader = new StringReader(xml);
    		JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
	        Unmarshaller unmarshaller = jc.createUnmarshaller();
	        @SuppressWarnings("unchecked")
			Document document = ((JAXBElement<Document>) unmarshaller.unmarshal(reader)).getValue();
			Gson gson = new Gson();
			String doc = gson.toJson(document);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tree = mapper.readTree(doc);
	        return tree;
    	}
    	finally
    	{
    		if (!Objects.isNull(reader))
    		{
    			reader.close();
    		}
    	}
    }
}