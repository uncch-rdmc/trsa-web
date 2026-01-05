/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.api;

import java.util.logging.Logger;
import jakarta.ejb.Stateless;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import static edu.harvard.iq.dataverse.util.json.NullSafeJsonBuilder.jsonObjectBuilder;
import static edu.harvard.iq.dataverse.util.json.JsonPrinter.*;
import jakarta.ws.rs.PathParam;
import us.cyberimpact.trsa.settings.Settings;
/**
 *
 * @author asone
 */
@Path("admin")
@Stateless
public class Admin extends AbstractApiBean {

    private static final Logger logger = Logger.getLogger(Admin.class.getName());

    @Path("settings")
    @GET
    public Response listAllSettings() {
        JsonObjectBuilder bld = jsonObjectBuilder();
        settingsSvc.listAll().forEach(s -> bld.add(s.getName(), s.getContent()));
        return ok(bld);
    }

    @Path("settings/{name}")
    @PUT
    public Response putSetting(@PathParam("name") String name, String content) {
        Settings s = settingsSvc.set(name, content);
        return ok(jsonObjectBuilder().add(s.getName(), s.getContent()));
    }

    @Path("settings/{name}")
    @GET
    public Response getSetting(@PathParam("name") String name) {
        String s = settingsSvc.get(name);

        return (s != null) ? ok(s) : notFound("Setting " + name + " not found");
    }

    @Path("settings/{name}")
    @DELETE
    public Response deleteSetting(@PathParam("name") String name) {
        settingsSvc.delete(name);

        return ok("Setting " + name + " deleted.");
    }

    
    
    
    
}
