package com.peir.pirk;

import javax.ws.rs.core.Response;

import org.apache.pirk.utils.PIRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* A controller for the query and query API impl.
 * Handles incoming translation to Model concepts and translating model replies back to REST responses.
 */
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    static QueryOperations handler = new QueryOperations();

    // PUT /v1/queries/{id}
    public static Response queriesIdPut(String id, io.swagger.model.Query query) {
        logger.info("queriesIdPut");

        if ((id == null) || !(id.equals(query.getId()))) {
            return errorResponse("ID of URL does not match ID of payload", null);
        }

        try {
            handler.storeQuery(query.getId(), query);
        } catch (PIRException e) {
            return errorResponse(e.getLocalizedMessage(), e);
        }

        return Response.ok(query.getId()).build();
    }

    private static Response errorResponse(String message, Exception e) {
        // TODO make this real
        System.out.println(message);
        e.printStackTrace();
        return Response.notAcceptable(null).build();
    }
}
