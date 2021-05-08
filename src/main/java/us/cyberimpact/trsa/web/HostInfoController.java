package us.cyberimpact.trsa.web;

import us.cyberimpact.trsa.entities.HostInfo;
import us.cyberimpact.trsa.web.jsf.util.JsfUtil;
import us.cyberimpact.trsa.web.jsf.util.JsfUtil.PersistAction;
import us.cyberimpact.trsa.entities.HostInfoFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

@Named("hostInfoController")
@ViewScoped
public class HostInfoController implements Serializable {

    private static final Logger logger = Logger.getLogger(HostInfoController.class.getName());
    @Inject
    private HostInfoFacade hostInfoFacade;
    
    private List<HostInfo> items = null;
    
    private HostInfo selected;

    public HostInfoController() {
    }
    
    
    @PostConstruct
    public void init() {
        logger.log(Level.INFO, "========== HostInfoController#init : start ==========");
        if (items !=null){
            logger.log(Level.INFO, "how many hostInfo={0}", items.size());
        }
        
        
        logger.log(Level.INFO, "========== HostInfoController#init : end ==========");
    }
    

    public HostInfo getSelected() {
        return selected;
    }

    public void setSelected(HostInfo selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private HostInfoFacade getFacade() {
        return hostInfoFacade;
    }

    public HostInfo prepareCreate() {
        selected = new HostInfo();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        logger.log(Level.INFO, "========== HostInfoController#create : start ==========");

        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_host").getString("HostInfoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        logger.log(Level.INFO, "========== HostInfoController#create : end ==========");
    }

    public void update() {
        logger.log(Level.INFO, "========== HostInfoController#update : start ==========");
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_host").getString("HostInfoUpdated"));
        logger.log(Level.INFO, "========== HostInfoController#update : end ==========");
    }

    public void destroy() {
        logger.log(Level.INFO, "========== HostInfoController#destroy : start ==========");
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_host").getString("HostInfoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
        logger.log(Level.INFO, "========== HostInfoController#destroy : end ==========");
    }

    public List<HostInfo> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_host").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_host").getString("PersistenceErrorOccured"));
            }
        }
    }

    public HostInfo getHostInfo(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<HostInfo> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<HostInfo> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = HostInfo.class)
    public static class HostInfoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            HostInfoController controller = (HostInfoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "hostInfoController");
            return controller.getHostInfo(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof HostInfo) {
                HostInfo o = (HostInfo) object;
                return getStringKey(o.getId());
            } else {
                logger.log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}",
                        new Object[]{object, 
                            object.getClass().getName(), 
                            HostInfo.class.getName()});
                return null;
            }
        }

    }

}
