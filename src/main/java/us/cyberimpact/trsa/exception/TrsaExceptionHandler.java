package us.cyberimpact.trsa.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

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
                nav.handleNavigation(fc, null, "/error");
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
