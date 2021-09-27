package pe.com.ci.sed.web.logging;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomsLogBookFormatter implements HttpLogFormatter {

	private final JsonHttpLogFormatter delegate;
	
	public CustomsLogBookFormatter(ObjectMapper mapper) {
		this.delegate = new JsonHttpLogFormatter(mapper);
	}
	@Override
	public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
		Map<String, Object> content = new LinkedHashMap<>();
		content.put("transactionId", ThreadContext.get("transactionId") );
		content.putAll(delegate.prepare(precorrelation, request));
		content.remove("correlation");
		return delegate.format(content);
	}

	@Override
	public String format(Correlation correlation, HttpResponse response) throws IOException {
		Map<String, Object> content = new LinkedHashMap<>();
		content.put("transactionId", ThreadContext.get("transactionId") );
		content.putAll(delegate.prepare(correlation, response));
		content.remove("correlation");
		return delegate.format(content);
	}

}
