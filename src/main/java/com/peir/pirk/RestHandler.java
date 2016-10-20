package com.peir.pirk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pirk.schema.data.DataSchemaRegistry;

import io.swagger.model.DataSchema;
import io.swagger.model.DataSchemaElements;
import io.swagger.model.Query;
import io.swagger.model.QuerySchema;

class RestHandler {

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
    List<String> getDataSchemaNames() {
        List<String> list = new ArrayList<>(DataSchemaRegistry.getNames());
        Collections.sort(list);
        return list;
    }

    // Convert Pirk data schema with given id to a Swagger data schema.
    public DataSchema getDataSchema(String id) {
        DataSchema swaggerSchema = new DataSchema();
        org.apache.pirk.schema.data.DataSchema pirkSchema = DataSchemaRegistry.get(id);

        System.out.println("Looking up schema name : " + id);

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
