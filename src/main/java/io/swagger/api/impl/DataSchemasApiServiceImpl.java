package io.swagger.api.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.peir.pirk.DataSchemaController;

import io.swagger.api.DataSchemasApiService;
import io.swagger.api.NotFoundException;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-10-20T11:57:54.781Z")
public class DataSchemasApiServiceImpl extends DataSchemasApiService {
    @Override
    public Response dataSchemasGet(SecurityContext securityContext) throws NotFoundException {
        return DataSchemaController.dataSchemasGet();
    }

    @Override
    public Response dataSchemasIdGet(String id, SecurityContext securityContext) throws NotFoundException {
        return DataSchemaController.dataSchemasIdGet(id);
    }
}
