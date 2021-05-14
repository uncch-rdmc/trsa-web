# This version uses H2's tcp (server-mode) as the baseline
# not mixed mode case

FROM payara/server-full:5.2021.2
LABEL dockerfile="v21.Dockerfile"
LABEL description="This version uses H2's tcp mode setup"
ARG TRSA_VERSION=2.0

# odum: stage jhove additions and trsa.config
COPY jhove* /tmp/
COPY trsa.config /tmp/

# odum: just use domain1 for prototype

ENV FILES_DIR=/opt/payara/trsa/files


# odum: trsa prototype uses h2
COPY install_h2.sh /install_h2.sh
RUN /install_h2.sh


# create default files_dir
RUN mkdir -p ${FILES_DIR} && \
  chown payara ${FILES_DIR}  && \
  cp /tmp/jhove* ${PAYARA_DIR}/glassfish/domains/domain1/config/ && \
  cp /tmp/trsa.config ${PAYARA_DIR}/glassfish/domains/domain1/config/  && \
  cp /opt/payara/appserver/h2db/bin/h2.jar ${PAYARA_DIR}/glassfish/modules/ && \
  ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} start-domain ${DOMAIN_NAME} && \
  ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options '-Dtrsa.configfile.directory=${com.sun.aas.installRoot}/domains/domain1/config' && \
  ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options "\-Dtrsa.files.directory=${FILES_DIR}" && \
   # odum: configure h2 jdbc resource/connection pool
  ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-connection-pool --datasourceclassname org.h2.jdbcx.JdbcDataSource --restype javax.sql.DataSource --property user=impactUser:password=1mq\@xt6z312:url="jdbc\:h2\:tcp\://localhost/~/trsa" H2impact2Pool && \
  ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-resource --connectionpoolid H2impact2Pool jdbc/trsa && \
  ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.logtoFile=true && \
  ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} stop-domain ${DOMAIN_NAME} 

# odum: hard-code warfile for now
COPY trsa-web-${TRSA_VERSION}.war ${DEPLOY_DIR}/

# COPY init_jvm_options.sh ${SCRIPT_DIR}/init_2_jvm_options.sh
#RUN echo 'create-jvm-options -Dtrsa.configfile.directory=${com.sun.aas.installRoot}/domains/domain1/config'  >> $PREBOOT_COMMANDS
#RUN echo 'create-jvm-options -Dtrsa.files.directory=${ENV=FILES_DIR}' >> $PREBOOT_COMMANDS

COPY launch_h2.sh ${SCRIPT_DIR}/init.d/