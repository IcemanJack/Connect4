Important
=========
Ubuntu 12.04 LTS
----------------
LipeRMI won't work on Java 7 downgrade to Java 6

In order to make the database work
----------------------------------
1. Install PostgreSQL, instructions are in: /doc/PostGreSQL
2. Create table "usr" with columns
3. "name" of "text" type with Unique Contraint on it.
4. "score" of "int" type
	
Problems & Solutions
====================
Client disconnecting
---------------------
1. Client can't call unregister method in the server in all disconnecting scenarios.
2. Client will freeze if he waits for unregister methon to complete in server and kills himself.

Solution
--------
Handle all possible client disconnects in server.

1. Store new connecting sockets in newClientID in String format "/ip:port, name".
2. On registerPlayer or registerSpectator, get newClientID that was renewed by his socket and put in users in String formats <"newClientId", "clientName">.
3. On socket disconnect get client by clientID from users and call unregister method in server with the client name.

