/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.api;

import edu.harvard.iq.dataverse.util.json.NullSafeJsonBuilder;
import java.io.StringReader;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import us.cyberimpact.trsa.settings.SettingsServiceBean;

/**
 *
 * @author asone
 */
public abstract class AbstractApiBean {
    
    private static final Logger logger = Logger.getLogger(AbstractApiBean.class.getName());
    
    private static final String DATAVERSE_KEY_HEADER_NAME = "X-Dataverse-key";
    private static final String PERSISTENT_ID_KEY=":persistentId";
    public static final String STATUS_ERROR = "ERROR";
    public static final String STATUS_OK = "OK";
    public static final String STATUS_WF_IN_PROGRESS = "WORKFLOW_IN_PROGRESS";
    
    /**
     * Utility class to convey a proper error response using Java's exceptions.
     */
    public static class WrappedResponse extends Exception {
        private final Response response;

        public WrappedResponse(Response response) {
            this.response = response;
        }

        public WrappedResponse( Throwable cause, Response response ) {
            super( cause );
            this.response = response;
        }

        public Response getResponse() {
            return response;
        }

        /**
         * Creates a new response, based on the original response and the passed message.
         * Typical use would be to add a better error message to the HTTP response.
         * @param message additional message to be added to the response.
         * @return A Response with updated message field.
         */
        public Response refineResponse( String message ) {
            final Status statusCode = Response.Status.fromStatusCode(response.getStatus());
            String baseMessage = getWrappedMessageWhenJson();

            if ( baseMessage == null ) {
                final Throwable cause = getCause();
                baseMessage = (cause!=null ? cause.getMessage() : "");
            }
            return error(statusCode, message+" "+baseMessage);
        }

        /**
         * In the common case of the wrapped response being of type JSON,
         * return the message field it has (if any).
         * @return the content of a message field, or {@code null}.
         */
        String getWrappedMessageWhenJson() {
            if ( response.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE) ) {
                Object entity = response.getEntity();
                if ( entity == null ) return null;

                String json = entity.toString();
                try ( StringReader rdr = new StringReader(json) ){
                    JsonReader jrdr = Json.createReader(rdr);
                    JsonObject obj = jrdr.readObject();
                    if ( obj.containsKey("message") ) {
                        JsonValue message = obj.get("message");
                        return message.getValueType() == ValueType.STRING ? obj.getString("message") : message.toString();
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        }
    }
    
    @EJB
    protected SettingsServiceBean settingsSvc;
    
    
    
    
    @PersistenceContext(unitName = "trsa-WebPU")
    private EntityManager em;
    
    @Context
    protected HttpServletRequest httpRequest;


    /**
     * For pretty printing (indenting) of JSON output.
     */
    public enum Format {

        PRETTY
    }

//    private final LazyRef<JsonParser> jsonParserRef = new LazyRef<>(new Callable<JsonParser>() {
//        @Override
//        public JsonParser call() throws Exception {
//            return new JsonParser(datasetFieldSvc, metadataBlockSvc,settingsSvc);
//        }
//    });
//
//    /**
//     * Functional interface for handling HTTP requests in the APIs.
//     *
//     * @see #response(edu.harvard.iq.dataverse.api.AbstractApiBean.DataverseRequestHandler)
//     */
//    protected static interface DataverseRequestHandler {
//        Response handle( DataverseRequest u ) throws WrappedResponse;
//    }

    
    
    
    /* ====================== *\
     *  HTTP Response methods *
    \* ====================== */

    protected Response ok( JsonArrayBuilder bld ) {
        return Response.ok(Json.createObjectBuilder()
            .add("status", STATUS_OK)
            .add("data", bld).build()).build();
    }

    protected Response ok( JsonObjectBuilder bld ) {
        return Response.ok( Json.createObjectBuilder()
            .add("status", STATUS_OK)
            .add("data", bld).build() )
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    protected Response ok( String msg ) {
        return Response.ok().entity(Json.createObjectBuilder()
            .add("status", STATUS_OK)
            .add("data", Json.createObjectBuilder().add("message",msg)).build() )
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    protected Response ok( boolean value ) {
        return Response.ok().entity(Json.createObjectBuilder()
            .add("status", STATUS_OK)
            .add("data", value).build() ).build();
    }

    /**
     * @param data Payload to return.
     * @param mediaType Non-JSON media type.
     * @return Non-JSON response, such as a shell script.
     */
    protected Response ok(String data, MediaType mediaType) {
        return Response.ok().entity(data).type(mediaType).build();
    }

    protected Response created( String uri, JsonObjectBuilder bld ) {
        return Response.created( URI.create(uri) )
                .entity( Json.createObjectBuilder()
                .add("status", "OK")
                .add("data", bld).build())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    protected Response accepted(JsonObjectBuilder bld) {
        return Response.accepted()
                .entity(Json.createObjectBuilder()
                        .add("status", STATUS_WF_IN_PROGRESS)
                        .add("data",bld).build()
                ).build();
    }
    
    protected Response accepted() {
        return Response.accepted()
                .entity(Json.createObjectBuilder()
                        .add("status", STATUS_WF_IN_PROGRESS).build()
                ).build();
    }

    protected Response notFound( String msg ) {
        return error(Response.Status.NOT_FOUND, msg);
    }

    protected Response badRequest( String msg ) {
        return error( Response.Status.BAD_REQUEST, msg );
    }
    
    protected Response forbidden( String msg ) {
        return error( Response.Status.FORBIDDEN, msg );
    }
    
    protected Response badApiKey( String apiKey ) {
        return error(Response.Status.UNAUTHORIZED, (apiKey != null ) ? "Bad api key " : "Please provide a key query parameter (?key=XXX) or via the HTTP header " + DATAVERSE_KEY_HEADER_NAME );
    }

//    protected Response permissionError( PermissionException pe ) {
//        return permissionError( pe.getMessage() );
//    }

    protected Response permissionError( String message ) {
        return unauthorized( message );
    }
    
    protected Response unauthorized( String message ) {
        return error( Response.Status.UNAUTHORIZED, message );
    }

    protected static Response error( Response.Status sts, String msg ) {
        return Response.status(sts)
                .entity( NullSafeJsonBuilder.jsonObjectBuilder()
                        .add("status", STATUS_ERROR)
                        .add( "message", msg ).build()
                ).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

   protected Response allowCors( Response r ) {
       r.getHeaders().add("Access-Control-Allow-Origin", "*");
       return r;
   }
}

class LazyRef<T> {
    private interface Ref<T> {
        T get();
    }

    private Ref<T> ref;

    public LazyRef( final Callable<T> initer ) {
        ref = () -> {
            try {
                final T t = initer.call();
                ref = () -> t;
                return ref.get();
            } catch (Exception ex) {
                Logger.getLogger(LazyRef.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        };
    }

    public T get()  {
        return ref.get();
    }
}