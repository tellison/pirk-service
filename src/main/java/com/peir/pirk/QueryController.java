package com.peir.pirk;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.pirk.query.wideskies.QueryInfo;
import org.apache.pirk.schema.query.QuerySchema;
import org.apache.pirk.schema.query.filter.DataFilter;
import org.apache.pirk.schema.query.filter.FilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.model.Query;

public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    // PUT /v1/queries/{id}
    public static Response queriesIdPut(String id, Query query) {
        logger.info("queriesIdPut");

        System.out.println("queriesPut");

        /* Construct a Pirk query from incoming swagger model query. */

        //TODO: shift all this schema building into the core pirk capability
        
        DataFilter filter = FilterFactory.getFilter(query.getQuerySchema().getFilter(), new HashSet<String>(query.getQuerySchema().getFilteredFields()));
        //FIXME: Should be computed from data elements 
        int dataElementSize = 0;
        // Query schema
        QuerySchema qs = new QuerySchema(
            query.getQuerySchema().getName(),
            query.getQuerySchema().getDataSchemaName(),
            query.getQuerySchema().getPrimarySelector(),
            query.getQuerySchema().getFilter(),
            filter,
            dataElementSize);
        
        
        // RSA modulus
        BigInteger modulus = parseBigInteger(query.getModulus());

        // Query elements.
        SortedMap<Integer, BigInteger> queryElements = new TreeMap<>();
        int index = 0;
        for (String element : query.getQueryElements()) {
            queryElements.put(index++, parseBigInteger(element));
        }

        QueryInfo qi = new QueryInfo(
            UUID.fromString(query.getId()),
            query.getNumSelectors(),
            query.getHashBitSize(),
            query.getHashKey(),
            query.getDataPartitionBitSize(),
            "querySchemaName",
            true, false, false);

        org.apache.pirk.query.wideskies.Query pirkQuery = new org.apache.pirk.query.wideskies.Query(qi, modulus,
                queryElements);
        // QuerySchema qs = new QuerySchema();
        // List<String> queryElements = new ArrayList<>();
        // Query q = new Query();
        //
        // q.setId(id);
        // q.setNumSelectors(2);
        // q.setHashBitSize(12);
        // q.setHashKey("Foobar");
        // q.setNumBitsPerDataElement(96);
        // q.setDataPartitionBitSize(8);
        // q.setNumPartitionsPerDataElement(12);
        // q.setModulus(
        // "30049399560646469676005493659082752898785212655599017144565972358099446280042588990477674146012309524890531874672789");
        // q.setQuerySchema(qs);
        // q.setQueryElements(queryElements);
        //
        // qs.setDataSchemaName("simple query");
        // qs.setDataSchemaName("Simple Data Schema 1");
        // qs.setElementNames(Arrays.asList("name", "age"));
        // qs.setFilter("com.example.Foo");
        // qs.setFilteredFields(Arrays.asList("filtered_field"));
        //
        // queryElements.add(
        // "191550723406084518591469029284995656590807890583952791477054646763013652531022594807624560411348538761707449665459721391970132574471789554192515615653292243752775621542596319777517406846487590458619682935632633366194785222228749954");
        // queryElements.add(
        // "708761260223492284214606189608835350828740716107713085191175122749726459266750635699507731862886370744878661635914854481831914893579356324072737130381958968722184499314288762113389523795210662995774011539282843855788464474546164091");

        return Response.ok().build();
    }

    private static BigInteger parseBigInteger(String modulusString) {
        try {
            return new BigInteger(modulusString, Character.MAX_RADIX);
        } catch (NumberFormatException e) {
            //TODO throw new ApiException(400, e.getLocalizedMessage());
          return BigInteger.ZERO;
        }
    }

}
