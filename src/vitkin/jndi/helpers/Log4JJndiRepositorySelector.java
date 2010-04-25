/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
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
 */

package vitkin.jndi.helpers;

import org.apache.log4j.*;
import org.apache.log4j.helpers.*;
import org.apache.log4j.spi.*;

import java.net.*;

import java.util.*;

import javax.naming.*;


/*******************************************************************************
 * Defines a unique logger repository for each web-application in a J2EE environment.<br>
 * This implementation is based primarily on Ceki G&uuml;lc&uuml;'s article
 *  <h3>Supporting the Log4j <code>RepositorySelector</code> in Servlet
 * Containers</h3>at: http://qos.ch/logging/sc.html<br>
 * <br>
 * By default, the class static <code>RepositorySelector</code> variable of
 * the <code>LogManager</code> class is set to a trivial <code>{@link
 * org.apache.log4j.spi.RepositorySelector RepositorySelector}</code>
 * implementation which always returns the same logger repository. a.k.a.
 * hierarchy. In other words, by default log4j will use one hierarchy, the
 * default hierarchy. This behavior can be overridden via the
 * <code>LogManager</code>'s <code>setRepositorySelector(RepositorySelector,
 * Object)</code> method.<br>
 * <br>
 * That is where this class enters the picture. It can be used to define a
 * custom logger repository. It makes use of the fact that in J2EE
 * environments, each web-application is guaranteed to have its own JNDI
 * context relative to the <code>java:comp/env</code> context. In EJBs, each
 * enterprise bean (albeit not each application) has its own context relative
 * to the <code>java:comp/env</code> context. An <code>env-entry</code> in a
 * deployment descriptor provides the information to the JNDI context. Once
 * the <code>env-entry</code> is set, a repository selector can query the JNDI
 * application context to look up the value of the entry. The logging context
 * of the web-application will depend on the value the env-entry. The JNDI
 * context which is looked up by this class is <code>java:comp/env/log4j/context-name</code><br>
 * . <br>
 * Here is an example of an <code>env-entry</code><br>:<blockquote>
 * <pre>
 &lt;env-entry&gt;
 &lt;description&gt;JNDI logging context name for this app&lt;/description&gt;
 &lt;env-entry-name&gt;log4j/context-name&lt;/env-entry-name&gt;
 &lt;env-entry-value&gt;aDistinctiveLoggingContextName&lt;/env-entry-value&gt;
 &lt;env-entry-type&gt;java.lang.String&lt;/env-entry-type&gt;
 &lt;/env-entry&gt;
 </pre></blockquote>
 * <br><em>If multiple applications use the same logging context name, then
 * they will share the same logging context.</em><br>
 * <br>
 * You can also specify the URL for this context's configuration resource.
 * This repository selector (ContextJNDISelector) will use this resource to
 * automatically configure the log4j repository. <br><blockquote>
 * <pre>
 &lt;env-entry&gt;
 &lt;description&gt;URL for configuring log4j context&lt;/description&gt;
 &lt;env-entry-name&gt;log4j/configuration-resource&lt;/env-entry-name&gt;
 &lt;env-entry-value&gt;urlOfConfigrationResource&lt;/env-entry-value&gt;
 &lt;env-entry-type&gt;java.lang.String&lt;/env-entry-type&gt;
 &lt;/env-entry&gt;
 </pre></blockquote>
 * <br>It usually good practice for configuration resources of distinct
 * applications to have distinct names. However, if this is not possible Naming<br>
 * <br>
 * Note that in case no configuration resource is specified, then there will
 * be <b>NO</b> attempt to search for the <em>default</em> configuration files
 * <em>log4j.xml</em> and <em>log4j.properties</em>. This voluntary omission
 * ensures that the configuration file for your application's logger
 * repository will not be confused with the default configuration file for the
 * default logger repository. <br>
 * <br>
 * Given that JNDI is part of the J2EE specification, the JNDI selector is the
 * recommended context selector. <br>
 *
 * @author <a href="mailto:hoju@visi.com">Jacob Kjome</a>
 * @author Ceki G&uuml;lc&uuml;
 * @author Victor Itkin
 * @version $Revision$
 */
public class Log4JJndiRepositorySelector implements RepositorySelector
{
  //~ Static fields/initializers -----------------------------------------------

  /** DOCUMENT ME! */
  static final String JNDI_CONFIGURATION_RESOURCE =
    "java:comp/env/log4j/configuration-resource";

  /** DOCUMENT ME! */
  static final String JNDI_CONFIGURATOR_CLASS =
    "java:comp/env/log4j/configurator-class";

  /** DOCUMENT ME! */
  static final String JNDI_CONTEXT_NAME = "java:comp/env/log4j/context-name";

  /** The name of the default repository is "default" (without the quotes). */
  static final String DEFAULT_REPOSITORY_NAME = "default";

