package com.zzzkvidi4.server.api;

import com.zzzkvidi4.dal.tables.daos.ProductDao;
import com.zzzkvidi4.dal.tables.pojos.Product;
import com.zzzkvidi4.server.NamedRequest;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.tools.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Path("/product")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class ProductAPI {
    @NotNull
    private final ProductDao productDao;

    @GET
    @NotNull
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> get() {
        return productDao.findAll();
    }

    @POST
    @NotNull
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@NotNull Product product) {
        productDao.insert(product);
        return Response.ok().build();
    }

    @POST
    @NotNull
    @Path("/name")
    public Response getByName(@NotNull NamedRequest namedRequest) {
        String name = namedRequest.getName();
        if (StringUtils.isBlank(name)) {
            return Response.status(HttpStatus.NOT_FOUND_404).build();
        }
        List<Product> products = productDao.fetchByName(name);
        if (products.size() == 0) {
            return Response.status(HttpStatus.NOT_FOUND_404).build();
        } else {
            return Response.ok(products).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
    }

    @GET
    @NotNull
    @Path("/organization")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getByOrganization(@Nullable @QueryParam("name") String name) {
        if (StringUtils.isBlank(name)) {
            return Collections.emptyList();
        }
        return productDao.fetchByOrganization(name);
    }
}
