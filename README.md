Getting started
===============

JNDI Helpers is a set of classes to bring some JNDI based features to J2EE/JEE
based applications:

- **PropertyFactory**, to share unique editable configurations based on
  `java.util.Properties`.
  Useful especially for clustered instances.

- **Log4JJndiRepositorySelector**, to define a unique _Log4J_ logger repository
  for each Web application context in a J2EE/JEE environment.

* * *

PropertyFactory
---------------

### Installation

#### GlassFish

 1. Place the JAR file under the application server instance shared libraries
    folder `glassfish/domains/domain1/lib/` if the instance is `domain1`.

 2. Add a _JNDI Custom Resource_ which:

    **_JNDI Name_** is `properties/MyConfigurationName` or whatever you prefer.

    **_Resource Type_** is `java.util.Properties`.

    **_Factory Class_** is `vitkin.jndi.helpers.PropertiesFactory`

    by either using the _Server Administration Console_ or by editing the file
    `glassfish/domains/domain1/config/domain.xml` as follow:

     1. Add the custom resource element containing the configuration as
        the last element under the `<resources>` element.
        
        ```xml
        <custom-resource res-type="java.util.Properties" 
                         jndi-name="properties/MyConfigurationName" 
                         factory-class="vitkin.jndi.helpers.PropertiesFactory">
          <property name="firstPropertyName" value="firstPropertyValue"></property>
          <property name="secondPropertyName" value="secondPropertyValue"></property>
          ...
        </custom-resource>
        ```

     2. Add the resource reference element as the last element under the
        `<server>` element matching the server configuration in use.
        
        ```xml
        <servers>
          <server name="server" config-ref="server-config">
            ...
            <resource-ref ref="properties/MyConfigurationName"></resource-ref>
          </server>
        </servers>
        ```

 3. Finally restart the application server instance.

