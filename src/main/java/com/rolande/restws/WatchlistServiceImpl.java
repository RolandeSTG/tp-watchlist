package com.rolande.restws;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.InternalServerErrorException;

import com.rolande.restws.model.Quote;
import com.rolande.restws.model.Security;
import com.rolande.restws.model.Watchlist;


/**
 * Class implementing the Watchlist Service interface.
 * 
 * Note: Watchlists and Securities lists are built using static data, to keep things simple (for demo purpose).
 * 
 * @author Rolande St-Gelais
 */
@Service
public class WatchlistServiceImpl implements WatchlistService {

	Map<Long, Watchlist> watchlists = new HashMap<>();
	Map<Long, Security> securities  = new HashMap<>();
	Map<String, Long> symbolsIdx    = new HashMap<>();                   // securities indexed by symbol, to speed up symbol lookup
	Map<Long, List<Security>> watchlistSecurities = new HashMap<>();     // K=Watchlist ID, V= List<Security>

	long currentWatchlistId = 0;        // watchlists current Id
	long currentSecurityId  = 0;	    // securities current Id

	@Autowired
	private Environment environment;

	public WatchlistServiceImpl() {
		
		loadSecurities();	                      	// Create a list of securities to later add to watchlists		
		createWatchlists(); 						// Create a couple of watchlists
		populateWatchlists(); 						// Add a few securities to each watchlist

	}
	
	/**
	 * Initialize our securities list. Security Ids starts at 1.
	 * 
	 */

	private void loadSecurities() {
	
		securities.put(++currentSecurityId, new Security(currentSecurityId, "AAPL", "Apple Inc", "Nasdaq", 
				new Quote(currentSecurityId, "AAPL", 175.0, 176.0, new Timestamp(System.currentTimeMillis()), 177.5, 100, 177.25, 100, 177.55, 200, "")));
		securities.put(++currentSecurityId, new Security(currentSecurityId, "AMZN", "Amazon.com, Inc.", "Nasdaq",
				new Quote(currentSecurityId, "AMZN", 132.0,	133.5, new Timestamp(System.currentTimeMillis()), 129.5, 400, 129.41, 100, 129.50, 200, "")));		
		securities.put(++currentSecurityId, new Security(currentSecurityId, "BABA", "Alibaba Group Holding Ltd", "NYSE", 
				new Quote(currentSecurityId, "BABA", 90.0,	91.0, new Timestamp(System.currentTimeMillis()), 92.5, 100, 92.40, 300, 92.50, 200, "")));
		securities.put(++currentSecurityId, new Security(currentSecurityId, "GOOG", "Alphabet Inc.", "Nasdaq", 
				new Quote(currentSecurityId, "GOOG", 137.0, 138.0, new Timestamp(System.currentTimeMillis()), 139.5, 100, 139.40, 100, 139.50, 200, "")));
		securities.put(++currentSecurityId, new Security(currentSecurityId, "META", "Meta, Inc", "Nasdaq", 
				new Quote(currentSecurityId, "META", 303.0, 304.0, new Timestamp(System.currentTimeMillis()), 305.5, 100, 305.40, 100, 305.50, 200, "")));
	    securities.put(++currentSecurityId, new Security(currentSecurityId, "MSFT", "Microsoft Corp", "Nasdaq", 
	    		new Quote(currentSecurityId, "MSFT", 317.0, 315.43, new Timestamp(System.currentTimeMillis()), 312.1, 200, 312.05, 800, 312.11, 200, "")));
	    securities.put(++currentSecurityId, new Security(10L, "NFLX", "Netflix Inc", "Nasdaq",
                new Quote(currentSecurityId, "NFLX",  384.0, 382.22, new Timestamp(System.currentTimeMillis()), 378.95, 200, 378.81, 100, 378.95, 400, "")));
	    securities.put(++currentSecurityId, new Security(currentSecurityId, "NVDA", "NVIDIA Corporation", "Nasdaq",
	            new Quote(currentSecurityId, "NVDA", 417.0, 420.75, new Timestamp(System.currentTimeMillis()), 423.52, 100, 423.49, 200, 423.51, 100, "")));
	    securities.put(++currentSecurityId, new Security(currentSecurityId, "SHOP", "Shopify Inc", "TSE", 
	    		new Quote(currentSecurityId, "SHOP", 53.0, 53.43, new Timestamp(System.currentTimeMillis()), 54.1, 100, 54.08, 400, 54.14, 100, "")));
		securities.put(++currentSecurityId, new Security(currentSecurityId, "TSLA", "Tesla Inc", "Nasdaq", 
				new Quote(currentSecurityId, "TSLA", 262.0, 265.0, new Timestamp(System.currentTimeMillis()), 266.45, 100, 266.40, 300, 266.50, 200, "")));
		securities.put(++currentSecurityId, new Security(currentSecurityId, "ZM", "Zoom Video Communications Inc", "Nasdaq", 
				new Quote(currentSecurityId, "ZM", 64.0, 64.34, new Timestamp(System.currentTimeMillis()), 65.1, 100, 65.05, 300, 65.2, 200, "")));
 
		// For each security, add a <symbol, id> key-value pair to the symbols index...

		for (Long id : securities.keySet()) {
			symbolsIdx.put(securities.get(id).getSymbol(), id);
		}

		return;
	}

