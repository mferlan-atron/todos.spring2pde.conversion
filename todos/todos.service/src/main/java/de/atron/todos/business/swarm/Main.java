package de.atron.todos.business.swarm;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.config.logging.Level;
import org.wildfly.swarm.logging.LoggingFraction;


public class Main {

    public static void main(String[] args) throws Exception {
		Level level = Level.CONFIG;
		String maxSize = "10m";
		Integer maxFiles = 10;
		String logsDirectory = "I:/Users/ferlan/logs";
		LoggingFraction loggingFraction = new LoggingFraction().consoleHandler(level, "COLOR_PATTERN")
				.formatter("PATTERN", "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%t] (%c{1}) %s%e%n")
				.formatter("COLOR_PATTERN", "%K{level}%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%t] (%c{1}) %s%e%n")
				.formatter("AUDIT", "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [TODOS] (%X{REQUEST_ID}) (%c{1}) %s%e%n")
				.periodicSizeRotatingFileHandler("FILE", h -> {
					h.level(level).namedFormatter("PATTERN").append(true).suffix(".yyyy-MM-dd").rotateSize(maxSize)
							.enabled(true).encoding("UTF-8").maxBackupIndex(maxFiles);
					Map<String, String> fileSpec = new HashMap<>();
					fileSpec.put("path", logsDirectory + "/" + "system.log");
					h.file(fileSpec);
				}).periodicSizeRotatingFileHandler("FILE_AUDIT_HANDLER", h -> {
					h.level(level).namedFormatter("AUDIT").append(true).suffix(".yyyy-MM-dd").rotateSize(maxSize)
							.enabled(true).encoding("UTF-8").maxBackupIndex(maxFiles);
					Map<String, String> fileSpec = new HashMap<>();
					fileSpec.put("path", logsDirectory + "/" + "application.log");
					h.file(fileSpec);
				}).rootLogger(l -> {
					l.level(level).handler("CONSOLE").handler("FILE");
				}).logger("de.atron.todos", l -> {
					l.level(level).handler("FILE_AUDIT_HANDLER");
				});

//        
		String URL = "jdbc:h2:mem:todosMain;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
		System.setProperty("swarm.ds.connection.url", URL);
		
        Swarm swarm = new Swarm();
		swarm.fraction(loggingFraction);
		swarm.start();
		
        // commented out datasource configuration as it is moved to project-stages.yml
//        swarm.deploy(Swarm.artifact("com.h2database:h2", "h2"));
//        
//        DatasourceArchive dsArchive = ShrinkWrap.create(DatasourceArchive.class);
//        dsArchive.dataSource("todosDS", (ds) -> {
//			ds.connectionUrl(URL );
//            ds.driverName("h2");
//            ds.userName("sa");
//            ds.password("sa");
//        });
//        swarm.deploy(dsArchive);

		swarm.deploy();
		// commented application deployement as it is automatically recognized
//        JAXRSArchive deployment = ShrinkWrap.create( JAXRSArchive.class, "todos.war" );
//        deployment.setContextRoot("todos");
//        deployment.addPackage("de.atron.todos.business");
//        deployment.addPackage("de.atron.todos.business.dao");
//        deployment.addPackage("de.atron.todos.business.entity");
//        deployment.addPackage("de.atron.todos.business.rest");
//        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/persistence.xml", Main.class.getClassLoader()), "classes/META-INF/persistence.xml");
//        deployment.addAsWebInfResource(new ClassLoaderAsset("META-INF/beans.xml", Main.class.getClassLoader()),
//				"classes/META-INF/beans.xml");
//        deployment.addAllDependencies();
//
//        swarm.deploy(deployment);
//        
        
    
    }
}