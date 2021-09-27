package pe.com.ci.sed.document.logging;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.*;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class TransactionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ThreadContext.put("transactionId", UUID.randomUUID().toString());

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // other methods
    }

    @Override
    public void destroy() {
        // other methods
    }

}