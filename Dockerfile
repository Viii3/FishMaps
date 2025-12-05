FROM payara/server-full:6.2025.1-jdk21

COPY postgres.jar /tmp
COPY default_file_user /tmp
COPY .env /tmp
ENV azul_json_save_directory=/tmp/azul_json/
RUN echo 'add-library /tmp/postgres.jar' > $POSTBOOT_COMMANDS
COPY target/*.war $DEPLOY_DIR/fishmaps.war

RUN echo "create-jvm-options '-Xmx4g'" >> $POSTBOOT_COMMANDS
RUN echo 'create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --driverclassname org.postgresql.Driver --restype javax.sql.DataSource --property user=postgres:password=password:URL="jdbc\\:postgresql\\://fishmaps-db/fishmaps" fishmapsPool' >> $POSTBOOT_COMMANDS
RUN echo 'ping-connection-pool fishmapsPool' >> $POSTBOOT_COMMANDS
RUN echo 'create-jdbc-resource --connectionpoolid fishmapsPool jdbc/fishmaps' >> $POSTBOOT_COMMANDS
#RUN echo 'create-auth-realm --classname=com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm --property jaas-context=jdbcRealm:datasource-jndi=jdbc/fishmaps:user-table=person:user-name-column=username:password-column=pwd:group-table=security_groups:group-table-user-name-column=username:group-name-column=groupname:digest-algorithm=SHA-256:encoding=Base64:charset=UTF-8: fishmaps_realm' >> $POSTBOOT_COMMANDS
RUN echo 'create-file-user --groups fishmaps_admin --passwordfile /tmp/default_file_user --authrealmname file fishmaps_admin' >> $POSTBOOT_COMMANDS
RUN echo 'create-jms-resource --restype jakarta.jms.ConnectionFactory jms/ConnectionFactory' >> $POSTBOOT_COMMANDS
RUN echo 'create-jms-resource --restype jakarta.jms.Topic --property Name=BlockTopic jms/BlockTopic' >> $POSTBOOT_COMMANDS