package pe.com.ci.sed.integrator.errors;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.CUSTOM, property = "error", visible = true)
@JsonTypeIdResolver(Error.LowerCaseClassNameResolver.class)
public class Error {

    private HttpStatus status;
    private String mensaje;
    private List<ValidationError> errors = new ArrayList<>();

    Error(HttpStatus status) {
        this.status = status;
    }

    Error(HttpStatus status, String mensaje, Throwable ex) {
        this.status = status;
        this.mensaje = mensaje;
    }

    @Data
    @AllArgsConstructor
    static class ValidationError {
        private String campo;
        private String mensaje;
    }

    public static class LowerCaseClassNameResolver extends TypeIdResolverBase {

        @Override
        public String idFromValue(Object value) {
            return value.getClass().getSimpleName().toLowerCase();
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            return idFromValue(value);
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.CUSTOM;
        }
    }
}