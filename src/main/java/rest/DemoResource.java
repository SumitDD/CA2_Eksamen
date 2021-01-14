package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entitiesDTO.NewUserDTO;
import entitiesDTO.UserDTO;
import entitiesDTO.UsersDTO;
import exceptions.UserNotFoundException;
import facades.UserFacade;
import fetcher.CatFactFetcher;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.SetupTestUsers;

/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
public class DemoResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final ExecutorService ES = Executors.newCachedThreadPool();
    private static final UserFacade FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static String cachedResponse;
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

   
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("personphone/{phone}")
    public String getUserByPhone(@PathParam("phone")String phone) throws UserNotFoundException {
        UserDTO user = FACADE.getUserByPhone(phone);
        return GSON.toJson(user);
        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("personhobby/{hobby}")
    public String getAllUsersByHobby(@PathParam("hobby")String hobby) throws UserNotFoundException {
        UsersDTO users = FACADE.getAllUsersByHobby(hobby);
        return GSON.toJson(users);
        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("personcity/{city}")
    public String getAllUsersByCity(@PathParam("city")String city) throws UserNotFoundException {
        UsersDTO users = FACADE.getAllUsersByCity(city);
        return GSON.toJson(users);
        
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{hobby}")
    public String getUserCountByhobby(@PathParam("hobby")String hobby) throws UserNotFoundException {
        long count = FACADE.getUserCountByhobby(hobby);
        return GSON.toJson(count);
        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("allzip")
    public String getAllZipCodes() {
        List<String> zipCodes = FACADE.getAllZipCodes();
        return GSON.toJson(zipCodes);
        
    }
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("delete")
    public Response deletePerson(String user) throws UserNotFoundException {
        UserDTO userDTO = GSON.fromJson(user, UserDTO.class); 
        userDTO = FACADE.deleteUser(userDTO);
        return Response.ok(userDTO).build();
        
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }
    
    @Path("parrallel")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getStarWarsParrallel() throws InterruptedException, ExecutionException, TimeoutException {
        String result = fetcher.StarWarsFetcher.responseFromExternalServersParrallel(ES, GSON);
        cachedResponse = result;
        return result;
    }

    @Path("cached")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getStarWarsCached() throws InterruptedException, ExecutionException, TimeoutException {
        return cachedResponse;
    }
    
    @Path("setUpUsers")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public void setUpUsers() {
        SetupTestUsers.setUpUsers();
    }
    
    @Path("planets")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPlanets() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        String result = fetcher.StarWarsPlanetFetcher.responseFromExternalServersSequential(ES, GSON);
        cachedResponse = result;
        return result;
    }
    
    @Path("countries")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getCountries() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        String result = fetcher.CountriesFetcher.responseFromExternalServersSequential(ES, GSON);
        cachedResponse = result;
        return result;
    }
    @Path("cats")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getCatFacts() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        String result = CatFactFetcher.fetchCatFactParrallel(ES, GSON);
        cachedResponse = result;
        return result;
    }
    
    @Path("allusers")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllUsers()  {
        UsersDTO users = FACADE.getAllUsers();
        return GSON.toJson(users);
        
    }
    @Path("add")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String addNewuser(String user) throws UserNotFoundException  {
        NewUserDTO nuDTO = GSON.fromJson(user, NewUserDTO.class);
        UserDTO userDTO = FACADE.addNewUser(nuDTO);
        return GSON.toJson(userDTO);
        
    }
    
    
    
    
}