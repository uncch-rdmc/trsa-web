package us.cyberimpact.trsa.web;

import us.cyberimpact.trsa.entities.DsTemplateData;
import us.cyberimpact.trsa.web.jsf.util.JsfUtil;
import us.cyberimpact.trsa.web.jsf.util.JsfUtil.PersistAction;
import us.cyberimpact.trsa.entities.DsTemplateDataFacade;

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

@Named("dsTemplateDataController")
@SessionScoped
public class DsTemplateDataController implements Serializable {

    @EJB
    private us.cyberimpact.trsa.entities.DsTemplateDataFacade ejbFacade;
    private List<DsTemplateData> items = null;
    private DsTemplateData selected;

    public DsTemplateDataController() {
    }

    public DsTemplateData getSelected() {
        return selected;
    }

    public void setSelected(DsTemplateData selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DsTemplateDataFacade getFacade() {
        return ejbFacade;
    }

    public DsTemplateData prepareCreate() {
        selected = new DsTemplateData();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DsTemplateDataCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DsTemplateDataUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DsTemplateDataDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<DsTemplateData> getItems() {
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

    public DsTemplateData getDsTemplateData(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<DsTemplateData> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<DsTemplateData> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = DsTemplateData.class)
    public static class DsTemplateDataControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DsTemplateDataController controller = (DsTemplateDataController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "dsTemplateDataController");
            return controller.getDsTemplateData(getKey(value));
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
            if (object instanceof DsTemplateData) {
                DsTemplateData o = (DsTemplateData) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), DsTemplateData.class.getName()});
                return null;
            }
        }

    }

}
