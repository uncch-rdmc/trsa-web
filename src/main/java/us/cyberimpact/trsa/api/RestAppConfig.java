/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.api;

/**
 *
 * @author asone
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("api/v1")
public class RestAppConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(us.cyberimpact.trsa.api.restservice.TrsaprofileFacadeREST.class);
        return resources;
    }
    
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> map = new HashMap<>();
//        map.put("jersey.config.server.disableMoxyJson", true);
        return map;
    }

}
