package com.peir.pirk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pirk.encryption.Paillier;
import org.apache.pirk.schema.data.DataSchemaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.model.DataSchema;
import io.swagger.model.DataSchemaElements;
import io.swagger.model.Query;
import io.swagger.model.QuerySchema;

class RestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestHandler.class);

    private static Map<String, Query> store = new HashMap<>();

    private static Query q = new Query();
    private static QuerySchema qs = new QuerySchema();

    static {
        qs.setName("query schema name");

        q.setId("42");
        q.setQuerySchema(qs);

        store.put("1", q);
    }

    public RestHandler() {
        // Default
    }

    // Stores a query defined by the user
    static Query putQuery(String id, Query queryInfo) {
        store.put(id, queryInfo);
        return queryInfo;
    }

    static Query getQuery(String id) {
        return store.get(id);
    }

    // Get all known data schema names as a sorted list.
    public List<String> getDataSchemaNames() {
        logger.debug("Getting sorted schema names");
        List<String> list = new ArrayList<>(DataSchemaRegistry.getNames());
        Collections.sort(list);
        return list;
    }

    /*
     * Gets a data schema with given id as a Swagger data schema. Returns null
     * if not found
     */
    public DataSchema getDataSchema(String id) {
        // Look up the Pirk data schema.
        org.apache.pirk.schema.data.DataSchema pirkSchema = DataSchemaRegistry.get(id);
        logger.debug("Looking up schema name {} returns {}", id, pirkSchema);
        if (pirkSchema == null) {
            return null;
        }

        DataSchema swaggerSchema = new DataSchema();
        swaggerSchema.setName(pirkSchema.getSchemaName());

        List<DataSchemaElements> elements = new ArrayList<>();
        for (String elementName : pirkSchema.getElementNames()) {
            DataSchemaElements swaggerElement = new DataSchemaElements();
            swaggerElement.setName(elementName);
            swaggerElement.setDataType(pirkSchema.getElementType(elementName));
            swaggerElement.setIsArray(pirkSchema.isArrayElement(elementName));
            elements.add(swaggerElement);
        }
        swaggerSchema.setElements(elements);

        return swaggerSchema;
    }
}
