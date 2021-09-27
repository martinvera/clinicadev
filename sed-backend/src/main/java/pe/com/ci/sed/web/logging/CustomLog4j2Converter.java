package pe.com.ci.sed.web.logging;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Plugin(name = "customlog4j2", category = PatternConverter.CATEGORY)
@ConverterKeys({"custommsg"})
public class CustomLog4j2Converter extends LogEventPatternConverter {

	private static ObjectMapper mapper = new ObjectMapper();
	
	protected CustomLog4j2Converter(String name, String style) {
		super(name, style);
		
	}

	public static CustomLog4j2Converter newInstance(String[] options) {
        return new CustomLog4j2Converter("My name Converter", "name");
    }
	
	@Override
	public void format(LogEvent event, StringBuilder toAppendTo) {
		try {
			String msj = event.getMessage().getFormattedMessage();
			
			Map<String,String> map = new LinkedHashMap<>();
			map.put("transactionId", Optional.ofNullable(event.getContextData().getValue("transactionId")).orElse(UUID.randomUUID()).toString() );
			map.put("message", msj);
	
			toAppendTo.append(mapper.writeValueAsString(map));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
