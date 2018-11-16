/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.web.api;

import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import us.cyberimpact.trsa.core.TrsaProfileDTO;
import us.cyberimpact.trsa.core.TrsaProfileDataService;

/**
 *
 * @author asone
 */
@Path("profile")
public class TrsaProfileRestService {
    
    private static final Logger logger = Logger.getLogger(TrsaProfileRestService.class.getName());
    
    private TrsaProfileDataService dataService = TrsaProfileDataService.getInstance();
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TrsaProfileDTO createProfile(TrsaProfileDTO newProfile) {
        return dataService.createProfile(newProfile);
    }
    
    
    
}
