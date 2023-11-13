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

import com.rolande.restws.model.ErrorBody;
import com.rolande.restws.model.Quote;
import com.rolande.restws.model.Security;
import com.rolande.restws.model.Watchlist;
import com.rolande.restws.configuration.Configuration;


/**
 * Class implementing the Watchlist Service interface.
 * 
 * Note: Watchlists and Securities lists are built using static data, to keep things simple (for demo purpose).
 *       Upon start, the service builds 3 watchlists and populates them with 5 securities each.
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
	
	@Autowired
	private Configuration config;

	private String quoteEndpoint;      

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
	 *  Populate the watchlists by attaching randomly 5 securities to each...
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

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");	
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			mapper.setDateFormat(df);

			return Response.ok(mapper.writer().withRootName("watchlists").writeValueAsString(watchlists.values())).build();
		} 
		catch (JsonProcessingException | IllegalArgumentException | NullPointerException e) {								
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
			return errorBodyResponse(Status.NOT_FOUND, "Watchlist (" + id + ") does not exist"); 	// HTTP 404
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
	 * Return a response that includes the error message in the body. 
	 * Error message will be prefixed with double stars and space (i.e. '** ').
	 * 
	 * @param code  Http Status code, as defined by (jakarta's) enum Response.Status
	 * @param errorMsg Error message to include in the body
	 * @return an HTTP Response with an error body
	 */
	private Response errorBodyResponse(Status code, String errorMsg) {
		
		ErrorBody errorBody = new ErrorBody("** " + errorMsg);
		
		System.out.println(".. Error = [" + errorBody.getMessage() + "]");		
		
		return Response.status(code).entity(errorBody).build();    
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
		
		// get the Quote Service's endpoint to get a quote, if not already set...
		if (this.quoteEndpoint == null) this.quoteEndpoint = getQuoteServiceEndpoint();

		Quote quote;
		
		HashMap<String, String> uriVariables = new HashMap<>();

		// http://localhost:8500/quote/TSLA
		
		uriVariables.put("symbol", symbol);
		
		try {
			ResponseEntity<Quote> responseEntity = new RestTemplate()
					.getForEntity(this.quoteEndpoint, Quote.class, uriVariables);
			
			quote = responseEntity.getBody();
			quote.setEnvironment(quote.getEnvironment() + " REST Template");
		}
		catch (RestClientException e) {
			quote = new Quote();        // set quote to empty fields if not found (404) or other error...
		}

		return quote;
	}
	
	/**
	 * Get the Quote Service's endpoint to get a quote, by obtaining the service's base URL in
	 * the application.properties file and adding proper endpoint suffix.
	 * 
	 * @return Quote's service endpoint to get a quote
	 */
	private String getQuoteServiceEndpoint() {
		String endpoint;
		String baseUrl;

		String getQuoteEndpoint = "quote/{symbol}";

		String quoteServiceBaseUrl = config.getQuoteBaseUrl();

		if (quoteServiceBaseUrl != null) {	
			// if url ends with a slash, keep it
			if (quoteServiceBaseUrl.endsWith("/")) {
				baseUrl = quoteServiceBaseUrl;
			}
			else { // otherwise, add one
				baseUrl = quoteServiceBaseUrl + "/";
			}
			
		}
		else { // quote-base-url property not defined, set default base url...
			baseUrl = "http://localhost:8500/";
		}
			
		endpoint = baseUrl + getQuoteEndpoint;
		
		System.out.println("... GetQuote Service Endpoint = " + endpoint);
		
		return endpoint;
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
			return errorBodyResponse(Status.NOT_ACCEPTABLE, "Watchlist name cannot be blank"); 				// HTTP 406
		}
		
	    // If we already have a list with the same name (ignoring case), reject 'add' request

		watchlist.setName(watchlist.getName().trim());        // normalize name before next check
		
		if (watchlistNameExists(watchlist.getName())) {	
			return errorBodyResponse(Status.CONFLICT, "Name (" + watchlist.getName() + ") already used"); 	// HTTP 409
		}

		// Else, all is fine, just add new list with the name provided (we set the other properties)...
		
		watchlist.setId(++currentWatchlistId);
		watchlist.setDateCreated(new Date());

		watchlists.put(watchlist.getId(), watchlist);

		System.out.println("... List(" + watchlist.getId() + "," + watchlist.getName() + "): Added");

		return Response.ok(watchlist).build();
	}
	
	/**
	 * Determine if a watchlist with a given name exists.
	 * 
	 * @param name Watchlist name to verify
	 * @return true if a watchlist with specified  name found; false otherwise.
	 */
	private boolean watchlistNameExists(String name) {
		
		for (Watchlist w : watchlists.values()) {				
			if (name.compareToIgnoreCase(w.getName()) == 0) {
			    return true;
			}
		}

		return false;
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

		Long id = watchlist.getId();
	
		Watchlist oldWatchlist = watchlists.get(id);
		
		if (oldWatchlist == null) {
			return errorBodyResponse(Status.NOT_FOUND, "Watchlist (" + id + ") does not exist"); 	// HTTP 404
		}	
			
		String newName = watchlist.getName();
		
		if ( (newName == null) || (newName.trim().isEmpty()) ) {
			return errorBodyResponse(Status.NOT_ACCEPTABLE, "Improper new name (" + watchlist.getName() + ") -- Not modified"); 	// HTTP 406			
		}
		
		newName = newName.trim();
		
		// {IssueFix}:
	    // If new & old names are not the same AND another list already uses the new name (ignoring case), reject 'update' request
		
		if ( (oldWatchlist.getName().compareToIgnoreCase(newName) != 0) && (watchlistNameExists(newName)) ) {	
			return errorBodyResponse(Status.CONFLICT, "Name (" + newName + ") already used -- Not modified"); 	// HTTP 409
		}

		System.out.println("... List (" + oldWatchlist.getName() + "): Renamed to (" + newName + ")");
		
		oldWatchlist.setName(newName);  			   // simply save trimmed name to existing object
		
		return Response.accepted().build(); // HTTP 202	
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
	
		if (watchlists.get(id) == null) {
			return errorBodyResponse(Status.NOT_FOUND, "Watchlist (" + id + ") does not exist"); 	// HTTP 404
		}
		
		System.out.println("... List (" + watchlists.get(id).getName() + "): Deleted (ok)");

		watchlistSecurities.remove(id);       // remove its security list
		watchlists.remove(id);                // remove list from actual watchlists
						
		return Response.ok().build();		
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
			return errorBodyResponse(Status.NOT_FOUND, "Watchlist (" + id + ") does not exist"); 	// HTTP 404
		}
		
		if ( (symbol == null) || (symbol.trim().isEmpty()) ) {
			return errorBodyResponse(Status.NOT_ACCEPTABLE, "Invalid Symbol");						// HTTP 406
		}
		
		symbol = symbol.trim();     // normalize symbol by removing all leading/trailing spaces, before doing next checks 
				
		Long secId = symbolsIdx.get(symbol.toUpperCase());            // find corresponding security using its symbol
			
		if (secId == null) {
			return errorBodyResponse(Status.NOT_FOUND, "Undefined symbol (" + symbol + ")");		// HTTP 404
		}

		Security security = securities.get(secId);                     // fetch security matching symbol
	
		List<Security> secList = watchlistSecurities.get(id);      // get watchlist's list of securities
		
		if (secList == null) {      // no securities attached to watchlist yet, create list
			System.out.println(".. Symbol added to new security list");

            secList = new ArrayList<Security>();		
            secList.add(security);
           
            watchlistSecurities.put(id, secList);       
		}
		else {
			// verify that symbol is not already in the list...
			if (secList.contains(security)) {
				return errorBodyResponse(Status.FORBIDDEN, "Symbol (" + symbol + ") already in list");	// HTTP 403
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
			return errorBodyResponse(Status.NOT_FOUND, "Watchlist (" + id + ") does not exist"); 	// HTTP 404
		}
		
		if ( (symbol == null) || (symbol.trim().isEmpty()) ) {
			return errorBodyResponse(Status.NOT_ACCEPTABLE, "Invalid Symbol");						// HTTP 406
		}
		
		symbol = symbol.trim();     // normalize symbol by removing all leading/trailing spaces, before doing next checks 
		
		Long secId = symbolsIdx.get(symbol.toUpperCase());            // find corresponding security using its symbol
			
		if (secId == null) {
			return errorBodyResponse(Status.NOT_FOUND, "Undefined symbol (" + symbol + ")");		// HTTP 404
		}

		Security security = securities.get(secId);                     // fetch security matching symbol
	
		List<Security> secList = watchlistSecurities.get(id);      // get watchlist's list of securities
		
		if ( (secList == null) || (! secList.contains(security)) ) {
			return errorBodyResponse(Status.NOT_FOUND, "Symbol (" + symbol + ") not in watchlist");	// HTTP 404
		}
		
		System.out.println(".. Symbol found in watchlist -- Deleted");
				
		secList.remove(security);
		watchlist.setNumberOfSecurities(secList.size());

		return Response.ok().build(); 		// HTTP 200
	}
	
}
