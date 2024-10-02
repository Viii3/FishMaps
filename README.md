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

## Setup
FishMaps will require a Postgres database (`localhost:5432`) with the name FishMaps.

The following batch script can be used for quick setup:
```
@ECHO OFF

CALL asadmin start-domain

CALL asadmin add-library "C:\path\to\your\.m2\repository\org\postgresql\postgresql\42.3.1\postgresql-42.3.1.jar"
CALL asadmin create-jdbc-connection-pool --dataSourceClassname=org.postgresql.ds.PGSimpleDataSource --resType=javax.sql.DataSource postgres-pool
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.password=DATABASE_PASSWORD
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.databaseName=FishMaps
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.serverName=localhost
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.user=postgres
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.portNumber=5432

CALL asadmin ping-connection-pool postgres-pool

CALL asadmin create-jdbc-resource --enabled=true --poolName=postgres-pool --target=domain jdbc/local-postgres
CALL asadmin create-resource-ref --enabled=true --target=server jdbc/local-postgres

CALL asadmin stop-domain
```

## Reproducer for GOAWAY
This branch and the associated [client branch](https://github.com/Viii3/FishMaps-Client/tree/Reproducer-for-GOAWAY)
create an error where the server prints "Unable to send GOAWAY. Session terminated" and crashes.

To reproduce this error:
- Deploy the WAR file compiled from this branch and run the standard setup.
- Run the associated version of the client application.
  - This can be in a live environment or the integrated testing environment.
  - If in a singleplayer environment, you will need to open/create a world.
- Open the web application and view the Overworld map.
  - Whenever the map flashes black, that is a tile updating.
- Watch the server logs, the error may take a few minutes to appear.
  - It will likely be preceded by nullpointer exceptions, these appear to be part of the problem, but they shouldn't be there either.

Leaving the applications open for long enough will overload and crash the server, asadmin will be unresponsive.
