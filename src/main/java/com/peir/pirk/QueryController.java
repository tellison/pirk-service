package com.peir.pirk;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.pirk.query.wideskies.Query;
import org.apache.pirk.query.wideskies.QueryInfo;
import org.apache.pirk.schema.query.QuerySchema;
import org.apache.pirk.schema.query.QuerySchemaBuilder;
import org.apache.pirk.schema.query.QuerySchemaRegistry;
import org.apache.pirk.utils.PIRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    // FIXME: temp store
    static Map<String, Query> store = new HashMap<>();

    // PUT /v1/queries/{id}
    public static Response queriesIdPut(String id, io.swagger.model.Query query) {
        logger.info("queriesIdPut");

        System.out.println("queriesPut");

        /* Construct a Pirk query from incoming swagger model query. */

        Query pirkQuery;
        try {
            pirkQuery = buildPirkQuery(query);
            store.put(query.getId(), pirkQuery);
        } catch (PIRException e) {
            e.printStackTrace();
        }

        return Response.ok(query.getId()).build();
    }

    private static Query buildPirkQuery(io.swagger.model.Query query) throws PIRException {
        // Extract the query schema
        QuerySchema pirkSchema = definePirkSchema(query);

        // RSA modulus
        BigInteger modulus = parseBigInteger(query.getModulus());

        // Query elements.
        SortedMap<Integer, BigInteger> queryElements = new TreeMap<>();
        int index = 0;
        for (String element : query.getQueryElements()) {
            queryElements.put(index++, parseBigInteger(element));
        }

        // Build a PIR query info object.
        QueryInfo queryInfo = new QueryInfo(UUID.fromString(query.getId()), query.getNumSelectors(),
                query.getHashBitSize(), query.getHashKey(), query.getDataPartitionBitSize(), pirkSchema.getSchemaName(),
                true, false, false);

        return new Query(queryInfo, modulus, queryElements);
    }

    private static QuerySchema definePirkSchema(io.swagger.model.Query query) throws PIRException {
        io.swagger.model.QuerySchema swaggerSchema = query.getQuerySchema();
        QuerySchema pirkSchema = QuerySchemaRegistry.get(swaggerSchema.getName());

        if (pirkSchema != null) {
            logger.info("Query schema {} is already defined, using that one");
        } else {
            QuerySchemaBuilder schemaBuilder = new QuerySchemaBuilder();
            schemaBuilder.setName(swaggerSchema.getName());
            schemaBuilder.setDataSchemaName(swaggerSchema.getDataSchemaName());
            schemaBuilder.setQueryElementNames(new HashSet<String>(swaggerSchema.getElementNames()));
            schemaBuilder.setSelectorName(swaggerSchema.getPrimarySelector());
            if (swaggerSchema.getFilter() != null) {
                schemaBuilder.setFilterTypeName(swaggerSchema.getFilter());
            }
            try {
                pirkSchema = schemaBuilder.build();
            } catch (IOException e) {
                throw new PIRException(e.getLocalizedMessage());
            }
            QuerySchemaRegistry.put(pirkSchema);
        }
        return pirkSchema;
    }

    private static BigInteger parseBigInteger(String modulusString) {
        try {
            // TODO return new BigInteger(modulusString, Character.MAX_RADIX);
            return new BigInteger(modulusString);
        } catch (NumberFormatException e) {
            // TODO throw new ApiException(400, e.getLocalizedMessage());
            return BigInteger.ZERO;
        }
    }

}
