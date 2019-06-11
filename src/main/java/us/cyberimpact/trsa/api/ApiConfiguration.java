package us.cyberimpact.trsa.api;


import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author asone
 */

@ApplicationPath("api/v1")
public class ApiConfiguration extends ResourceConfig {
   
   public ApiConfiguration() {
       packages("us.cyberimpact.trsa.api");
       register(us.cyberimpact.trsa.api.TrsaProfileRestFacade.class);
   }
}