	/**
	 *  Create a few watchlists, each with different creation dates (to make it a little more interesting)...
	 *  Watchlist IDs start at 1.
	 */
	
	private void createWatchlists() {

		Calendar cal = Calendar.getInstance();
		
		for (int i = 1; i < 4; i++) {			
	 		 int randomDays = (int) (Math.random() * 30);    // vary creation date randomly within last 30 days (from 0-29 days)
	 		 cal.setTime(new Date());
			 cal.add(Calendar.DATE, (randomDays * -1));
		 		
			 watchlists.put(++currentWatchlistId, new Watchlist(currentWatchlistId, "List " + currentWatchlistId, cal.getTime(), 0));
		}
		
		return;
	}
		
	/**
	 *  Attach randomly a given number of securities to each watchlist...
	 */
	private void populateWatchlists() {
		
		int max = securities.size();
		int min = 1;
		int range = max - min + 1;         // +1 = include max

		for (Long id : watchlists.keySet()) {
			List<Security> secList = new ArrayList<Security>();

			int count = 0;			
			while (count < 5) {
				long randomNumber = min + (long) (Math.random() * range);

				if (!secList.contains(securities.get(randomNumber))) {
					secList.add(securities.get(randomNumber));
					count++;
				} 
			}

			// Show content of list...
			
			System.out.print("List (" + watchlists.get(id).getName() + ") = [ ");
			for (int i = 0; i < secList.size(); i++) {
				System.out.print(secList.get(i).getSymbol());
				String sep = ", ";
				if (i == secList.size() - 1) {
					sep = " ]\n";
				}
				System.out.print(sep);
			}

			// Associate security list to watchlist
			watchlistSecurities.put(id, secList);
			
			watchlists.get(id).setNumberOfSecurities(secList.size());		
		}
		
		System.out.println("watchlists = " + watchlists);
		
	}
	
	/**
	 * Get the list of existing watchlists and return them in the response.
	 * Note: Date objects are not serialized the same way among JSon libraries, so we define a standard one. 
	 * 
	 * @return the list of watchlists as a JSON object called 'watchlists'
	 * 
	 */
	@Override
	public Response getWatchlists() {

		System.out.println("GET: getWatchlists()");

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		mapper.setDateFormat(df);

		// we wrap the list using 'watchlists', so that response starts with a single object 
		try {
			return Response.ok(mapper.writer().withRootName("watchlists").writeValueAsString(watchlists.values())).build();
		} 
		catch (JsonProcessingException | IllegalArgumentException e) {								
			throw new InternalServerErrorException(e.getMessage());                 // HTTP 500
		}

	}

	/**
	 * Get a single watchlist, which includes getting its list of securities and their current quote.  Each quote 
	 * is provided by the Quote microservice.  
	 * 
	 * @param id The id of the watchlist
	 * @return a watchlist detail response object as the body of the HTTP response.
	 */
	@Override
	public Response getWatchlist(Long id) {
		
		System.out.println("GET: getWatchList(id=" + id + ")");

		Watchlist watchlist = watchlists.get(id);

		if (watchlist == null) {
			String errorMsg = "Watchlist does NOT exist (id=" + id + ")";

			throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMsg, null);
		}
		
		List<Security> secList = watchlistSecurities.get(watchlist.getId());

		// Get the latest quote for each security...
		try {
			for (Security s : secList) {
				s.setQuote(fetchRemoteQuote(s.getSymbol()));
			}
		} 
		catch (NullPointerException e) {
			System.out.println(">> Watchlist (" + watchlist.getName() + "): No securities attached to it");
		}

		WatchlistDetailResponse response = new WatchlistDetailResponse();

		response.setList(watchlist);
		response.setSecurities(secList);

		String port = environment.getProperty("local.server.port");
		response.setEnvironment(port);

