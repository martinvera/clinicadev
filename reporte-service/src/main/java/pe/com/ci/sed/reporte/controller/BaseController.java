package pe.com.ci.sed.reporte.controller;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import pe.com.ci.sed.reporte.model.generic.RequestHeader;

import java.security.Principal;
import java.util.UUID;

@AllArgsConstructor
public abstract class BaseController {

    public final ApplicationContext context;

    public RequestHeader getHeader(Principal principal) {
        return RequestHeader.builder()
                .userId(principal.getName())
                .applicationId(context.getId())
                .transactionId(UUID.randomUUID().toString())
                .build();
    }
}
