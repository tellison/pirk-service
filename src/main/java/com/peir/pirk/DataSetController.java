package com.peir.pirk;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import io.swagger.api.NotFoundException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.model.DataSchema;

/* A controller for the API impl.
 * Handles incoming translation to Model concepts and translating model replies back to REST responses.
 */
public class DataSetController {

    private static final Logger logger = LoggerFactory.getLogger(DataSetController.class);

    static DataSetOperations handler = new DataSetOperations(new DataSetLoader());

    // GET /v1/data_schemas
    public static Response dataSchemasGet() {
        logger.info("dataSchemasGet");
        List<String> names = handler.getDataSetNames();
        return Response.ok(names).build();
    }

    // GET /v1/data_schemas/{id}
    public static Response dataSchemasIdGet(String id) throws NotFoundException {
        logger.info("dataSchemasIdGet id={}", id);
        // Decode request
        String schemaName = null;
        try {
            schemaName = URLDecoder.decode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 is a required encoding type
        }
        logger.debug("Decoded name : {}", schemaName);

        // Run request on model
        DataSchema schema = handler.getDataSchema(schemaName);

        // Compose appropriate response
        if (schema == null) {
            logger.info("Schema not found : {}", schemaName);
            throw new NotFoundException(404, "Schema named " + schemaName + " is not here");
        }
        return Response.ok(schema).build();
    }
}
