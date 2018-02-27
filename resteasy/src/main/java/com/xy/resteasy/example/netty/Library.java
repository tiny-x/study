package com.xy.resteasy.example.netty;

import javax.ws.rs.*;

@Path("/library")
@Produces({"application/json; charset=utf-8"})
public class Library {

    @GET
    @Path("/books")
    public String getBooks() {
        return "java";
    }

    @GET
    @Path("/book/{isbn}")
    public String getBook(@PathParam("isbn") String id) {
        return "123";
    }

    @PUT
    @Path("/book/{isbn}")
    public void addBook(@PathParam("isbn") String id, @QueryParam("name") String name) {

    }

    @DELETE
    @Path("/book/{id}")
    public void removeBook(@PathParam("id") String id) {

    }

}