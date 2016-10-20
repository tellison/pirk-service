package com.peir.pirk;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import io.swagger.model.DataSchema;

/* A controller for the API impl */
public class DSController {

   static RestHandler handler = new RestHandler();
   
   static {
       new Loader().loadTestData();
   }

   // GET /v1/data_schemas
    public static List<String> dataSchemasGet() {
        return handler.getDataSchemaNames();
    }
    
    // GET /v1/data_schemas/{id}
    public static DataSchema dataSchemasIdGet(String id) {
        String schemaName = null;
        try {
            schemaName = URLDecoder.decode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 is a required encoding type
        }
        System.out.println("Encoded name : " + id);
        System.out.println("Decoded name : " + schemaName);
        return handler.getDataSchema(schemaName);
    }
}
