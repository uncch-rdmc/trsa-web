package us.cyberimpact.trsa.exception;

/**
 *
 * @author asone
 */
public class TrsaConfigurationException extends TrsaException {

    String message;
    
    public TrsaConfigurationException(String message) {
        this.message= message;
    }
  
}
