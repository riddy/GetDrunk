package com.hackathon.getdrunk;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Webserver extends HttpServlet{
	private static final long serialVersionUID = 2595256201109648370L;

	final static Logger logger = LogManager.getLogger(Webserver.class.getName());

	private String documentRoot;
	
	
	private Server server = new Server();
	private String keystorePath;
	HttpConfiguration httpsRedirectionConfig;
	private ServletContextHandler context;
	private ObjectMapper om;
	private MasterBridge edgeRouter;

	public Webserver() {
		
		this.om = new ObjectMapper();		
	}
	

	public void start(){
		
		// set up HTTP and HTTPS port
		final HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSecureScheme("https");
		httpConfig.setSecurePort(82);
		httpConfig.setOutputBufferSize(1000 * 1024);

		// set up HTTP connector
		final ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
		http.setPort(81);
		http.setIdleTimeout(30000);

		// set up HTTPS connector
		
			server.setConnectors(new Connector[] { http });

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		


		context.setContextPath("/");
		context.addServlet(new ServletHolder(this), "/*");

		server.setHandler(context);

		
		try {
			server.start();
		} catch (Exception e) {
			logger.error("Webserver could not be started");
			e.printStackTrace();
		}
	}
	
	private SecurityHandler basicAuth(final String username, final String password) {
		final String[] defaultRoles = new String[] { "default" };

		final HashLoginService l = new HashLoginService();
		l.putUser(username, Credential.getCredential(password), defaultRoles);

		final Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(defaultRoles);
		constraint.setAuthenticate(true);

		final ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec("/*");

		final ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
		csh.setAuthenticator(new BasicAuthenticator());
		csh.addConstraintMapping(cm);
		csh.setLoginService(l);

		return csh;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			handleGetRequest(request, response);
		} catch (Exception e) {
			logger.error("Error while handle request " 
					+ request.getRequestURL(), e);
		}
	}
	
	private void handleGetRequest(HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		Tcu tcu = Main.getMasterBridge().tcu;


		// always set content type to HTML
		response.setContentType("text/html;charset=utf-8");

		// and status to 200 (OK)
		response.setStatus(HttpServletResponse.SC_OK);
		
		
		if (request.getRequestURI().equals("/tcu"))
		{
			String command = request.getParameter("cmd");
			
			String result = tcu.send(command);
			
			response.getWriter().println(result);
			
		} else if (request.getRequestURI().equals("/tcu-simstate"))
		{			
			String result = tcu.getSimState();
			
			response.getWriter().println(result);
			
		} else if (request.getRequestURI().equals("/print1"))
		{			
			LabelPrinter.printGlas("award1");
			
		} else if (request.getRequestURI().equals("/print2"))
		{			
			LabelPrinter.printGlas("award2");
			
		} else if (request.getRequestURI().equals("/pour-glass"))
		{			
			tcu.pourGlassAmbientWater();
			
		} else if (request.getRequestURI().equals("/pour-glass"))
		{			
			tcu.pourGlassAmbientWater();
			
		}
		else
		{
			ServeStaticFiles(request, response);
		}
	}



	private void ServeStaticFiles(HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException,
			IOException {

		response.getWriter().println("Not available");
		
		/*StaticFileLocations fileLocations = new StaticFileLocations("/");
		String requestedFile = fileLocations.provide(request.getRequestURI().toString());

		// set the content type depending on the file extension
		if (requestedFile.endsWith(".css")) {
			response.setContentType("text/css;charset=utf-8");
		}
		else if (requestedFile.endsWith(".js")) {
			response.setContentType("application/javascript;charset=utf-8");
		}

		// read the file contents
		final File file = new File(documentRoot + requestedFile);
		final byte[] fileData = new byte[(int) file.length()];
		final DataInputStream dis = new DataInputStream(new FileInputStream(file));
		dis.readFully(fileData);
		dis.close();

		response.getOutputStream().write(fileData);*/
	}

	public void shutdown() {
		try {
			context.stop();
		} catch (Exception e) {
			
		}
		try {
			server.stop();
		} catch (Exception e){
			
		}
	}



}
