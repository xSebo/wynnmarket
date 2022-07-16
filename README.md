# WynnMarket

## Explanation

WynnMarket is a THING I MADE. It consists of a client (the forge mod) and server (the spring boot server). The mod fetches information, formats it to JSON and sends it
to the server, which then formats it further, matches it with items in the database and saves to a file. This will be expanded in the future.

## Setup
Pre-requisites:
- Minecraft, with forge for 1.12.2 installed. During development, version 1.12.2-forge-14.23.5.2859 was used
- A computer

1. Clone the repository
2. Run gradle build within the forge directory.
3. Run gradle build within the server directory.
4. Place the jar from 1.12.2-forge-14.23.5.2859/build/libs in your forge mods folder, located in .minecraft.
5. Run the server with 'java -jar "wynnmarketserver-0.0.1-SNAPSHOT.jar"'. This jar is located in the wynnmarketserver/build/libs directory.
6. Once the server is running, navigate to http://localhost:8080/getItems, and then restart the server. This does not need to be done again.
7. Open minecraft with the mod installed, and join wynncraft. Find the nearest trade market and open it.
8. Create a macro to click at roughly 2.2CPS (The timings for this are Click down, wait 138ms, Click up wait 138ms), and hover over the down arrow. Let this run until there are no more items.
9. Once there are no items left, navigate to http://localhost:8080/items. This will save all the items currently stored in items.json, located wherever the jar is.
10. You can now stop the server and close minecraft, as the process is complete. Currently, the server must be restarted whenever you wish to read items again, but minecraft can remain running.

Enjoy
