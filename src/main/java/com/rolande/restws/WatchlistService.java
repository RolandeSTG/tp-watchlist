package com.rolande.restws;

import java.util.List;

import com.rolande.restws.model.Security;

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
 * @author Rolande St-Gelais
 */

//@Consumes("application/xml, application/json")       // {RSTG}:  We'll support only JSON for now, to keep things simple.
//@Produces("application/xml, application/json")
@Consumes("application/json")
@Produces("application/json")

@Path("/watchlist-service")

public interface WatchlistService {
	
	// Get list of securities in watchlist
	@Path("/watchlist")
	@GET
	List<Security> getWatchlist();

	// Get a specified security by ID
	@Path("/watchlist/{id}")
	@GET
	Security getSecurityById(@PathParam("id") Long id);

	// Get a specified security by symbol
	@Path("/symbol/{symbol}")
	@GET
	Security getSecurityBySymbol(@PathParam("symbol") String symbol);
	
	// Add a security to watchlist
	@Path("/watchlist")
	@POST
	Response addSecurity(Security security);

	// Update a security info
	@Path("/watchlist")
	@PUT
	Response updateSecurity(Security security);

	// Delete security by ID
	@Path("/watchlist/{id}")
	@DELETE
	Response deleteSecurityById(@PathParam("id") Long id);

	// Delete security by symbol
	@Path("/symbol/{symbol}")
	@DELETE
	Response deleteSecurityBySymbol(@PathParam("symbol") String symbol);

}
