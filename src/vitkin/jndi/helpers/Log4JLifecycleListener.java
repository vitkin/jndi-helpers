package vitkin.jndi.helpers;

import com.sun.appserv.server.LifecycleEvent;
import com.sun.appserv.server.LifecycleListener;
import com.sun.appserv.server.ServerLifecycleException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/*******************************************************************************
 * DOCUMENT ME!
 *
 * @author Victor Itkin
 * @version $Revision$
 */
public class Log4JLifecycleListener implements LifecycleListener
{
  //~ Static fields/initializers -----------------------------------------------
  
  /** DOCUMENT ME! */
  private static final Logger logger = LogManager.getLogger(Log4JLifecycleListener.class);

  //~ Methods ------------------------------------------------------------------

  /*****************************************************************************
   * DOCUMENT ME!
   *
   * @param le DOCUMENT ME!
   *
   * @throws ServerLifecycleException DOCUMENT ME!
   */
  public void handleEvent(LifecycleEvent le) throws ServerLifecycleException
  {
    if (LifecycleEvent.STARTUP_EVENT == le.getEventType())
    {
      logger.info(
        "Setting Log4J repository selector to Log4JJndiRepositorySelector");

      LogManager.setRepositorySelector(new Log4JJndiRepositorySelector(), this);
    }
  }
}
