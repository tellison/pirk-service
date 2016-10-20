package com.peir.pirk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.pirk.schema.data.DataSchemaLoader;
import org.apache.pirk.schema.data.DataSchemaRegistry;
import org.apache.pirk.utils.PIRException;

class Loader {
    
    public Loader() {
    }
    
    public void loadTestData() {
        loadDataSchemas();
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
}
