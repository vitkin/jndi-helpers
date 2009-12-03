package vitkin.jndi.helpers;

import org.apache.log4j.Logger;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;


/*******************************************************************************
 * DOCUMENT ME!
 *
 * @author Victor Itkin
 * @version $Revision: 2 $
 */
public class PropertiesFactory implements ObjectFactory
{
  //~ Static fields/initializers -----------------------------------------------

  /** DOCUMENT ME! */
  private static final Logger logger =
    Logger.getLogger(PropertiesFactory.class);

  //~ Methods ------------------------------------------------------------------

  /*****************************************************************************
   * DOCUMENT ME!
   *
   * @param obj DOCUMENT ME!
   * @param name DOCUMENT ME!
   * @param nameCtx DOCUMENT ME!
   * @param environment DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   *
   * @throws Exception DOCUMENT ME!
   */
  public Object getObjectInstance(Object obj, Name name, Context nameCtx,
                                  Hashtable environment)
                           throws Exception
  {
    // Acquire an instance of the Properties class
    Properties properties = new Properties();

    // Customize the properties from our attributes
    Reference ref = (Reference) obj;

    for (Enumeration addrs = ref.getAll(); addrs.hasMoreElements();)
    {
      RefAddr addr = (RefAddr) addrs.nextElement();

      String propName = addr.getType();
      String propValue = (String) addr.getContent();

      properties.put(propName, propValue);
    }

    if (logger.isDebugEnabled())
    {
      logger.debug("Got properties: " + properties);
    }

    return properties;
  }
}
