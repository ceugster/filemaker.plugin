package ch.eugster.filemaker.fsl.plugin.pdf;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Objects;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;

public class Pdf extends Executor
{
	private PDDocument document;
	
	public void getDocumentInfo(ObjectNode requestNode, ObjectNode responseNode)
	{
		JsonNode node = requestNode.get("content");
		if (Objects.nonNull(node))
		{
			String content = String.class.cast(node.asText());
			try
			{
				document = PDDocument.load(Base64.getDecoder().decode(content));
				PDDocumentInformation info = document.getDocumentInformation();
				if (Objects.nonNull(info)) 
				{
					JsonMapper mapper = new JsonMapper();
					ObjectNode metadata = mapper.createObjectNode();
					String value = info.getAuthor();
					metadata.put("author", Objects.nonNull(value) ? value : "");
					value = info.getCreator();
					metadata.put("creator", Objects.nonNull(value) ? value : "");
					value = info.getKeywords();
					metadata.put("keywords", Objects.nonNull(value) ? value : "");
					value = info.getProducer();
					metadata.put("producer", Objects.nonNull(value) ? value : "");
					value = info.getSubject();
					metadata.put("subject", Objects.nonNull(value) ? value : "");
					value = info.getTitle();
					metadata.put("title", Objects.nonNull(value) ? value : "");
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Calendar calendar= info.getCreationDate();
					metadata.put("creationDate", Objects.nonNull(calendar) ? formatter.format(calendar.getTime()): "");
					calendar= info.getModificationDate();
					metadata.put("modificationDate", Objects.nonNull(calendar) ? formatter.format(calendar.getTime()): "");
					responseNode.put(Executor.RESULT, metadata.toString());
				}
			}
			catch (Exception e) 
			{
				addErrorMessage(responseNode, e.getLocalizedMessage());
			}
		}
		else
		{
			addErrorMessage(responseNode, "missing_paramenter 'content'");
		}
	}
}