  //~ Instance fields ----------------------------------------------------------

  /** DOCUMENT ME! */
  private final LoggerRepository defaultLoggerRepository;

  /** key: name of logging context, value: Hierarchy instance */
  private final Map hierMap;

  //~ Constructors -------------------------------------------------------------

  /*****************************************************************************
   * public no-args constructor
   */
  public Log4JJndiRepositorySelector()
  {
    hierMap = Collections.synchronizedMap(new HashMap());
    defaultLoggerRepository = LogManager.getLoggerRepository();
  }

  //~ Methods ------------------------------------------------------------------

  /*****************************************************************************
   * Remove the repository with the given name from the list of known
   * repositories.
   *
   * @param contextName DOCUMENT ME!
   *
   * @return the detached repository
   */
  public LoggerRepository detachRepository(String contextName)
  {
    return (LoggerRepository) hierMap.remove(contextName);
  }

  /*****************************************************************************
   * Return the repository selector based on the current JNDI
   * environment. If the respository is retreived for the first time, then
   * also configure the repository using a user specified resource.
   *
   * @return the appropriate JNDI-keyed context name/LoggerRepository
   */
  public LoggerRepository getLoggerRepository()
  {
    String loggingContextName = null;
    Context ctx = null;

    try
    {
      ctx = new InitialContext();

      loggingContextName = (String) ctx.lookup(JNDI_CONTEXT_NAME);
    }
    catch (NamingException ne)
    {
      // we can't log here
    }

    if ((loggingContextName == null) ||
          loggingContextName.equals(DEFAULT_REPOSITORY_NAME))
    {
      return defaultLoggerRepository;
    }
    else
    {
      Hierarchy hierarchy = (Hierarchy) hierMap.get(loggingContextName);

      if (hierarchy == null)
      {
        // create new hierarchy
        hierarchy = new Hierarchy(new RootLogger(Level.DEBUG));

        hierMap.put(loggingContextName, hierarchy);

        // Check if Mrs. Piggy gave us explicit configration directives
        // regarding this directory.
        String configResourceStr = lookup(ctx, JNDI_CONFIGURATION_RESOURCE);

        // For non-default repositories we do not try to search for default
        // config files such as log4j.xml or log4j.properties because
        // we have no deterministic way of finding the right one
        if (configResourceStr != null)
        {
          String configuratorClassName = lookup(ctx, JNDI_CONFIGURATOR_CLASS);

          initialConfiguration(hierarchy, loggingContextName,
                               configResourceStr, configuratorClassName);
        }
      }

      return hierarchy;
    }
  }

  /*****************************************************************************
   * Get the logger repository with the corresponding name.<br>
   * Returned value can be null if the selector is unaware of the repository
   * with the given name.<br>
   *
   * @param name DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public LoggerRepository getLoggerRepository(String name)
  {
    if (DEFAULT_REPOSITORY_NAME.equals(name))
    {
      return defaultLoggerRepository;
    }
    else
    {
      return (LoggerRepository) hierMap.get(name);
    }
  }

  /*****************************************************************************
   * Configure <code>repository</code> using
   * <code>configuratonResourceStr</code>  and <code>configuratorClassNameStr</code>.<br>
   * If <code>configuratonResourceStr</code>  is not a URL it will be searched
   * as a resource from the classpath.
   *
   * @param repository The repository to configre
   * @param name DOCUMENT ME!
   * @param configuratonResourceStr URL to the configuration resourc
   * @param configuratorClassNameStr The name of the class to use as the
   *        configrator. This parameter can be null.
   */
  public static void initialConfiguration(LoggerRepository repository,
                                          String name,
                                          String configurationResourceStr,
                                          String configuratorClassNameStr)
  {
    URL url = null;

    try
    {
      url = new URL(configurationResourceStr);
    }
    catch (MalformedURLException ex)
    {
      // so, resource is not a URL:
      // attempt to get the resource from the class loader path
      // Please refer to Loader.getResource documentation.
      url = Loader.getResource(configurationResourceStr);
    }

    // If we have a non-null url, then delegate the rest of the
    // configuration to the OptionConverter.selectAndConfigure
    // method.
    if (url != null)
    {
      LogLog.debug("Using URL [" + url +
                   "] for automatic log4j configuration of repository named [" +
                   name + "].");

      OptionConverter.selectAndConfigure(url, configuratorClassNameStr,
                                         repository);
    }
  }

  /*****************************************************************************
   * DOCUMENT ME!
   *
   * @param ctx DOCUMENT ME!
   * @param name DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static String lookup(Context ctx, String name)
  {
    if (ctx == null)
    {
      return null;
    }

    try
    {
      return (String) ctx.lookup(name);
    }
    catch (NamingException e)
    {
      return null;
    }
  }
}
