/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.entities;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author asone
 */
@Stateless
public class TrsaProfileFacade extends AbstractFacade<TrsaProfile> {

    @PersistenceContext(unitName = "trsa-WebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrsaProfileFacade() {
        super(TrsaProfile.class);
    }
    
}
