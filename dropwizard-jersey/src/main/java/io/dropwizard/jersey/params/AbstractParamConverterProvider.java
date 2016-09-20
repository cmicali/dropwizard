package io.dropwizard.jersey.params;

import com.google.common.base.Strings;
import io.dropwizard.jersey.validation.JerseyParameterNameProvider;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public class AbstractParamConverterProvider implements ParamConverterProvider {

    public AbstractParamConverterProvider() {
    }

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (AbstractParam.class.isAssignableFrom(rawType)) {
            String parameterName = JerseyParameterNameProvider.getParameterNameFromAnnotations(annotations).orElse("Parameter");
            try {
                final Constructor<T> constructor = rawType.getConstructor(String.class, String.class);
                return new ParamConverter<T>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public T fromString(String s) {
                        if (Strings.isNullOrEmpty(s)) {
                            return null;
                        }
                        try {
                            return (T)constructor.newInstance(s, parameterName);
                        }
                        catch (InstantiationException | IllegalAccessException e) {
                            throw new InternalServerErrorException(String.format("Unable to convert parameter %s: %s", parameterName, e.getMessage()));
                        }
                        catch(InvocationTargetException e) {
                            Throwable t = e.getTargetException();
                            if (t instanceof RuntimeException) {
                                throw (RuntimeException)t;
                            }
                            return null;
                        }
                    }

                    @Override
                    public String toString(T t) {
                        return t.toString();
                    }
                };
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(String.format("Unable to create parameter converter: %s does not have a (String, String) constructor", rawType.getName()));
            }
        }
        return null;
    }

}
