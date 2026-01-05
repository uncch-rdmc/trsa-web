package us.cyberimpact.trsa.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.faces.FacesException;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.application.ViewExpiredException;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;

/**
 *
 * @author asone
 */
public class TrsaExceptionHandler extends ExceptionHandlerWrapper {
    private static final Logger logger = Logger.getLogger(TrsaExceptionHandler.class.getName());
    
    private ExceptionHandler wrapped;

    public TrsaExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }
    
    
    
    
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() throws FacesException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
                
        final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
        while (i.hasNext()) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context =
                    (ExceptionQueuedEventContext) event.getSource();


            // get the exception from context
            Throwable t = context.getException();

            final FacesContext fc = FacesContext.getCurrentInstance();
            final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
            final NavigationHandler nav = fc.getApplication().getNavigationHandler();

            //here you do what ever you want with exception
            try {

                //log error ?
                logger.log(Level.INFO, "TRSA Exception Handler", t);

                //redirect to error view etc....
                requestMap.put("exceptionMessage", t.getMessage());
                
                t.printStackTrace(pw);
                
                requestMap.put("exceptionTrace", sw.toString());
                
                if (t instanceof ViewExpiredException) {
                    logger.log(Level.INFO, "This exception is an instance of ViewExpiredException");
                    nav.handleNavigation(fc, null, "/index");
                } else {
                    logger.log(Level.INFO, "This exception is NOT an instance of ViewExpiredException");
                    nav.handleNavigation(fc, null, "/error");
                }
                
                fc.renderResponse(); 
               
                // remove the comment below if you want to report the error in a jsf error message
                // JsfUtil.addErrorMessage(t.getMessage());
                
            } finally {
                //remove it from queue
                i.remove();
            }
        }
        //parent hanle
        getWrapped().handle();
    }
    
    
}
