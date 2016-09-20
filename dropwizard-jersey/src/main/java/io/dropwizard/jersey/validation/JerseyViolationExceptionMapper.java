package io.dropwizard.jersey.validation;

import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.server.model.Invocable;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Provider
public class JerseyViolationExceptionMapper implements ExceptionMapper<JerseyViolationException> {
    @Override
    public Response toResponse(final JerseyViolationException exception) {
        final Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        final Invocable invocable = exception.getInvocable();
        final List<String> errors = exception.getConstraintViolations()
            .stream()
            .map(violation -> ConstraintMessage.getMessage(violation, invocable))
            .collect(Collectors.toList());
        final int status = ConstraintMessage.determineStatus(violations, invocable);
        return Response.status(status)
                .entity(new ValidationErrorMessage(ImmutableList.copyOf(errors)))
                .build();
    }
}
