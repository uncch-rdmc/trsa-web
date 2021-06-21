/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.web;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;

/**
 *
 * @author asone
 */
@Named("ingestFailureMessageView")
@ViewScoped
public class IngestFailureMessageView implements Serializable {
    
    private static final Logger logger = Logger.getLogger(IngestFailureMessageView.class.getName());
    
    String stackTrace;

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
    
    @PostConstruct
    public void init(){
        String currentMsgKey = Faces.getSessionAttribute("currentMessageKey");
        stackTrace=Faces.getSessionAttribute(currentMsgKey);
        logger.log(Level.INFO, "stackTrace received={0}", stackTrace);
    }
}
