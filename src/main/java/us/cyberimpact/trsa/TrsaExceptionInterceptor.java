package us.cyberimpact.trsa;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author asone
 */
@Interceptor
public class TrsaExceptionInterceptor implements Serializable{
  private static final Logger logger = Logger.getLogger(TrsaExceptionInterceptor.class.getName());
  
  @AroundInvoke
  public Object handle(InvocationContext context) throws Exception{
      logger.log(Level.INFO, "TrsaExceptionInterceptor#handle: before call:{0}", context.getMethod().getName());
      Object result =  context.proceed();
      logger.log(Level.INFO, "TrsaExceptionInterceptor#handle: after call:{0}", context.getMethod().getName());
      return result;
  }
}
