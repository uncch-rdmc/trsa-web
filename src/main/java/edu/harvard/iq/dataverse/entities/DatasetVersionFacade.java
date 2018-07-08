/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.entities;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author akios
 */
@Named
@Stateless
public class DatasetVersionFacade extends AbstractFacade<DatasetVersion> {

    @PersistenceContext(unitName = "trsa-WebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DatasetVersionFacade() {
        super(DatasetVersion.class);
    }
    
}
