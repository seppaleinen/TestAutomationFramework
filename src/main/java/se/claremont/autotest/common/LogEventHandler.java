package se.claremont.autotest.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;

/**
 * Created by jordam on 2016-10-10.
 */
@Path("logevent")
public class LogEventHandler {

    private final static Logger logger = LoggerFactory.getLogger( LogEventHandler.class );

    public LogEventHandler() {
        super();
    }

    @POST
    @Consumes("application/json")
    @Produces("text/plain")
    public String postLogEvent(@Context Request request, String json) {
        logger.debug( "received event:" + json );
        //LogEventRouter.sendPost(json);
        return "event received " + json;
    }

    @GET
    @Produces("text/plain")
    public String getLogEvent(@Context Request request) {
        return "nothing to report from getMovieEvent";
    }

}
