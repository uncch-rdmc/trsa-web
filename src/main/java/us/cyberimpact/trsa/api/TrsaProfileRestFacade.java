/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.api;

import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import us.cyberimpact.trsa.entities.AbstractFacade;
import us.cyberimpact.trsa.entities.TrsaProfile;

/**
 *
 * @author asone
 */
@Stateless
@Path("trsaprofile")
public class TrsaProfileRestFacade extends AbstractFacade<TrsaProfile> {
    private static final Logger logger = 
            Logger.getLogger(TrsaProfileRestFacade.class.getName());

    @PersistenceContext(unitName = "trsa-WebPU")
    private EntityManager em;

    public TrsaProfileRestFacade() {
        super(TrsaProfile.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response create(TrsaProfile entity) {
        logger.log(Level.INFO, "post: create method was called={0}", entity);
        Jsonb jsonb = JsonbBuilder.create();
        String result = jsonb.toJson(entity);
        
        JsonObject etty = Json.createReader(new StringReader(result)).readObject();
        JsonObject jsn = Json.createObjectBuilder()
                .add("status", "OK")
                .add("TRSAProfile", etty).build();
        
        super.create(entity);
        return Response.status(Response.Status.OK).entity(jsn).build();
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response edit(@PathParam("id") Long id, TrsaProfile entity) {
        super.edit(entity);
        TrsaProfile obj = super.find(id);
        
        Jsonb jsonb = JsonbBuilder.create();
        String result = jsonb.toJson(obj);
        
        JsonObject etty = Json.createReader(new StringReader(result)).readObject();
        JsonObject jsn = Json.createObjectBuilder()
                .add("status", "OK")
                .add("TRSAProfile", etty).build();
        return Response.status(Response.Status.OK).entity(jsn).build();
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") Long id) {
        String message = "TRSA Profile whose id="+id+" has been deleted";
        
        super.remove(super.find(id));
        logger.log(Level.INFO, "profile whose id = {0} has been deleted", id);
        JsonObject jsn = Json.createObjectBuilder()
                .add("status", "OK")
                .add("id", id)
                .add("message", message).build();
        return Response.status(Response.Status.OK).entity(jsn)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response find(@PathParam("id") Long id) {
        TrsaProfile obj = super.find(id);
        logger.log(Level.INFO, "obj={0}", obj);
        Jsonb jsonb = JsonbBuilder.create();
        String result = jsonb.toJson(obj);
        JsonObject etty = Json.createReader(new StringReader(result)).readObject();
        JsonObject jsn = Json.createObjectBuilder()
                .add("status", "OK")
                .add("TRSAProfile", etty).build();
        return Response.status(Response.Status.OK).entity(jsn).build();
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<TrsaProfile> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<TrsaProfile> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
