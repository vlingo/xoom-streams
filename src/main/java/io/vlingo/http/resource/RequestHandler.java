//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.vlingo.http.resource;

import io.vlingo.actors.Logger;
import io.vlingo.common.Completes;
import io.vlingo.http.Method;
import io.vlingo.http.Request;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Action.MappedParameters;
import io.vlingo.http.resource.ParameterResolver.Type;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RequestHandler {
    public final Method method;
    public final String path;
    public final String actionSignature;
    private final Pattern pattern = Pattern.compile("\\{(.*?)\\}");
    protected MediaTypeMapper mediaTypeMapper;
    protected ErrorHandler errorHandler;

    protected RequestHandler(Method method, String path, List<ParameterResolver<?>> parameterResolvers) {
        this.method = method;
        this.path = path;
        this.actionSignature = this.generateActionSignature(parameterResolvers);
        this.errorHandler = DefaultErrorHandler.instance();
        this.mediaTypeMapper = DefaultMediaTypeMapper.instance();
    }

    protected RequestHandler(Method method, String path, List<ParameterResolver<?>> parameterResolvers, ErrorHandler errorHandler, MediaTypeMapper mediaTypeMapper) {
        this.method = method;
        this.path = path;
        this.actionSignature = this.generateActionSignature(parameterResolvers);
        this.errorHandler = errorHandler;
        this.mediaTypeMapper = mediaTypeMapper;
    }

    protected Completes<Response> runParamExecutor(Object paramExecutor, Supplier<Completes<Response>> executeRequest) {
        if (paramExecutor == null) {
            throw new HandlerMissingException("No handler defined for " + this.method.toString() + " " + this.path);
        } else {
            return (Completes)executeRequest.get();
        }
    }

    protected abstract Completes<Response> execute(Request var1, MappedParameters var2, Logger var3);

    private String generateActionSignature(List<ParameterResolver<?>> parameterResolvers) {
        this.checkOrder(parameterResolvers);
        if (this.path.replaceAll(" ", "").contains("{}")) {
            throw new IllegalArgumentException("Empty path parameter name for " + this.method + " " + this.path);
        } else {
            StringBuilder result = new StringBuilder();
            Matcher matcher = this.pattern.matcher(this.path);
            boolean first = true;
            Iterator var5 = parameterResolvers.iterator();

            while(var5.hasNext()) {
                ParameterResolver<?> resolver = (ParameterResolver)var5.next();
                if (resolver.type == Type.PATH) {
                    matcher.find();
                    if (first) {
                        first = false;
                    } else {
                        result.append(", ");
                    }

                    result.append(resolver.paramClass.getSimpleName()).append(" ").append(matcher.group(1));
                }
            }

            return result.toString();
        }
    }

    private void checkOrder(List<ParameterResolver<?>> parameterResolvers) {
        boolean firstNonPathResolver = false;
        Iterator var3 = parameterResolvers.iterator();

        ParameterResolver resolver;
        do {
            if (!var3.hasNext()) {
                return;
            }

            resolver = (ParameterResolver)var3.next();
            if (resolver.type != Type.PATH) {
                firstNonPathResolver = true;
            }
        } while(!firstNonPathResolver || resolver.type != Type.PATH);

        throw new IllegalArgumentException("Path parameters are unsorted");
    }

    protected Mapper mapperFrom(Class<? extends Mapper> mapperClass) {
        try {
            return (Mapper)mapperClass.newInstance();
        } catch (Exception var3) {
            throw new IllegalArgumentException("Cannot instantiate mapper class: " + mapperClass.getName());
        }
    }
}
