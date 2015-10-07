CS 447 Project 2 - Final Readme

Group Members: 
Josh Crawford
Vitaliy 
Rolf

Running (Server/Client on same computer):
  1) start server with java -jar server.jar
  2) start client with java -Xms128m -Xmx512m -jar client.jar
  3) manually enter the 127.0.0.1 as the server ip when the client starts.
  4) hit an F key as shown below to choose a spawn location 

Controls:
  w - jetpack
  a - left
  d - right
  space - jump
  mouse - aiming the cross-hair for weapons
  left mouse button - fire
  1-3 - switches weapons: 1=rifle, 2=shotgun, 3=grenades
  F1-F4 - choose a spawn location when starting the game or dead

Low Bar Goals Achieved:
  Player Movement - Completed by adding force to player objects 
                    controlled by the physics engine. The physics engine 
                    was modified so the force would continue until turned off 
                    usually by releasing the movement key. The velocity 
                    was clamped to prevent infinite speed. 

  Combat - Completed by generating bullets controlled by the physics engine. 
           The bullet collisions would register damage to the player objects.

  Deathmatch - Completed with a match class that would keep track of scores 
               and time limits.

  Networking - Completed by introducing threaded network engine that 
               broadcasts game state every few milliseconds to a list of 
               connected clients. Clients would predict object movement and 
               would correct the position when new packets arrived from the 
               server. UDP was used to broadcast game state and a TCP to 
               communicate reliable information between the client and server.

  2D Physics - Completed by integrating the Catto Physics Engine into the game.
               Three weapon types - Completed with a rifle, shotgun and grenade.
               Grenades include a timer to control detonation.
               Player has all weapons and unlimited ammo - Completed.

	
Other features, not stated in the initial specs:
  User interface - Shows health and jetpack fuel. Additional game status 
                   were also displayed such as the time remaining in the match 
                   and player stats at the end of a match.


Technical Show-pieces:
  Networking - Demonstrated with a networked game in the lab
  2D Physics - Player movement, grenades, boxes, barrels and bullet collisions.

Attributions 
Sprites - Derick
Physics Engine - Eric Catto, Scott Wallace

Everything here is licensed under MIT license.
