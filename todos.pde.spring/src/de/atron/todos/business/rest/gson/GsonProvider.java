package de.atron.todos.business.rest.gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

    public static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final String FORMAT_DATE = "yyyy-MM-dd";

    /**
     * Supports any date ISO 8601 compliant format including {@link #FORMAT_DATE}
     */
    public static final DateTimeFormatter FORMATTER_ISO_DATE = DateTimeFormatter.ISO_DATE; // DateTimeFormatter.ofPattern(FORMAT_DATE);

    /**
     * Supports any date time ISO 8601 compliant format(both with and without timeone) including
     * {@link #FORMAT_DATETIME}
     */
    public static final DateTimeFormatter FORMATTER_ISO_DATETIME = DateTimeFormatter.ISO_DATE_TIME; // DateTimeFormatter.ofPattern(FORMAT_DATETIME);
    
    
    protected final Gson gson;

    public GsonProvider() {
        this.gson = new GsonBuilder().setDateFormat(FORMAT_DATETIME).create();
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType) {
        return true;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type gnericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
        InputStream entityStream) throws IOException, WebApplicationException {
        InputStreamReader reader = new InputStreamReader(entityStream, StandardCharsets.UTF_8);
        try {
            return this.gson.fromJson(reader, type);
        } finally {
            reader.close();
        }
    }

    @Override
    public void writeTo(T object, Class<?> type, Type genericType, Annotation[] annotations,
        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
        OutputStream entityStream) throws IOException, WebApplicationException {
        PrintWriter printWriter = new PrintWriter(
            new OutputStreamWriter(entityStream, StandardCharsets.UTF_8), true);
        try {
            String json = this.gson.toJson(object);
            printWriter.write(json);
            printWriter.flush();
        } finally {
            printWriter.close();
        }
    }

}
