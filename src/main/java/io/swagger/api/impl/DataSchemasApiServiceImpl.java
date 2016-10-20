package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.model.*;

import io.swagger.model.DataSchema;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.peir.pirk.DSController;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-10-20T11:57:54.781Z")
public class DataSchemasApiServiceImpl extends DataSchemasApiService {
    @Override
    public Response dataSchemasGet(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(DSController.dataSchemasGet()).build();
    }
    @Override
    public Response dataSchemasIdGet(String id, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(DSController.dataSchemasIdGet(id)).build();
    }
}
