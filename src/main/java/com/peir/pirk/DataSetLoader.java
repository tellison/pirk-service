package com.peir.pirk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pirk.schema.data.DataSchemaLoader;
import org.apache.pirk.schema.data.DataSchemaRegistry;
import org.apache.pirk.utils.PIRException;

/*
 * Sample data for debugging and testing.
 */
class DataSetLoader {

    public DataSetLoader() {
        loadDataSchemas();
    }

    // Must return a copy, not the original.
    public List<String> getDataSetNames() {
        // Creates new each time
        return new ArrayList<String>(Arrays.asList("example"));
    }

    private void loadDataSchemas() {
        // Simple data schema #1
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> ");
        sb.append("<schema xmlns=\"http://pirk.apache.org\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xsi:schemaLocation=\"http://pirk.apache.org data-schema.xsd\"> ");
        sb.append("<schemaName>Simple Data Schema 1</schemaName> ");
        sb.append("<element> <name>name</name> <type>string</type> </element> ");
        sb.append("<element> <name>age</name>  <type>int</type>    </element> ");
        sb.append("<element> <name>children</name>  <type>string</type>  <isArray/>  </element> ");
        sb.append("</schema> ");
        String xml = sb.toString();
        InputStream stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));

        try {
            DataSchemaRegistry.put(new DataSchemaLoader().loadSchema(stream));
        } catch (IOException | PIRException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void simpleDataSet() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"name\": \"Alice\", \"age\": \"31\", \"children\": [ \"Zack\", \"Yvette\" ] }\n");
        sb.append("{ \"name\": \"Bob\",   \"age\": \"25\", \"children\": [ \"Xavier\", \"Wendy\" ] }\n");
        sb.append("{ \"name\": \"Chris\", \"age\": \"43\", \"children\": [ \"Donna\" ] }\n");
        sb.append("{ \"name\": \"Donna\", \"age\": \"19\", \"children\": [ ] }");
        String json = sb.toString();
    }
}
