package vitkin.jndi.helpers;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;

import org.apache.log4j.LogManager;


/*******************************************************************************
 * DOCUMENT ME!
 *
 * @author Victor Itkin
 * @version $Revision$
 */
public class Log4JLifecycleListener implements LifecycleListener
{
  //~ Methods ------------------------------------------------------------------
  
  /*****************************************************************************
   * DOCUMENT ME!
   *
   * @param le DOCUMENT ME!
   *
   * @throws LifecycleException DOCUMENT ME!
   */
  public void lifecycleEvent(LifecycleEvent le) throws LifecycleException
  {
    if (Lifecycle.START_EVENT.equals(le.getType()))
    {
      LogManager.setRepositorySelector(new Log4JJndiRepositorySelector(), this);
    }
  }
}
