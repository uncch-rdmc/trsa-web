/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.util;

import java.io.Serializable;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class GlobalId implements Serializable {

    public GlobalId(String identifier) {
        this.identifier = identifier;
    }
    
    
    
    
    
    private String identifier;
    
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    
}
