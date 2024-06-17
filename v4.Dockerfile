FROM payara/server-full:5.2021.10-jdk11
LABEL dockerfile="v4.Dockerfile"
LABEL description="This version: DB is postgresql and payara is updated"
LABEL description="trsa-files-directory is under /opt/payara"
LABEL description="This version assumes postgreSQL is already running"

ARG TRSA_VERSION=4.0
ARG DB_SERVER

COPY jhove* /tmp/
COPY v4.trsa.config /tmp/trsa.config

# !!!!! important !!!!!
# Due to the USER setting in the base-image, trsa's file storage-root is
# to be created under a directory owned by the user payara, which differs
# from the one set in trsa.config

ENV FILES_DIR=/opt/payara/trsa/files \
    DOMAIN_NAME=domain1 \
    POSTGRES_SERVER=$DB_SERVER
RUN mkdir -p ${FILES_DIR} && \
    chown payara ${FILES_DIR}

COPY psql/postgresql-42.3.5.jar ${PAYARA_DIR}/glassfish/domains/domain1/lib

# JDBC-resource/pool settings are now payara-resources.xml
RUN  ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} start-domain ${DOMAIN_NAME} && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.logtoFile=true && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options '-Dtrsa.configfile.directory=${com.sun.aas.installRoot}/domains/domain1/config' && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options "\-Dtrsa.files.directory=${FILES_DIR}" && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGPoolingDataSource --restype javax.sql.DataSource --property "create=true:User=dvnapp:PortNumber=5432:databaseName=dvndb:password=secret:ServerName=${POSTGRES_SERVER}" dvnDbPool && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-resource --connectionpoolid dvnDbPool jdbc/VDCNetDS &&\
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} stop-domain ${DOMAIN_NAME} 

RUN cp /tmp/jhove* ${PAYARA_DIR}/glassfish/domains/domain1/config/ && \
    cp /tmp/trsa.config ${PAYARA_DIR}/glassfish/domains/domain1/config/ 

COPY target/trsa-web-${TRSA_VERSION}.war $DEPLOY_DIR/ 


RUN mkdir -p ${SCRIPT_DIR}/init.d && \
    chmod +x ${SCRIPT_DIR}/*