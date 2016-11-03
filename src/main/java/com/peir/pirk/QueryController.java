package com.peir.pirk;

import javax.ws.rs.core.Response;

import org.apache.pirk.utils.PIRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.model.Query;

/* A controller for the query and query API impl.
 * Handles incoming translation to Model concepts and translating model replies back to REST responses.
 */
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    static QueryOperations handler = new QueryOperations();

    // GET /v1/queries
    public static Response queriesGet() {
        logger.info("queriesGet");
        return Response.ok(handler.storedQueryNames()).build();
    }

    // GET /v1/queries/{id}
    public static Response queriesIdGet(String id) {
        logger.info("queriesIdGet");

        if (id == null) {
            return errorResponse("ID of URL is null", null);
        }

        Query query = handler.retrieveQuery(id);
        if (query == null) {
            return errorResponse("No such query", null);
        }

        return Response.ok(query).build();
    }

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
