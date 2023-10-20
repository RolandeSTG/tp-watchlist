package com.rolande.restws;

import com.rolande.restws.model.Watchlist;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.core.Response;

/**
 * Defines the Watchlist Service interface, along with the API's endpoints.
 * 
 * @author Rolande St-Gelais
 */

@Consumes("application/json")
@Produces("application/json")

@Path("/watchlist-service")

public interface WatchlistService {
	
	//-------------------------------------------
	// CRUD operations on watchlists
	//-------------------------------------------
	
	// Get list of existing watchlists
	@Path("/watchlists")
	@GET
	Response getWatchlists();

	// Add a watchlist 
	@Path("/watchlists")
	@POST
	Response addWatchlist(Watchlist watchlist);

	// Update a watchlist
	@Path("/watchlists")
	@PUT
	Response updateWatchlist(Watchlist watchlist);

	// Delete a watchlist
	@Path("/watchlists/{id}")
	@DELETE
	Response deleteWatchlist(@PathParam("id") Long id);

	//-------------------------------------------
	// Operations on a specific watchlist
	//-------------------------------------------
	
	// Get content of a watchlist
	
	@Path("/watchlists/{id}")
	@GET
	Response getWatchlist(@PathParam("id") Long id);
	
	// Add symbol to a watchlist
	@Path("/watchlists/{id}/symbol/{symbol}")
	@POST
	Response addSecurity(@PathParam("id") Long id, @PathParam("symbol") String symbol);
	
	// Delete symbol from a watchlist
	@Path("/watchlists/{id}/symbol/{symbol}")
	@DELETE
	Response deleteSecurity(@PathParam("id") Long id, @PathParam("symbol") String symbol);

}
