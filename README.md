# FishMaps

A mapping tool, that uses Payara Server and Jakarta EE to display a live Minecraft map in your browser.

## Project Technologies
- Servlets
  - Generating map tiles.
  - Generating player icons.
- REST API
  - Populating the database.
  - Updating players and events.
- Postgres Database
  - Storing every individual pixel for the map.
  - Storing player and event locations.

## Associated Application
Data can be fed into FishMaps by any application, but it is intended for use with the [FishMaps Minecraft mod](https://github.com/Viii3/FishMaps-Client).