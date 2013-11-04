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
public class Log4JGlassFishLifecycleListener implements LifecycleListener
{
  //~ Static fields/initializers -----------------------------------------------
  
  /** DOCUMENT ME! */
  private static final Logger logger = LogManager.getLogger(Log4JGlassFishLifecycleListener.class);

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
