package us.cyberimpact.trsa.web.jsf.util;

import java.util.Map;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

/**
 *
 * @author asone
 */
@FacesConverter("apiTokenConverter")
public class ApiTokenConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {

      Map<String, Object> attributes = uic.getAttributes();
      String length = (String) attributes.get("length");
      if (length== null || length.isBlank()){
          length="23";
      }
      Integer len = Integer.parseInt(length);
      String base = "X".repeat(len-1);
      return base.concat(string.substring(len));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object t) {
        String raw = (String) t;
        Map<String, Object> attributes = uic.getAttributes();
        
        String length = (String) attributes.get("length");
      if (length== null || length.isBlank()){
          length="23";
      }
         Integer len = Integer.parseInt(length);
        String base = "X".repeat(len-1);
        return base.concat(raw.substring(len));
    }
    
}
