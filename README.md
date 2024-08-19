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