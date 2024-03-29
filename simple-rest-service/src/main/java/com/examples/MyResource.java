package com.examples;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getItHTM() {
    	return "<html>\n"
    			+ "<head>\n"
    			+ "<title>Hello Jersey</title>\n"
    			+ "</head>\n"
    			+ "<body>Got it (HTML)!</body>\n"
    			+ "</html>\n";
    }
    
    @GET
    @Produces(MediaType.TEXT_XML)
    public String getItXML() {
    	return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    			+ "<hello>Got it (XML)!</hello>\n";
    }
}
