Connect4
========
Basics
------
* This is a distributed Connect4 game.
* For a more accurate description of the game, please visit http://en.wikipedia.org/wiki/Connect_Four
* It's build under the MVC design pattern.
* The server contains the game database and defines the rules.
* The clients connects to the server in order to play or watch a game.

Game constraints
----------------
1. 1vs1 game against a player or the computer.
2. All other players will be asked to become observers of the game or to leave.
3. If there is no other player you will be asked to wait.
4. If you are playing a game and you Quit/Exit the game, you lose.
5. The server can only handle 1 game at a time.
6. You have to stop the server yourself, so you can consult critical output if needed.
7. You have to restart the server to start a new game.

Important
=========
Ubuntu 12.04 LTS
----------------
<table>
  <tr>
    <th>Java</th><th>Works</th>
  </tr>
  <tr>
    <td>6-openjdk</td><td>Yes</td>
  </tr>
  <tr>
    <td>6-openjdk-amd64</td><td>No</td>
  </tr>
  <tr>
    <td>7-openjdk-amd64</td><td>No</td>
  </tr>
  <tr>
    <td>7-oracle</td><td>Yes</td>
  </tr>
</table>

In order to make the database work
----------------------------------
1. Install PostgreSQL, instructions are in: /doc/PostGreSQL
2. Change "postgres" user password to "mypassword".
3. Create database "postgres" with "postgres" user.
4. Create table "usr".
5. Insert columns "name" of "text" type with Unique Contraint on it and "score" of "int" type.
*Note: If you want to change this default database settings, change them in this two classes: "Database.java" & "MockDatabase.java" in "connect4/server/database/".

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

Author: Seva Ivanov
https://github.com/IcemanJack/Connect4

