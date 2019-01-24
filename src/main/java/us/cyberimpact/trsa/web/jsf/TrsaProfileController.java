package us.cyberimpact.trsa.web.jsf;

import us.cyberimpact.trsa.core.TrsaProfile;
import us.cyberimpact.trsa.web.jsf.util.JsfUtil;
import us.cyberimpact.trsa.web.jsf.util.JsfUtil.PersistAction;
import us.cyberimpact.trsa.web.TrsaProfileFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("trsaProfileController")
@SessionScoped
public class TrsaProfileController implements Serializable {

    @EJB
    private TrsaProfileFacade trsaProfileFacade;
    private List<TrsaProfile> items = null;
    private TrsaProfile selected;

    public TrsaProfileController() {
    }

    public TrsaProfile getSelected() {
        return selected;
    }

    public void setSelected(TrsaProfile selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TrsaProfileFacade getFacade() {
        return trsaProfileFacade;
    }

    public TrsaProfile prepareCreate() {
        selected = new TrsaProfile();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, 
            ResourceBundle.getBundle("/Bundle").getString("TrsaProfileCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, 
            ResourceBundle.getBundle("/Bundle").getString("TrsaProfileUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, 
            ResourceBundle.getBundle("/Bundle").getString("TrsaProfileDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<TrsaProfile> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public TrsaProfile getTrsaProfile(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<TrsaProfile> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<TrsaProfile> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = TrsaProfile.class)
    public static class TrsaProfileControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TrsaProfileController controller = (TrsaProfileController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "trsaProfileController");
            return controller.getTrsaProfile(getKey(value));
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
            if (object instanceof TrsaProfile) {
                TrsaProfile o = (TrsaProfile) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), TrsaProfile.class.getName()});
                return null;
            }
        }

    }

}
