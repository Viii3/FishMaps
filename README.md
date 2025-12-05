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

## Azul Testing
For testing with Azul, there are Python scripts attached the `./azul/scripts` folder.

Be advised that Out of Memory errors tend to occur with scripts over the 10,000 mark, increasing the JVM max heap size is recommended.

The program will search for a `.env` file in the `glassfish/domains/domain1` folder, or if not found, at `/tmp/.env`. Setting the environment variables manually will also work.

The `.env` file must contain the following variables.
- CONFLUENCE_API_TOKEN 
  - Your Confluence API key.
- CONFLUENCE_BASE_URL
  - Should be `https://payara.atlassian.net/wiki`
- CONFLUENCE_USERNAME
  - This is your Payara email.
- CONFLUENCE_SPACE_KEY
  - The key for the space that you wish to publish the results to.

## Setup
FishMaps will require a Postgres database (`localhost:5432`) with the name FishMaps.

The following batch script can be used for quick setup:
```
@ECHO OFF

CALL asadmin start-domain

CALL asadmin add-library "C:\path\to\your\postgresql-42.3.1.jar"
CALL asadmin create-jdbc-connection-pool --dataSourceClassname=org.postgresql.ds.PGSimpleDataSource --resType=javax.sql.DataSource postgres-pool
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.password=DATABASE_PASSWORD
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.databaseName=FishMaps
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.serverName=localhost
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.user=postgres
CALL asadmin set resources.jdbc-connection-pool.postgres-pool.property.portNumber=5432

CALL asadmin create-file-user --groups fishmaps_admin --passwordfile "C:\path\to\default_file_user" --authrealmname file fishmaps_admin
CALL asadmin create-jms-resource --restype jakarta.jms.ConnectionFactory jms/ConnectionFactory
CALL asadmin create-jms-resource --restype jakarta.jms.Topic --property Name=BlockTopic jms/BlockTopic

CALL asadmin ping-connection-pool postgres-pool

CALL asadmin create-jdbc-resource --enabled=true --poolName=postgres-pool --target=domain jdbc/local-postgres
CALL asadmin create-resource-ref --enabled=true --target=server jdbc/local-postgres

CALL asadmin stop-domain
```
