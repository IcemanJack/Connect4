# Distributed Connect4

*http://en.wikipedia.org/wiki/Connect_Four*

This is a distributed LipeRMI Connect4 game. It is built under the MVC design pattern. The server contains the game database and defines the rules. The clients connect to the server in order to play or watch an ongoing game.

![Preview](https://raw.githubusercontent.com/sevaivanov/connect4/master/src/img/preview.png)

## Game Constraints

* This is a 1vs1 game against a player or the computer.
* All other players will be asked to become observers of the game or to leave.
* If there is no other player, you will be asked to wait.
* If you are playing a game and you Quit/Exit the game, you lose.
* The server can only handle one game at a time.
* You have to stop the server yourself.
* You have to restart the server to start a new game.

## TODO

* Write unitests
* Refactor everything
* Change database setup
* Implement computer AI to play against

## Supported Java Versions

<table>
  <tr>
    <th>Java</th><th>Status</th>
  </tr>
  <tr>
    <td>6-openjdk</td><td>✓</td>
  </tr>
  <tr>
    <td>6-openjdk-amd64</td><td>x</td>
  </tr>
  <tr>
    <td>7-openjdk-amd64</td><td>x</td>
  </tr>
  <tr>
    <td>7-oracle</td><td>✓</td>
  </tr>
</table>

## Database Setup

1. Set *postgres* default user password to *mypassword*.
2. Create database *postgres* with *postgres* user.
3. Create table *usr* with the columns *name* of text type set as primary key and *score* of integer type.

For Ubuntu and Windows installation see */doc/PostGreSQL/*.

### Debian

    sudo apt-get install postgresql postgresql-client
    sudo su – postgres
    
    createdb postgres
    psql -d postgres
    
    ALTER USER postgres PASSWORD 'mypassword';
    CREATE TABLE usr (name varchar(50) CONSTRAINT firstkey PRIMARY KEY,
        score integer
    );

# Encountered Issues

## Disconnecting Client

1. Client can't call unregister method in the server in all disconnecting scenarios.
2. Client will freeze if he waits for unregister method to complete in server and kills himself.

* Solution

Handle all possible client disconnects in server.

1. Store new connecting sockets in newClientID in String format "/ip:port, name".
2. On registerPlayer or registerSpectator, get newClientID that was renewed by his socket and put in users in String formats <"newClientId", "clientName">.
3. On socket disconnect get client by clientID from users and call unregister method in server with the client name.

*Authors: Vsevolod Ivanov, Kevin Bonnelly, Mathieu Vézina*

