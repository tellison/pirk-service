package com.peir.pirk;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

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
                query.getHashBitSize(), query.getDataPartitionBitSize(), pirkSchema.getSchemaName(),
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

    /*
     * Returns a stored Pirk query as a swagger model query.
     * Answers null if there is no such query.
     */
    public io.swagger.model.Query retrieveQuery(String id) {
        Query pirkQuery = store.get(id);
        if (pirkQuery == null) {
            return null;
        }
        
        QueryInfo queryInfo = pirkQuery.getQueryInfo();
        io.swagger.model.Query swaggerQuery = new io.swagger.model.Query();
        swaggerQuery.setId(queryInfo.getIdentifier().toString());
        swaggerQuery.setNumSelectors(queryInfo.getNumSelectors());
        swaggerQuery.setHashBitSize(queryInfo.getHashBitSize());
        swaggerQuery.setNumBitsPerDataElement(queryInfo.getNumBitsPerDataElement());
        swaggerQuery.setDataPartitionBitSize(queryInfo.getDataPartitionBitSize());
        swaggerQuery.setNumPartitionsPerDataElement(queryInfo.getNumPartitionsPerDataElement());
        swaggerQuery.setModulus(pirkQuery.getN().toString(Character.MAX_RADIX));
        
        QuerySchema pirkSchema = QuerySchemaRegistry.get(queryInfo.getQueryType());
        io.swagger.model.QuerySchema swaggerSchema = new io.swagger.model.QuerySchema();
        swaggerQuery.setQuerySchema(swaggerSchema);
        
        swaggerSchema.setName(pirkSchema.getSchemaName());
        swaggerSchema.setDataSchemaName(pirkSchema.getDataSchemaName());
        swaggerSchema.setPrimarySelector(pirkSchema.getSelectorName());
        swaggerSchema.setElementNames(pirkSchema.getElementNames());
        swaggerSchema.setFilter(pirkSchema.getFilterTypeName());
        swaggerSchema.setFilteredFields(new ArrayList<String>(pirkSchema.getFilteredElementNames()));
       
        List<String> queryElements = pirkQuery.getQueryElements().values().stream()
                .map(n -> n.toString(Character.MAX_RADIX))
                .collect(Collectors.toList());
        swaggerQuery.setQueryElements(queryElements);
  
        return swaggerQuery;
    }
    
    private BigInteger parseBigInteger(String modulusString) {
        try {
            return new BigInteger(modulusString, Character.MAX_RADIX);
        } catch (NumberFormatException e) {
            // TODO throw new ApiException(400, e.getLocalizedMessage());
            return BigInteger.ZERO;
        }
    }

}
