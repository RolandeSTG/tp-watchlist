package com.rolande.restws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.rolande.restws.model.Security;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.NotFoundException;

/**
 * A class to implements our Watchlist Service interface
 * @author Rolande St-Gelais
 */
@Service
public class WatchlistServiceImpl implements WatchlistService {

	Map<Long, Security> watchlist  = new HashMap<>();
	Map<String, Long>   symbolsIdx = new HashMap<>();             // watchlist indexed by symbol, to speed up symbol lookup 
	
	long currentId = 0;

	public WatchlistServiceImpl() {

		// Initialize our watchlist (a list of securities) with a few well-known stocks...
		
		watchlist.put(++currentId, new Security(currentId, "TSLA", "Tesla, Inc", "Nasdaq", 226.87, 229.32, 228.43) );
		watchlist.put(++currentId, new Security(currentId, "AAPL", "Apple, Inc", "Nasdaq", 173.22, 172.00, 174.50) );
		watchlist.put(++currentId, new Security(currentId, "META", "Meta, Inc",  "Nasdaq", 287.56, 285.00, 286.23) );
		watchlist.put(++currentId, new Security(currentId, "BABA", "Alibaba Group Holding Ltd",  "NYSE", 90.93, 89.03, 88.07) );

		// For each security, add a <symbol, id> key-value pair to the symbols index...

		for (Long id : watchlist.keySet()) {			
			symbolsIdx.put(watchlist.get(id).getSymbol(), id);
		}
				
	}

	@Override
	public List<Security> getWatchlist() {
		Collection<Security> results = watchlist.values();

		List<Security> response = new ArrayList<>(results);

		return response;
	}

	@Override
	public Security getSecurityById(Long id) {

		if (watchlist.get(id) == null) {
			throw new NotFoundException(); 					// jakarta.ws.rs: HTTP 404 error
		}

		return watchlist.get(id);
	}

	@Override
	public Security getSecurityBySymbol(String symbol) {
		
//		Security security = findSecurityBySymbol(symbol);          

		Long securityId = symbolsIdx.get(symbol.toUpperCase());      // symbols stored in uppercase...
				
		if (securityId == null) {
			String errorMsg = "Symbol NOT found in watchlist";
			
			throw new NotFoundException("(" + symbol + "): " + errorMsg);         	// from jakarta.ws.rs: HTTP 404 error		
		}
	
		Security security = watchlist.get(securityId);

		return security;
	}
	
	// {RSTG}: Consider having an index on symbols, to speed up access --> Done
	// Update: Not used anymore, performance could (in theory) be dreadful in real-life 
	//         (i.e. Big O of n, where n could be quite a large number)
	
	private Security findSecurityBySymbol(String symbol) {
		Security foundSecurity = null;
		
		for (Security s : watchlist.values()) {
	    
		    if (symbol.compareToIgnoreCase(s.getSymbol()) == 0) {
		    	foundSecurity = s;
		    	break;
		    }
		}
		    
		return foundSecurity;
	}
	
	@Override
	public Response addSecurity(Security security) {
		
	//	if (findSecurityBySymbol(security.getSymbol()) != null) {
		
		String lookupSymbol = security.getSymbol().toUpperCase();
		
		if (lookupSymbol.isBlank() || symbolsIdx.get(lookupSymbol) != null) {
		   // Symbol already exists, reject add request
			
			return Response.status(Status.FORBIDDEN).build();              // HTTP 403
			// return Response.status(Status.CONFLICT).build();            // HTTP 409
		}
			
		security.setId(++currentId);
		security.setSymbol(lookupSymbol);          				    // normalize symbol to uppercase
		
		watchlist.put(security.getId(), security);                  // add <id, security> to watchlist
		symbolsIdx.put(security.getSymbol(), security.getId());		// add <symbol, id> to symbol list

		return Response.ok(security).build();
	}

	@Override
	public Response updateSecurity(Security security) {
		Security currentSecurity = watchlist.get(security.getId());
		Response response;
		
		if (currentSecurity != null) {
			// Allow to change everything except the security's symbol...
			
			if (currentSecurity.getSymbol().compareToIgnoreCase(security.getSymbol()) == 0) {
				watchlist.put(security.getId(), security);
				response = Response.accepted().build();                 // HTTP 202 
			}
			else {
				response = Response.notModified().build();            	// HTTP 304
			}
		}
		else {
			response = Response.status(Status.NOT_FOUND).build();   	// HTTP 404
		}
		
		return response;
	}

	@Override
	public Response deleteSecurityById(Long id) {
		Security currentSecurity = watchlist.get(id);
		
		return removeSecurity(currentSecurity);
	}
	
	@Override
	public Response deleteSecurityBySymbol(String symbol) {
//		Security currentSecurity = findSecurityBySymbol(symbol);
			
		Security currentSecurity =  watchlist.get(symbolsIdx.get(symbol.toUpperCase()));
		
		return removeSecurity(currentSecurity);
	}

	private Response removeSecurity(Security security) {
		Response response;
				
		if (security != null) {
			watchlist.remove(security.getId());				// remove from actual watchlist
			symbolsIdx.remove(security.getSymbol());        // remove also from symbol index
			
			response = Response.ok().build();
		}
		else {
			response = Response.status(Status.NOT_FOUND).build();   	// HTTP 404
		}
				
		return response;
	}


}
