# WynnMarket

## Explanation

WynnMarket is a THING I MADE. It consists of a client (the forge mod) and server (the spring boot server). The mod fetches information from the wynncraft in game market, formats it to JSON and sends it to the server, which then formats it further, matches it with items in the database and saves to a file. This will be expanded in the future.

## Setup
Pre-requisites:
- Minecraft, with forge for 1.12.2 installed. During development, version 1.12.2-forge-14.23.5.2859 was used
- A computer

1. Clone the repository
2. Run gradle build within the forge directory.
3. Run gradle bootJar within the server directory.
4. Place the jar from 1.12.2-forge-14.23.5.2859/build/libs in your forge mods folder, located in .minecraft.
5. Run the server with 'java -jar "wynnmarketserver-0.0.1-SNAPSHOT.jar"'. This jar is located in the wynnmarketserver/build/libs directory.
- Note: When running the server for the first time, it will take a little longer to start as the local item database needs to build.

6. Open minecraft with the mod installed, and join wynncraft.
7. Open the auction house, click next page once and then do not move your mouse. Once all pages have been scrolled through, the mouse will stop clicking.
9. Navigate to localhost:8080/ to view all of the items pulled from the auction house

Enjoy