#### Tomcat 7.x

 1. Place the JAR file under the application server instance _Common_ libraries
    folder that should be `$CATALINA_BASE/lib/` but may differ depending on
    the Tomcat configuration.

 2. Edit the application `Context` to add the properties. For instance for
    a per application context basis configuration, edit the file
    `$CATALINA_BASE/conf/Catalina/localhost/MyApplication.xml` and add the
    properties as in the following example:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <Context antiJARLocking="true" path="/MyApplication">
      <Resource
        name="properties/MyConfigurationName"
        auth="Container"
        type="java.util.Properties"
        factory="vitkin.jndi.helpers.PropertiesFactory"
        firstPropertyName="firstPropertyValue"
        secondPropertyName="secondPropertyValue"
        ...
      />
    </Context>
    ```

 3. Finally restart the application server instance.

> **Notes:**
>
> - See [Apache Tomcat 7 Class Loader HOW-TO]
>   (http://tomcat.apache.org/tomcat-7.0-doc/class-loader-howto.html
>    "Class Loader HOW-TO") for more information.
>
> - See [Apache Tomcat 7 JNDI Resources HOW-TO]
>   (http://tomcat.apache.org/tomcat-7.0-doc/jndi-resources-howto.html
>    "JNDI Resources HOW-TO") for more information.


### Integration

 1. In the `web.xml` file of your Web application and/or any other similar
    deployment descriptor based on your needs add the following resource
    reference element:
    ```xml
    <resource-ref>
      <description>Configuration</description>
      <res-ref-name>properties/MyConfigurationName</res-ref-name>
      <res-type>java.util.Properties</res-type>
      <res-auth>Container</res-auth>
      <res-sharing-scope>Shareable</res-sharing-scope>
    </resource-ref>
    ```

 2. Then simply make the configuration being loaded into a `Properties` field
    of a class using the `@Resource` annotation as in the following example:
    ```java
    @Resource(name = "properties/MyConfigurationName")
    private Properties myConfiguration;
    ```

### Usage

Simply edit your configuration properties and restart the application.

* * *

Log4JJndiRepositorySelector
---------------------------

### Installation

> **Notes:**
> When not specifying the configuration file to use, _Log4J_ automatically
> looks for an XML file called `log4j.xml` in the classpath.

#### GlassFish

 1. Place the JAR file under the application server instance shared libraries
    folder `glassfish/domains/domain1/lib/` if the instance is `domain1`.

 2. Place the _Log4J_ XML configuration files under the folder
    `glassfish/domains/domain1/lib/classes/`. For default configuration,
    place in that folder a file simply called `log4j.xml` and for your context
    logger a file called `MyContextName.log4j.xml` or whatever name you prefer. 

 3. Add a _Lifecycle Module_ which:

    **_Name_** is `Log4JLifecycleListener` or whatever you prefer.

    **_Class Name_** is `vitkin.jndi.helpers.Log4JGlassFishLifecycleListener`.

    by either using the _Server Administration Console_ or by editing the file
    `glassfish/domains/domain1/config/domain.xml` as follow:
  
     1. Add the Lifecycle application element as the last element under
        the `<applications>` element.

        ```xml
        <application name="Log4JLifecycleListener" object-type="user">
          <property name="is-failure-fatal" value="false"></property>
          <property name="class-name" value="vitkin.jndi.helpers.Log4JGlassFishLifecycleListener"></property>
          <property name="isLifecycle" value="true"></property>
        </application>
        ```
    
     2. Add the application reference element as the last one under
        the `<server>` element matching the server configuration in use.

        ```xml
        <servers>
          <server name="server" config-ref="server-config">
            ...
            <application-ref ref="Log4JLifecycleListener" virtual-servers="server"></application-ref>
            ...
          </server>
        </servers>
        ```

 3. Finally restart the application server instance.

> **Notes:**
>
> - For clustered instances you don't need to perform the steps on each
>   instance. Actually whenever files placed under the `lib/` folder of
>   the master are automatically copied to all instances `lib/` folders.
>
> - You can use `${com.sun.aas.instanceRoot}/logs/` when specifying
>   the location of the log files for a given _Log4J_ file based _Appender_.

#### Tomcat 7.x

 1. Place the JAR file under the application server instance _Common_ libraries
    folder that should be `$CATALINA_BASE/lib/` but may differ depending on
    the Tomcat configuration.

 2. Place the _Log4J_ XML configuration files under the _Common_ libraries folder
    (`$CATALINA_BASE/lib/` or other...). For default configuration, place in
    that folder a file simply called `log4j.xml` and for your context logger
    a file called `MyContextName.log4j.xml` or whatever name you prefer. 

 3. Add a _Listener_ to the `$CATALINA_BASE/conf/server.xml` under
    the `<Server>` element as in the following example:
    ```xml
    <Server port="8005" shutdown="SHUTDOWN">
      ...
      <Listener className="vitkin.jndi.helpers.Log4JTomcatLifecycleListener" />
      ...
    </Server>
    ```

 4. Finally restart the application server instance.

> **Notes:**
>
> - See [Apache Tomcat 7 Class Loader HOW-TO]
>   (http://tomcat.apache.org/tomcat-7.0-doc/class-loader-howto.html
>   "Class Loader HOW-TO") for more information.
>
> - See [Apache Tomcat 7 The LifeCycle Listener Component]
>   (http://tomcat.apache.org/tomcat-7.0-doc/config/listeners.html
>   "The LifeCycle Listener Component") for more information.


### Integration

In the `web.xml` and/or `ejb-jar.xml` file and/or any other similar deployment
descriptor based on your needs of your Web application add the following
environment entry elements:
```xml
<env-entry>
  <env-entry-name>log4j/context-name</env-entry-name>
  <env-entry-type>java.lang.String</env-entry-type>
  <env-entry-value>MyContextName</env-entry-value>
</env-entry>
<env-entry>
  <env-entry-name>log4j/configuration-resource</env-entry-name>
  <env-entry-type>java.lang.String</env-entry-type>
  <env-entry-value>MyContextName.log4j.xml</env-entry-value>
</env-entry>
```

### Usage

Simply edit your _Log4J_ configuration XML and restart the application.
