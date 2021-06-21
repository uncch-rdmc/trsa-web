# This version uses H2's mixed mode setup; no installation of H2
# The war file to be copied into the image must exist in target/ directory

# FROM payara/server-full:5.2021.1
FROM payara/server-full:5.2021.2-jdk11
# as: built-in exposed ports: 4848 8080 8181
LABEL dockerfile="v2.Dockerfile"
LABEL description="This version uses H2's mixed mode setup"

# as: FROM openjdk:8

# Default payara ports to expose
# 4848: admin console
# 8080: http
# 8181: https
# 9009: debug port (JPDA)
# as: EXPOSE 4848 8080 8181 9009
# as: ARG PAYARA_PKG=https://github.com/payara/Payara/releases/download/payara-server-5.2020.5/payara-5.2020.5.zip
# as: ARG PAYARA_SHA1=edda839aa42898410051d44d36ea4d926535dbf0

# as: the base image includes tini
# as: ARG TINI_VERSION=v0.19.0
ARG TRSA_VERSION=2.0
# as: ARG GF_UID=1000
# as: ARG GF_GID=1000

# odum: avoid gpg errors
# as: RUN apt install -y dirmngr gnupg gpgv

# odum: trsa prototype uses h2
# as: using H2 with Payara
# as: COPY install_h2.sh /install_h2.sh
# as: RUN /install_h2.sh

# odum: stage jhove additions and trsa.config
COPY jhove* /tmp/
COPY v2.trsa.config /tmp/trsa.config

# odum: just use domain1 for prototype
# Initialize the configurable environment variables
# as: ENV HOME_DIR=/opt/payara\
# as: PAYARA_DIR=/opt/payara/appserver\
# as: SCRIPT_DIR=/opt/payara/scripts\
# as: CONFIG_DIR=/opt/payara/config\
# as: DEPLOY_DIR=/opt/payara/deployments\
ENV FILES_DIR=/opt/payara/trsa/files \
    DOMAIN_NAME=domain1
# as: PASSWORD_FILE=/opt/payara/passwordFile\
    # Payara Server Domain options
# as: DOMAIN_NAME=domain1\
# as: ADMIN_USER=admin\
# as: ADMIN_PASSWORD=admin \
    # Utility environment variables
# as: JVM_ARGS=\
# as: DEPLOY_PROPS=\
# as: POSTBOOT_COMMANDS=/opt/payara/config/post-boot-commands.asadmin\
# as: PREBOOT_COMMANDS=/opt/payara/config/pre-boot-commands.asadmin
# as: ENV PATH="${PATH}:${PAYARA_DIR}/bin"

# Create and set the Payara user and working directory owned by the new user
# as: RUN groupadd -g ${GF_GID} payara && \
# as: useradd -u ${GF_UID} -M -s /bin/bash -d ${HOME_DIR} payara -g payara && \
# as: echo payara:payara | chpasswd && \
# as: mkdir -p ${DEPLOY_DIR} && \
# as: mkdir -p ${CONFIG_DIR} && \
# as: mkdir -p ${SCRIPT_DIR} && \
# as: chown -R payara: ${HOME_DIR}

# create default files_dir
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




# odum: hard-code warfile for now
COPY target/trsa-web-${TRSA_VERSION}.war $DEPLOY_DIR/

# Install tini as minimized init system
# as: RUN wget --no-verbose -O tini-amd64 https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini-amd64 && \
# as: echo '93dcc18adc78c65a028a84799ecf8ad40c936fdfc5f2a57b1acda5a8117fa82c tini-amd64' | sha256sum -c - && \
# as: mv tini-amd64 /tini && chmod +x /tini

# as: USER payara
# as:WORKDIR ${HOME_DIR}

