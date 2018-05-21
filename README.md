# Description
A Minecraft plugin for creating maps with checkpoints throughout that players can complete in order to receive rewards

# Commands
- /maps help - Shows all commands in-game
- /maps setspawn \<name> - Sets the spawn to where you are standing
- /maps addreward \<name> \<command> - Adds a command that is executed when the player finishes
- /maps setend \<name> - Sets the end region (From WorldGuard selection)
- /maps setlimit \<name> \<limit> - Sets the maximum amount of the arena that can be played at once
- /maps setmap \<name> \<nameOfMapInMapsFolder> - Sets the maps world
- /maps setplayerlimit \<name> \<limit> - Sets the player limit for multiplayer
- /maps addpoint \<name> - Adds a checkpoint at your location
- /maps delpoint \<name> - Removes a checkpoint at your location
- /maps settime \<name> \<minutes> - Sets the time limit for an arena
- /maps delreward \<name> \<command> - Removes a reward command

# Permissions
- maps.create
- maps.setspawn
- maps.addreward
- maps.delreward
- maps.setend
- maps.setmap
- maps.ready
- maps.setlimit
- maps.ready
- maps.settime
- maps.setplayerlimit

# How to set up a map
First you have to copy the the world folder of the map you want to use into plugins/AdventureMaps/maps/ (Create it if it doesn't exist!)

**Note:** Make sure to delete the uid.dat inside the folder you copied

Now you can create an arena with /maps create \<mapName>
Then the following commands have to be ran to set it up:

- /maps setspawn \<name> - Sets the spawn to where you are standing
- /maps addreward \<name> \<command> - Adds a command that is executed when the player finishes
- /maps setend \<name> - Sets the end region (From WorldGuard selection)
- /maps setlimit \<name> \<limit> - Sets the maximum amount of the arena that can be played at once
- /maps setmap \<name> \<nameOfMapInMapsFolder> - Sets the maps world
- /maps setplayerlimit \<name> \<limit> - Sets the player limit for multiplayer


Optional Commands:
- /maps addpoint \<name> - Adds a checkpoint at your location

- /maps delpoint \<name> - Removes a checkpoint at your location

- /maps settime \<name> \<minutes> - Sets the time limit for an arena

- /maps delreward \<name> \<command> - Removes a reward command

Then finally the command /maps ready \<name> must be ran to set the arena as ready to play.

