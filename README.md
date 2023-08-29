# watchlist
Simple Stock Watchlist REST API using Spring Boot.  

### Infos:
- Project built with Spring Tools Suite
- Client used to test: Postman
- Content-type supported: application/json only (for simplicity)
- API-Key: None required

### API endpoints:
GET:
- /demos/api/watchlist-service/watchlist --> to get list of all securities in the watchlist
- /demos/api/watchlist-service/watchlist/{id} --> to get a specific security using its id, where id is a security integer number
- /demos/api/watchlist-service/symbol/{symbol} --> to get a watchlist item by its symbol

POST:  
 - /demos/api/watchlist-service/watchlist --> to add a security to the watchlist; body must contain instance of the security to add, in json

PUT: 
 - /demos/api/watchlist-service/watchlist --> to update definition of a security; body must contain instance of the security to modify with new value(s), in json

DELETE:  
 - /demos/api/watchlist-service/watchlist/{id} --> delete security with {id} from the watchlist
 - /demos/api/watchlist-service/symbol/{symbol} --> delete security with {symbol} from the watchlist

   
