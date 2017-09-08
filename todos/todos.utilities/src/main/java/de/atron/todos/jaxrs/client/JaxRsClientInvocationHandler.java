package de.atron.todos.jaxrs.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * Used for creating proxy that around interface annotated with JAX-RS annotations.
 * 
 * 
 * @author ferlan
 *
 */
public class JaxRsClientInvocationHandler implements InvocationHandler {

    private final static String NORMALIZED_PATH_VALUE = "-0999666333";

    public static final String PARAM_PATH_VERSION = "version";

    /**
     * 
     * @param serviceUrl
     * @param providers
     * @return
     */
    public static WebTarget buildService(String serviceUrl, Class<?>... providers) {
        Client client = ClientBuilder.newClient();
        for (Class<?> provider: providers) {
            client.register(provider);
        }
        return client.target(serviceUrl);
    }

    /**
     * 
     * @param serviceClass
     * @param host
     * @param context
     * @return
     */
    public static String getServiceUrl(Class<?> serviceClass, String protocol, String host, String context) {
        Path path = serviceClass.getAnnotation(Path.class);
        String service = null;
        if (path != null) {
            service = path.value();
        }
        Map<String, String> mapParams = new HashMap<String, String>();
        mapParams.put("protocol", protocol);
        mapParams.put("server", host);
        mapParams.put("context", context);
        mapParams.put("service", service);
        UriBuilder uriBuilder = UriBuilder.fromPath("{protocol}://{server}/{context}/{service}");
        URI uri = uriBuilder.buildFromMap(mapParams);
        return uri.toString();
    }

    /**
     * service class
     */
    private Class<?> serviceClass;

    /**
     * web target
     */
    private final WebTarget client;

    /**
     * missing path values
     */
    private String missingPathValue = JaxRsClientInvocationHandler.NORMALIZED_PATH_VALUE;

    /**
     * 
     * @param serviceClass - Interface annotated with JaxRS annotations
     * @param baseUrl - url of the server
     * @param context - context on which Rest services are published
     * @param providers
     */
    public JaxRsClientInvocationHandler(Class<?> serviceClass, String protocol, String host, String context,
        Class<?>... providers) {
        super();
        this.serviceClass = serviceClass;
        String serviceUrl = JaxRsClientInvocationHandler.getServiceUrl(serviceClass, protocol, host, context);
        this.client = JaxRsClientInvocationHandler.buildService(serviceUrl, providers);

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method,
     * java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] parameterValues) throws Throwable {

        /**
         * build parameters
         */
        Map<String, String> mapPathParams = new HashMap<>();
        Map<String, Object> mapQueryParams = new HashMap<>();
        Map<String, Object> mapHeaderParams = new HashMap<>();
        // find all parameters annotations
        Annotation[][] annotations = method.getParameterAnnotations();
        Object payload = null;
        for (int i = 0; i < annotations.length; i++) {
            // check if annotation exists at each index
            if (annotations[i].length == 0 /* and if the annotation is the type you want */ ) {
                // get the value of the parameter
                if (payload == null) {
                    payload = parameterValues[i];
                } else {
                    throw new UnsupportedOperationException(
                        "More than one payload registered for REST method " + method.toString());
                }
            }
            // iterate over parameter annotation
            for (Annotation a: annotations[i]) {
                if (a instanceof HeaderParam) {
                    // store header parameter
                    mapHeaderParams.put(((HeaderParam)a).value(), parameterValues[i]);
                } else {
                    if (a instanceof PathParam) {
                        // convert path param value to string
                        Object object = parameterValues[i];
                        String pathValue = null;
                        // check if string
                        if (object instanceof String) {
                            pathValue = (String)object;
                        } else {
                            // check if null
                            if (object == null) {
                                // apply missing path value
                                pathValue = this.missingPathValue;
                            } else {
                                pathValue = object.toString();
                            }
                        }
                        // store path parameter
                        mapPathParams.put(((PathParam)a).value(), pathValue);
                    } else {
                        if (a instanceof QueryParam) {
                            // store query parameter
                            mapQueryParams.put(((QueryParam)a).value(), parameterValues[i]);
                        }
                    }
                }
            }
        }
        // build URI based on annotation on the method and map parameters
        String methodUri = null;
        UriBuilder uriBuilder = null;
        try {
            uriBuilder = UriBuilder.fromMethod(this.serviceClass, method.getName());
        } catch (IllegalArgumentException e) {
            // cannot find @Path annotation
        }
        // using "" as path when method not annotated
        methodUri = uriBuilder == null ? "" : uriBuilder.buildFromMap(mapPathParams).getPath();

        Class<?> returnType = method.getReturnType();
        GenericType returnObject = null;
        if (List.class.isAssignableFrom(returnType)) {
            Type genericReturnType = method.getGenericReturnType();
            returnObject = new GenericType(genericReturnType);
        }
        WebTarget webTarget = this.client.path(methodUri);
        /* add query params */
        if (mapQueryParams != null) {
            for (Entry<String, Object> entry: mapQueryParams.entrySet()) {
                webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
            }
        }
        // TODO: calculate application type based on annotations
        // build a request
        Builder builder = webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        /* add header params */
        if (mapHeaderParams != null) {
            for (Entry<String, Object> entry: mapHeaderParams.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        for (Annotation annotation: method.getAnnotations()) {
            if (annotation instanceof GET) {
                if (returnObject == null) {
                    return builder.get(returnType);
                } else {
                    return builder.get(returnObject);
                }
            }
            if (annotation instanceof POST) {
                if (returnObject == null) {
                    return builder.post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE), returnType);
                } else {
                    return builder.post(Entity.entity(payload, MediaType.APPLICATION_JSON_TYPE), returnObject);
                }
            }
            if (annotation instanceof PUT) {
                if (returnObject == null) {

                    return builder.post(
                        Entity.entity(payload == null ? new Object() : payload, MediaType.APPLICATION_JSON_TYPE),
                        returnType);

                } else {

                    return builder.post(
                        Entity.entity(payload == null ? new Object() : payload, MediaType.APPLICATION_JSON_TYPE),
                        returnObject);

                }
            }

            if (annotation instanceof DELETE) {
                if (returnObject == null) {
                    return builder.delete(returnType);
                } else {
                    return builder.delete(returnObject);
                }
            }
        }
        throw new UnsupportedOperationException(
            "This method must be executed only from method annotated with  proper JAVA-RS annotation");
    }

    /**
     * @param missingPathValue the missingPathValue to set
     */
    public void setMissingPathValue(String missingPathValue) {
        this.missingPathValue = missingPathValue;
    }
}
