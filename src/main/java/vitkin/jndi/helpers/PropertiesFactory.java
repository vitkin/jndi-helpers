/*
 * #%L
 * jndi-helpers
 * %%
 * Copyright (C) 2009 - 2013 Victor Itkin
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
