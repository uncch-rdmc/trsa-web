FROM payara/server-full:5.2021.10-jdk11
LABEL dockerfile="v3.Dockerfile"
LABEL description="This version: H2 is run in the mixed mode "
LABEL description="trsa-files-directory is under /opt/payara"


ARG TRSA_VERSION=3.0

COPY jhove* /tmp/
COPY v3.trsa.config /tmp/trsa.config

# !!!!! important !!!!!
# Due to the USER setting in the base-image, trsa's file storage-root is
# to be created under a directory owned by the user payara, which differs
# from the one set in trsa.config

ENV FILES_DIR=/opt/payara/trsa/files \
    DOMAIN_NAME=domain1

RUN mkdir -p ${FILES_DIR} && \
    chown payara ${FILES_DIR}


RUN  ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} start-domain ${DOMAIN_NAME} && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.logtoFile=true && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options '-Dtrsa.configfile.directory=${com.sun.aas.installRoot}/domains/domain1/config' && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options "\-Dtrsa.files.directory=${FILES_DIR}" && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-connection-pool --datasourceclassname org.h2.jdbcx.JdbcDataSource --restype javax.sql.DataSource --property 'url="jdbc:h2:${com.sun.aas.instanceRoot}/lib/databases/trsa2;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9595":user=impactUser2:password=1mq@xt6z312' H2impact2Pool && \
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-resource --connectionpoolid H2impact2Pool jdbc/trsa2 &&\
${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} stop-domain ${DOMAIN_NAME} 

RUN cp /tmp/jhove* ${PAYARA_DIR}/glassfish/domains/domain1/config/ && \
    cp /tmp/trsa.config ${PAYARA_DIR}/glassfish/domains/domain1/config/ 

COPY target/trsa-web-${TRSA_VERSION}.war $DEPLOY_DIR/

RUN mkdir -p ${SCRIPT_DIR}/init.d && \
    chmod +x ${SCRIPT_DIR}/*