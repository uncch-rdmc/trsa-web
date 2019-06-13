/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.cyberimpact.trsa.entities;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author asone
 */
@Stateless
@Named
public class HostInfoFacade extends AbstractFacade<HostInfo> {
    
    private static final Logger logger = Logger.getLogger(HostInfoFacade.class.getName());

    @PersistenceContext(unitName = "trsa-WebPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HostInfoFacade() {
        super(HostInfo.class);
    }
    
    public HostInfo findByDatasetId(long id){
        try {
            List<HostInfo> result =  em.createNamedQuery("HostInfo.findByDatasetid", HostInfo.class)
                    .setParameter("datasetid", id).getResultList();
            if (result.size()==1){
                logger.log(Level.INFO, "id={0} was unique", id);
                return result.get(0);
            } else {
                logger.log(Level.INFO, "id={0} was not unique: multiple result: return last one", id);
                return result.get(result.size()-1);
            }
                } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }

    }
}
