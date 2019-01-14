/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.api;

import java.util.Set;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author asone
 */
@ApplicationPath("api/v1")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("us.cyberimpact.trsa.api");
    }

//    @Override
//    public Set<Class<?>> getClasses() {
//        Set<Class<?>> resources = new java.util.HashSet<>();
//        addRestResourceClasses(resources);
//        return resources;
//    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
//    private void addRestResourceClasses(Set<Class<?>> resources) {
//        resources.add(us.cyberimpact.trsa.web.api.TRSAprofileResource.class);
//        resources.add(us.cyberimpact.trsa.web.api.TRSAprofilesResource.class);
//        resources.add(us.cyberimpact.trsa.web.api.TrsaProfileRestService.class);
//    }
    

    
    
    
    
}
