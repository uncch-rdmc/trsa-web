/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.api;

import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import us.cyberimpact.trsa.core.TrsaProfile;

/**
 * REST Web Service
 *
 * @author asone
 */
@Stateless
@Path("profiles")
public class TRSAprofileResource {
    
    private static final Logger logger = Logger.getLogger(TRSAprofileResource.class.getName());

    @PersistenceContext(unitName = "trsa-WebPU")
    private EntityManager entityManager;

    
    @Context
    Configuration config;
    
    @Context
    Application app;
    
    
    @Context
    private UriInfo context;
    
    
    public TRSAprofileResource() {
    }
    
//    private String id;


    /**
     * Creates a new instance of TRSAprofileResource
     */
//    private TRSAprofileResource(String id) {
//        this.id = id;
//    }

    /**
     * Get instance of the TRSAprofileResource
     */
//    public static TRSAprofileResource getInstance(String id) {
//        // The user may use some kind of persistence mechanism
//        // to store and restore instances of TRSAprofileResource class.
//        return new TRSAprofileResource(id);
//    }

    /**
     * Retrieves representation of an instance of us.cyberimpact.trsa.web.api.TRSAprofileResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of TRSAprofileResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource TRSAprofileResource
     */
    @DELETE
    public void delete() {
    }
    
    
    /**
     * POST method for creating an instance of TRSAprofileResource
     * @param entity
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(TrsaProfile entity) {
        //TODO
        entityManager.persist(entity);
        return Response.created(context.getAbsolutePath()).build();
    }
    
    
}
