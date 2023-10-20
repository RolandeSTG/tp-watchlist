# watchlist
Simple Stock Watchlist REST API using Spring Boot.  

### Infos:
- Project built with Spring Tools Suite
- Client used to test: Postman
- Content-type supported: application/json only (for simplicity)
- API-Key: None required

### API endpoints:
GET:
- /watchlist-service/watchlists --> to get the list of all existing watchlists
- /watchlist-service/watchlists/{id} --> to get the content of a specific watchlist, i.e. the list of all its securities

POST:  
 - /watchlist-service/watchlists --> to add a new watchlist; body must contain instance of the watchlist to add, in json
 - /watchlist-service/watchlists/{id}/symbol/{symbol} --> to add a symbol' security to the specified watchlist

PUT: 
 - /watchlist-service/watchlists --> to update a watchlist; body must contain instance of the watchlist to modify with its new value(s), in json

DELETE:  
 - /watchlist-service/watchlist/{id} --> to delete a specific watchlist
 - /watchlist-service/watchlist/{id}/symbol/{symbol} --> to delete a symbol' security from the specified watchlist
 
   
