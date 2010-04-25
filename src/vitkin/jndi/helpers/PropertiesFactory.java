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
 * @version $Revision$
 */
public class PropertiesFactory implements ObjectFactory
{
  //~ Static fields/initializers -----------------------------------------------
  
  /** DOCUMENT ME! */
  private static final Logger logger = Logger.getLogger(PropertiesFactory.class);

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
  public Object getObjectInstance(final Object obj, final Name name,
    final Context nameCtx, final Hashtable environment)
    throws Exception
  {
    // Acquire an instance of the Properties class
    final Properties properties = new Properties();

    // Customize the properties from our attributes
    for (final Enumeration addrs = ((Reference) obj).getAll();
        addrs.hasMoreElements();)
    {
      final RefAddr addr = (RefAddr) addrs.nextElement();
      properties.put(addr.getType(), addr.getContent());
    }

    if (logger.isDebugEnabled())
    {
      logger.debug("Got properties: " + properties);
    }

    return properties;
  }
}
