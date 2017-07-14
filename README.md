# todos.spring2pde.conversion
Todo Spring WAR example converted to OSGI /PDE/Tycho enabled project

This project contains two branches:

* spring
* pde-spring

# spring 

Branch spring contains project todos-spring that is basically Spring Web application.
It can be imported as Maven project.
It is a three tier application providing a functionalities of todos.

Like a classic spring web application it contains
* persistence layer that is defines with persistence.xml, entities and DAO
* business layer that is expose as REST services
* static html page with java script that provides UI using REST endpoints
* web.xml that defines web application
* spring configuration
* test classes

Libraries used:
 - h2 driver
 - c3p0
 - hibernate (3.6.8.FINAL)
 - spring (3.1.3.FINAL)
 - resteasy
  - maven

Running mvn jetty:run will build project and deploy it on jetty.
Application can be accessed on http://localhost:8080/



# pde-spring 

Branch pde-spring is basically conversion for classic Maven / Spring / Web application to
OSGI/ PDE (manifest first) / Spring application.

We are using our custom target platform that is not accessible and not published.

Libraries used:
 - equinox runtime (neon)
 - h2 driver
 - c3p0
 - hibernate (3.6.8.FINAL)
 - spring (3.1.3.FINAL)
 - spring DM (3.1.3.FINAL)
 - jersey (osgi-jaxrs-publisher)
 - maven/tycho 


Idea behind the application is to use PDE for development, Tycho for building plugins/features/products.
Now instead of single project we have several of them

* todos.pde.spring - project that contains domain model and REST API for our Task resource.
  The difference between classic approach is that we do not need to use src/main/java as source folder.
  We are using src as source folder, spring xml configuration is placed under META_INF/spring and contains additional file osgi-context.xml that registeres rest endpoints, providers to service registry.
  JPA persistence xml (persistence.xml) is located in META_INF.
  All required packages and bundles are described in META_INF/MANIFEST.MF while pom.xml exists only to define parent, artifactId and version that must match bundles symbolic name and version. Type of packaging is eclipse-plugin.
  Package containing Task entity must be exported and hibernate must inclide DynamicImport: * otherwise due to OSGI and classloading hibernate will not know how to map Map to Task
  GsonProvider as message body writer/reader was not required in classic spring application while here we need it so that we know how to serialize/deserialize Task entity.
  

  How it works:
  	Spring DM extender creates ApplicationContext for our bundle, creates all beans (datasource, entity managers, transaction manager, dao, rest resource, exception mappers, providers) and registers OSGI services defined in osgi-context.xml (rest resources, exception mappers, providers)
  	OSGI-JAXRS Publisher listens for all osgi-services published with rest annotations and start/reloads Jersey servlet. 

 * todos.pde.spring.test - all test classes are contained in a separate project so that they are not bundled with main code.
 	This project is a fragment of todos.pde.spring, therefore extends classpath of its host and allows us to test all internal classes.
 	Again maven pom.xml only contains  parent, artifactId and version that must match bundles symbolic name and version and packaging is eclipse-test-plugin


 * todos.pde.spring.web - bundle that registers static web resources to HTTP service.
	Again maven pom.xml only contains  parent, artifactId and version that must match bundles symbolic name and version and packaging is eclipse-plugin     

* todos.pde.spring.feature - feature project that defines todos module
	Again maven pom.xml only contains  parent, artifactId and version that must match features id and version and packaging is eclipse-feature     

* todos.pde.spring.releng - releases engineer project. This is pure maven project representing parent pom that enables tycho, configures target platform for all children (placed in targetdefinition subfolder)
  and defines submodules bundles, features, tests. These submodules are used to group and reference real projects. There is also repositories submodule that contains Todos.product


Running can be by launching Todos.product as eclipse application and opening page
Application can be accessed on http://localhost:8080/todos/index.html

Other solution is build application by running mvn clean verify  in project todos.pde.spring.releng
This will build linux and windows application under todos.pde.spring.releng/target/products/... 
and application can be then executed using either Todos.sh or Todos.exe.
THen open http://localhost:8080/todos/index.html
