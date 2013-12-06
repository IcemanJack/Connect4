Important
=========
Ubuntu 12.04
	LipeRMI won't work on Java7 downgrade to Java6

In order to make the database work you need to:
1. Install PostgreSQL, instructions are in: /doc/PostGreSQL
2. Create table "usr" with columns
3. "name" of "text" type with Unique Contraint on it.
4. "score" of "int" type
	
Disconnecting the clients technique
====================================
Question: Why client wont unregister himself?
---------
It will make lag in RMI & if he disconnects by any means, the server wont be notified

Solution: Handle all possible client disconnects in server.
---------
1. Store newClientID "/ip:port, name" in TreeMap on socket connect
2. On registerPlayer or Spectator, get newClientID and put in 		users<newClientId, clientName>
3. On socket disconnect unregister it from model by name get client by clientID from users.

