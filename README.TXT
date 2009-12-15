CS 447 Project 2 - Final Readme

Group Members: 
Josh Crawford
Vitaliy 
Rolf

Running the server and client on the same computer.
Step 1) start server with java -jar server.jar
Step 2) start client with java -Xms128m -Xmx512m -jar client.jar
Step 3) manually enter the 127.0.0.1 as the server ip when the client starts.
Step 4) hit an F key as shown below to choose a spawn location 

w - jetpack
a - left
d - right
space - jump
mouse - aiming the crosshair for weapons
left mouse button - fire
1-3 - switches weapons: 1=rifle, 2=shotgun, 3=grenades
F1-F4 - choose a spawn location when starting the game or dead

Low Bar Goals Achived
Player Movement - Completed by adding force to player objects controlled by the physics engine.
	The physics engine was modified so the force would continue until turned off usually by releasing the movement key.
	The velocity was clamped to prevent infinite speed. 
Combat - Completed by generating bullets controlled by the physics engine. The bullet collisions would register
	damage to the player objects.
Deathmatch - Completed with a match class that would keep track of scores and timelimits.
Networking - Completed by brodcating game state packets every few milliseconds to a list of clients connected.
	Clients would predict object movement and would correct when new packets arrived from the server. UDP was 
	used to broadcast packets and a TCP layer was used to communicate data sensitive information between the 
	client and server.
2D Physics - Completed by integrating the Catto Physics Engine into the game.
Three weapon types - Completed with a rifle, shotgun and grenade. Grenades include a timer to control detonation.
Player has all weapons and unlimited ammo - Completed.

Hight Bar Goals Achived
	No high bar items stated in the design document were completed.
	
Other Features Not Stated
User interface - Completed shoing health and jetpack fuel. Additional game status were also displayed such 
	as the time remaining in the match and player stats at the end of a match.
Jetpack
Variable Terrain
Map loader - Custom maps can easily be dropped into a text file to be played
Server Test Environment - The server has a test environment for easy testing new code.

Cheat Codes
None

Technical Showpeices
Networking - Demonstrated with a networked game in the lab
2D Physics - Player movement, grenades, boxes, barrels and bullet collisions demonstrate this.

Attributions 
Sprites - Derick
Physics Engine - Eric Catto, Scott Wallace

License
MIT