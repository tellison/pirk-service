package com.peir.pirk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pirk.schema.data.DataSchemaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.model.DataSchema;
import io.swagger.model.DataSchemaElements;

/*
 * Assumes all URL encoding/decoding has been done on inputs, and will be done on return values.
 * Throws exceptions to indicate errors, and lets caller map to response.
 */
class DataSetOperations {

    private static final Logger logger = LoggerFactory.getLogger(DataSetOperations.class);

    private final DataSetLoader loader;
    
    public DataSetOperations(DataSetLoader loader) {
        this.loader = loader;
    }

    // Get all known data set names as a sorted list.
    public List<String> getDataSetNames() {
        logger.debug("Getting sorted data set names");
        List<String> list = loader.getDataSetNames();
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
