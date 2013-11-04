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
public class Log4JTomcatLifecycleListener implements LifecycleListener
{
  //~ Methods ------------------------------------------------------------------
  
  /*****************************************************************************
   * DOCUMENT ME!
   *
   * @param le DOCUMENT ME!
   *
   * @throws LifecycleException DOCUMENT ME!
   */
  public void lifecycleEvent(LifecycleEvent le)
  {
    if (Lifecycle.START_EVENT.equals(le.getType()))
    {
      LogManager.setRepositorySelector(new Log4JJndiRepositorySelector(), this);
    }
  }
}
