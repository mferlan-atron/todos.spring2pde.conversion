package de.atron.todos.pde.spring.web;

import javax.servlet.ServletException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator, ServiceListener {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private BundleContext bundleContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.bundleContext = context;
		doRegister();
		try{
		synchronized (this) {
			context.addServiceListener(this, "(objectClass=" + HttpService.class.getName() + ")");
		}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		bundleContext = null;
		unregister();

	}

	/**
	 * 
	 * @throws InvalidSyntaxException
	 * @throws ServletException
	 * @throws NamespaceException
	 */
	private void register() throws InvalidSyntaxException, ServletException, NamespaceException {
		ServiceReference[] httpReferences = bundleContext.getServiceReferences(HttpService.class.getName(), null);
		HttpService httpService = null;
		if (httpReferences != null) {
			httpService = (HttpService) bundleContext.getService(httpReferences[0]);
		} else {
			logger.info("No http service available");
		}

		if (httpService != null) {
			logger.info("training servlet will be registered.");
			httpService.registerResources("/todos", "/htmls", null);
		} else {
			logger.info("No servlet to register, problem with training service or http service");
		}
	}

	/**
	 * 
	 */
	private void doRegister() {
		try {
			register();
		} catch (InvalidSyntaxException e) {
			logger.error("Could not register servlet based on an Invalid Syntax", e);
		} catch (ServletException e) {
			logger.error("Could not register servlet based on an Servlet exception", e);
		} catch (NamespaceException e) {
			logger.error("Could not register servlet based on an Namespace exception", e);
		}
	}

	/**
	 * 
	 * @throws InvalidSyntaxException
	 */
	private void unregister() throws InvalidSyntaxException {
		logger.info("Unregister a servlet");
		ServiceReference[] httpReferences = bundleContext.getServiceReferences(HttpService.class.getName(), null);
		if (httpReferences != null) {
			HttpService httpService = (HttpService) bundleContext.getService(httpReferences[0]);
			httpService.unregister("/todos");
		}
	}

	/**
	 * 
	 */
	private void doUnregister() {
		try {
			unregister();
		} catch (InvalidSyntaxException e) {
			logger.error("Could not unregister servlet", e);
		}
	}

	public void serviceChanged(ServiceEvent event) {
		String objectClass = ((String[]) event.getServiceReference().getProperty("objectClass"))[0];
		logger.info("Service change event occurred for : {}", objectClass);
		if (event.getType() == ServiceEvent.REGISTERED) {
			doRegister();
		} else if (event.getType() == ServiceEvent.UNREGISTERING) {
			doUnregister();
		} else if (event.getType() == ServiceEvent.MODIFIED) {
			doUnregister();
			doRegister();
		}
	}

}
