/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.entities;

/**
 *
 * @author Akio Sone, Univ, of North Carolina at Chapel Hill, H.W. Odum Inst.
 */
public class DatasetAuthor {
    
    private String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    
        private String affiliation;

    /**
     * Get the value of affiliation
     *
     * @return the value of affiliation
     */
    public String getAffiliation() {
        return affiliation;
    }

    /**
     * Set the value of affiliation
     *
     * @param affiliation new value of affiliation
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    
    
}
