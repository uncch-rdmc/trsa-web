package us.cyberimpact.trsa.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 *
 * @author asone
 */
public class TrsaExceptionHandlerFactory extends ExceptionHandlerFactory{
    
    
   private ExceptionHandlerFactory parent;

    public TrsaExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }
   
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler handler = new TrsaExceptionHandler(parent.getExceptionHandler());
        return handler;
    }
}
