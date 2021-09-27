package pe.com.ci.sed.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import pe.com.ci.sed.web.model.generic.HeaderRequest;

import java.security.Principal;
import java.util.UUID;

@AllArgsConstructor
public abstract class BaseController {

    public final ApplicationContext context;

    public HeaderRequest getHeader(Principal principal) {
        return HeaderRequest.builder()
                .userId(principal.getName())
                .applicationId(context.getId())
                .transactionId(UUID.randomUUID().toString())
                .build();
    }
}