		return Response.ok(response).build();
	}
	
	/**
	 * Fetch the latest quote for a given security's symbol from the Quote microservice.  If not running at the time of request, 
	 * the quote returned will be empty. 
	 * 
	 * @param symbol The symbol of the security to inquire about
	 * @return the latest quote data, or an empty quote object if symbol not found or service not running. 
	 * 
	 * Note: For the time being, the Quote Microservice runs on port 8500. 
	 */

	private Quote fetchRemoteQuote(String symbol) {
		Quote quote;
		
		HashMap<String, String> uriVariables = new HashMap<>();

		// http://localhost:8500/quote/TSLA

		uriVariables.put("symbol", symbol);

		try {
			ResponseEntity<Quote> responseEntity = new RestTemplate()
					.getForEntity("http://localhost:8500/quote/{symbol}", Quote.class, uriVariables);

			quote = responseEntity.getBody();
			quote.setEnvironment(quote.getEnvironment() + " REST Template");
		}
		catch (RestClientException e) {
			quote = new Quote();        // set quote to empty fields if not found (404) or other error...
		}

		return quote;
	}
	
	/**
	 * Add a watchlist to the list of existing watchlists.
	 * 
	 * @param watchlist The watchlist to be added; Only the name is relevant (at this time), so all other properties are 
	 *                  ignored for creation. Name of a watchlist is non-case sensitive and must be unique.
	 * @return the created watchlist object and its assigned id in the body of the response, if successful; otherwise
	 *         the corresponding HTTP error code.                
	 */
	@Override
	public Response addWatchlist(Watchlist watchlist) {

		System.out.println("POST: addWatchlist("+ watchlist + ")");
	
		if ( (watchlist.getName() == null) || (watchlist.getName().trim().isEmpty()) ) {
			return Response.status(Status.NOT_ACCEPTABLE).build();              // HTTP 406
		}
		
	    // If we already have a list with the same name (ignoring case), reject 'add' request

		for (Watchlist w : watchlists.values()) {				
			if (watchlist.getName().compareToIgnoreCase(w.getName()) == 0) {
			    return Response.status(Status.CONFLICT).build();            // HTTP 409
			}
		}

		// Else, all is fine, just add new list with the name provided (we set the other properties)...
		
		watchlist.setId(++currentWatchlistId);
		watchlist.setDateCreated(new Date());

		watchlists.put(watchlist.getId(), watchlist);

		return Response.ok(watchlist).build();
	}
	
	/** 
	 * Update an existing watchlist with the given replacement object.  Only the name may be modified (at this time).
	 * 
	 * @param watchlist Watchlist object to be modified
	 * @return HTTP response code, whether the request was accepted or rejected
	 */

	@Override
	public Response updateWatchlist(Watchlist watchlist) {

		System.out.println("PUT: updateWatchlist(" + watchlist + ")");

		Response response;
	
		Long id = watchlist.getId();
	
		Watchlist oldWatchlist = watchlists.get(id);
		
		if (oldWatchlist != null) {
			
			String newName = watchlist.getName();
			
			if ( (newName != null) && (!newName.trim().isEmpty()) ) {
				watchlists.put(id, watchlist);                // replace old list with this one
				
				response = Response.accepted().build(); // HTTP 202
				
				System.out.println("...List (" + oldWatchlist.getName() + "): Renamed to (" + watchlist.getName() + ")");
			}
			else {
				System.out.println("..." +  "Not modified: Improper name (" + watchlist.getName() + ")");
			
				response = Response.notModified().build(); // HTTP 304		
			}		
		} 
		else {
			System.out.println("... (not found)");
			
			response = Response.status(Status.NOT_FOUND).build(); // HTTP 404
		}
		
		return response;
		
	}

	/**
	 * Delete a given watchlist.
	 * 
	 * @param id Id of the watchlist to delete.
	 * @return HTTP response code, corresponding to whether the request succeeded or not.
	 * 
	 */

	@Override
	public Response deleteWatchlist(Long id) {

		System.out.println("DELETE: deleteWatchlist(id=" + id + ")");

		Response response;
	
		if (watchlists.get(id) != null) {
			System.out.println("..." + watchlists.get(id).getName() + ": Deleted (ok)");

			watchlistSecurities.remove(id);       // remove its security list
			watchlists.remove(id);                // remove list from actual watchlists
							
			response = Response.ok().build();
		} else {	
			System.out.println("... (not found)");
			
			response = Response.status(Status.NOT_FOUND).build(); // HTTP 404
		}

		return response;
		
	}
	
	/**
	 * Add a symbol's security to the specified watchlist.
	 * 
	 * @param id Id of the watchlist involved
	 * @param symbol Symbol of the security sought.
	 * @return the security object of the symbol, if valid and found; HTTP failure response code otherwise.
	 */

	@Override
	public Response addSecurity(Long id, String symbol) {

		System.out.println("POST: addSecurity(watchlistID=" + id + ", symbol=" + symbol + ")");

		Watchlist watchlist = watchlists.get(id);
		
		if (watchlist == null) {
			System.out.println("** Watchlist NOT found");
		
			return Response.status(Status.NOT_FOUND.getStatusCode(), "Watchlist (" + id + ") NOT found").build();          // 404 - Watchlist not found
		}
		
		if ( (symbol == null) || (symbol.trim().isEmpty()) ) {
			System.out.println("** Invalid Symbol");
			return Response.status(Status.NOT_ACCEPTABLE.getStatusCode(), "Invalid symbol").build();     // 406 - Unacceptable symbol
		}
		
		symbol = symbol.trim();     // normalize symbol by removing all leading/trailing spaces, before doing next checks 
				
		Long secId = symbolsIdx.get(symbol.toUpperCase());            // find corresponding security using its symbol
			
		if (secId == null) {
			System.out.println("** Symbol undefined");
			return Response.status(Status.NOT_FOUND.getStatusCode(), "Symbol undefined").build();          // 404 - Symbol not associated with any security
		}

		Security security = securities.get(secId);                     // fetch security matching symbol
	
		List<Security> secList = watchlistSecurities.get(id);      // get watchlist's list of securities
		
		if (secList == null) {      // no securities attached yet to watchlist, create list
			System.out.println(".. Symbol added to new security list");

            secList = new ArrayList<Security>();		
            secList.add(security);
           
            watchlistSecurities.put(id, secList);       
		}
		else {
			// verify that symbol is not already in the list...
			if (secList.contains(security)) {
				System.out.println("** Symbol already in list -- Add Request rejected");
				
				return Response.status(HttpStatus.FORBIDDEN.value(), "Symbol (" + symbol + ") already in list -- Not added").build(); // HTTP 403 -- Symbol already in list, rejected
			}
			else {
				System.out.println("..Symbol added to existing security list");

				secList.add(security);
			}
				
		}
		
		security.setQuote(fetchRemoteQuote(symbol));   // Get latest quote for this security/symbol
		
		watchlist.setNumberOfSecurities(secList.size());
					
		return Response.ok(security).build();
	}

	/**
	 * Delete a symbol' security from the specified watchlist
	 * 
	 * @param id Id of the watchlist involved
	 * @param symbol Symbol of the security to remove.
	 * @return the resulting HTTP response code, whether the request was successful or not.
	 */
	
	@Override
	public Response deleteSecurity(Long id, String symbol) {
		
		System.out.println("DELETE: deleteSecurity(watchlistID=" + id + ", symbol=" + symbol + ")");

		Watchlist watchlist = watchlists.get(id);
		
		if (watchlist == null) {
			System.out.println("** Watchlist NOT found");
			return Response.status(Status.NOT_FOUND.getStatusCode(), "Watchlist (" + id + ") NOT found").build();          // 404 - Watchlist not found
		}
		
		if ( (symbol == null) || (symbol.trim().isEmpty()) ) {
			System.out.println("** Invalid Symbol");
			return Response.status(Status.NOT_ACCEPTABLE.getStatusCode(), "Invalid symbol").build();     // 406 - Unacceptable symbol
		}
		
		symbol = symbol.trim();     // normalize symbol by removing all leading/trailing spaces, before doing next checks 
		
		Long secId = symbolsIdx.get(symbol.toUpperCase());            // find corresponding security using its symbol
			
		if (secId == null) {
			System.out.println("** Symbol undefined");
			return Response.status(Status.NOT_FOUND.getStatusCode(), "Symbol NOT found").build();          // 404 - Symbol not associated with any security
		}

		Security security = securities.get(secId);                     // fetch security matching symbol
	
		List<Security> secList = watchlistSecurities.get(id);      // get watchlist's list of securities
		
		if ( (secList == null) || (! secList.contains(security)) ) {
			System.out.println("** Symbol NOT found in watchlist");
			return Response.status(Status.NOT_FOUND.getStatusCode(), "Symbol NOT found").build();  // HTTP 404
		}
		
		System.out.println(".. Symbol found in watchlist -- Deleted");
				
		secList.remove(security);
		watchlist.setNumberOfSecurities(secList.size());

		return Response.ok().build(); 		// HTTP 200
	}
	
}
