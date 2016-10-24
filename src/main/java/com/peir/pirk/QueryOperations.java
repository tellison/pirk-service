package com.peir.pirk;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.pirk.query.wideskies.Query;
import org.apache.pirk.query.wideskies.QueryInfo;
import org.apache.pirk.schema.query.QuerySchema;
import org.apache.pirk.schema.query.QuerySchemaBuilder;
import org.apache.pirk.schema.query.QuerySchemaRegistry;
import org.apache.pirk.utils.PIRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Assumes all URL encoding/decoding has been done on inputs, and will be done on return values.
 * Throws exceptions to indicate errors, and lets caller map to response.
 */
public class QueryOperations {

    private static final Logger logger = LoggerFactory.getLogger(QueryOperations.class);

    // FIXME: temp store
    static Map<String, Query> store = new HashMap<>();

    /*
     * Construct a Pirk query from incoming swagger model query, and store it.
     */
    public Query storeQuery(String id, io.swagger.model.Query query) throws PIRException {
        logger.info("parsing and storing query {}", id);

        Query pirkQuery = buildPirkQuery(query);
        store.put(query.getId(), pirkQuery);

        return pirkQuery;
    }

    private Query buildPirkQuery(io.swagger.model.Query query) throws PIRException {
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

    private QuerySchema definePirkSchema(io.swagger.model.Query query) throws PIRException {
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

    private BigInteger parseBigInteger(String modulusString) {
        try {
            // TODO return new BigInteger(modulusString, Character.MAX_RADIX);
            return new BigInteger(modulusString);
        } catch (NumberFormatException e) {
            // TODO throw new ApiException(400, e.getLocalizedMessage());
            return BigInteger.ZERO;
        }
    }

}
