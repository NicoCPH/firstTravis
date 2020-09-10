package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.MovieDTO;

//Make sure NOT to have any references to your Entity Classes here
//import entities.Movie;

import facades.MovieFacade;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import utils.EMF_Creator;

//Todo Remove or change relevant parts before ACTUAL use
@Path("movie")
public class MovieResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
               
    private static final MovieFacade FACADE =  MovieFacade.getMovieFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getMovieCount() {
        long count = FACADE.getMovieCount();
        return "{\"count\":"+count+"}";  //Done manually so no need for a DTO
    }
    

    @Path("/all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAll() {
        return Response.ok().entity(GSON.toJson(FACADE.getAllMovies())).build();
    }

    
    
     @Path("/MoviesByActor/{actor}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
     public Response getByActor(@PathParam("actor") String actor) {
        return Response.ok().entity(GSON.toJson(FACADE.getMoviesByActors(actor))).build();
    }
    
    @Path("/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getById(@PathParam("id") Long id) {
        return Response.ok().entity(GSON.toJson(FACADE.getMovieById(id))).build();
    }

    @Path("title/{title}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getByTitle(@PathParam("title") String title) {
        return Response.ok().entity(GSON.toJson(FACADE.getMoviesByTitle(title))).build();
    }
    

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public String create(MovieDTO movie) {
      throw new UnsupportedOperationException();
    }
    
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    public void update(MovieDTO entity, @PathParam("id") int id) {
        throw new UnsupportedOperationException();
    }
}