# Download and unzip the Payara distribution
# RUN 
# as:    wget --no-verbose -O payara.zip ${PAYARA_PKG} && \
# as:    echo "${PAYARA_SHA1} *payara.zip" | sha1sum -c - && \
# as:    unzip -qq payara.zip -d ./ && \
# as:    mv payara*/ appserver && \
    # odum: need the h2 jar in glassfish
# as: RUN    cp /opt/h2/bin/*.jar ${PAYARA_DIR}/glassfish/modules/ 
# as: && \
    # Configure the password file for configuring Payara
# as:    echo "AS_ADMIN_PASSWORD=\nAS_ADMIN_NEWPASSWORD=${ADMIN_PASSWORD}" > /tmp/tmpfile && \
# as:    echo "AS_ADMIN_PASSWORD=${ADMIN_PASSWORD}" >> ${PASSWORD_FILE} && \
    # Configure the payara domain
# as:    ${PAYARA_DIR}/bin/asadmin --user ${ADMIN_USER} --passwordfile=/tmp/tmpfile change-admin-password --domain_name=${DOMAIN_NAME} && \
# as: RUN     ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} start-domain ${DOMAIN_NAME} && \
# as:    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} enable-secure-admin && \

    # odum: add jhove confs and trsa.config

    # odum: config settings
# as:    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options '-Dtrsa.configfile.directory=${com.sun.aas.installRoot}/domains/domain1/config' && \

# as:    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options "\-Dtrsa.files.directory=${FILES_DIR}" && \
    # odum: configure h2 jdbc resource/connection pool
# as:    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-connection-pool --datasourceclassname org.h2.jdbcx.JdbcDataSource --restype javax.sql.DataSource --property user=impactUser:password=1mq\@xt6z312:url="jdbc\:h2\:${PAYARA_DIR}/glassfish/domains/domain1/lib/databases/trsa2;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9595" H2impact2Pool && \
# as:    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-resource --connectionpoolid H2impact2Pool jdbc/trsa2 
# as: && \

# as:    for MEMORY_JVM_OPTION in $(${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} list-jvm-options | grep "Xm[sx]"); do\
# as:        ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} delete-jvm-options $MEMORY_JVM_OPTION;\
# as:    done && \
    # FIXME: when upgrading this container to Java 10+, this needs to be changed to '-XX:+UseContainerSupport' and '-XX:MaxRAMPercentage'
# as:    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options '-XX\:+UnlockExperimentalVMOptions:-XX\:+UseCGroupMemoryLimitForHeap:-XX\:MaxRAMFraction=1' && \
    # FIXME: waiting on fix to https://github.com/payara/Payara/issues/3506
    #${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.logtoFile=false && \
#    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} stop-domain ${DOMAIN_NAME} && \
    # Cleanup unused files
# as:    rm -rf \
# as:        /tmp/tmpFile \
# as:        payara.zip \
# as:        ${PAYARA_DIR}/glassfish/domains/${DOMAIN_NAME}/osgi-cache \
# as:        ${PAYARA_DIR}/glassfish/domains/${DOMAIN_NAME}/logs

# Copy across docker scripts
# as: COPY --chown=payara:payara bin/*.sh ${SCRIPT_DIR}/
RUN mkdir -p ${SCRIPT_DIR}/init.d && \
    chmod +x ${SCRIPT_DIR}/*

# copy h2 launch script
# as: COPY launch_h2.sh ${SCRIPT_DIR}/init.d/

# COPY init_jvm_options.sh ${SCRIPT_DIR}/init_2_jvm_options.sh
#RUN echo 'create-jvm-options -Dtrsa.configfile.directory=${com.sun.aas.installRoot}/domains/domain1/config'  >> $PREBOOT_COMMANDS
#RUN echo 'create-jvm-options -Dtrsa.files.directory=${ENV=FILES_DIR}' >> $PREBOOT_COMMANDS


# as: the following two lines are included in the payara image
# as: ENTRYPOINT ["/tini", "--"]
# as: CMD ["scripts/entrypoint.sh"]
