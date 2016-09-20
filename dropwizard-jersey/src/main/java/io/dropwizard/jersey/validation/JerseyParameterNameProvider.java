package io.dropwizard.jersey.validation;

import org.hibernate.validator.internal.engine.DefaultParameterNameProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JerseyParameterNameProvider extends DefaultParameterNameProvider {

    @Override
    public List<String> getParameterNames(Constructor<?> constructor) {
        return super.getParameterNames(constructor);
    }

    @Override
    public List<String> getParameterNames(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        List<String> names = new ArrayList<>( parameterAnnotations.length );
        for (Annotation[] annotations : parameterAnnotations) {
            String name = getParameterNameFromAnnotations(annotations).orElse("arg" + (names.size() + 1));
            names.add(name);
        }
        return names;
    }

    /**
     * Derives member's name and type from it's annotations
     */
    public static Optional<String> getParameterNameFromAnnotations(Annotation[] memberAnnotations) {
        for (Annotation a : memberAnnotations) {
            if (a instanceof QueryParam) {
                return Optional.of("query param " + ((QueryParam) a).value());
            } else if (a instanceof PathParam) {
                return Optional.of("path param " + ((PathParam) a).value());
            } else if (a instanceof HeaderParam) {
                return Optional.of("header " + ((HeaderParam) a).value());
            } else if (a instanceof CookieParam) {
                return Optional.of("cookie " + ((CookieParam) a).value());
            } else if (a instanceof FormParam) {
                return Optional.of("form field " + ((FormParam) a).value());
            } else if (a instanceof Context) {
                return Optional.of("context");
            } else if (a instanceof MatrixParam) {
                return Optional.of("matrix param " + ((MatrixParam) a).value());
            }
        }

        return Optional.empty();
    }

}
