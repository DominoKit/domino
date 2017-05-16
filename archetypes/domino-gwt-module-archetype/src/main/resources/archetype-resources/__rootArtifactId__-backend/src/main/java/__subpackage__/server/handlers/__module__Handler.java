#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${subpackage}.server.handlers;

import com.progressoft.brix.domino.api.server.Handler;
import com.progressoft.brix.domino.api.server.RequestHandler;
import ${package}.${subpackage}.shared.response.${module}Response;
import ${package}.${subpackage}.shared.request.${module}Request;

import java.util.logging.Logger;

@Handler("${module}Request")
public class ${module}Handler implements RequestHandler<${module}Request, ${module}Response> {
    private static final Logger LOGGER= Logger.getLogger(${module}Handler.class.getName());
    @Override
    public ${module}Response handleRequest(${module}Request request) {
        LOGGER.info("message recieved from client : "+request.getMessage());
        return new ${module}Response("Server message");
    }
}
