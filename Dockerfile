FROM openjdk:8

# Default payara ports to expose
# 4848: admin console
# 8080: http
# 8181: https
# 9009: debug port (JPDA)
EXPOSE 4848 8080 8181 9009

ARG PAYARA_PKG=https://github.com/payara/Payara/releases/download/payara-server-5.2020.5/payara-5.2020.5.zip
ARG PAYARA_SHA1=edda839aa42898410051d44d36ea4d926535dbf0
ARG TINI_VERSION=v0.19.0
ARG TRSA_VERSION=2.0
ARG GF_UID=1000
ARG GF_GID=1000

# odum: avoid gpg errors
RUN apt install -y dirmngr gnupg gpgv

# odum: trsa prototype uses h2
COPY install_h2.sh /install_h2.sh
RUN /install_h2.sh

# odum: stage jhove additions and trsa.config
COPY jhove* /tmp/
COPY trsa.config /tmp/

# odum: just use domain1 for prototype
# Initialize the configurable environment variables
ENV HOME_DIR=/opt/payara\
    PAYARA_DIR=/opt/payara/appserver\
    SCRIPT_DIR=/opt/payara/scripts\
    CONFIG_DIR=/opt/payara/config\
    DEPLOY_DIR=/opt/payara/deployments\
    FILES_DIR=/trsa/files\
    PASSWORD_FILE=/opt/payara/passwordFile\
    # Payara Server Domain options
    DOMAIN_NAME=domain1\
    ADMIN_USER=admin\
    ADMIN_PASSWORD=admin \
    # Utility environment variables
    JVM_ARGS=\
    DEPLOY_PROPS=\
    POSTBOOT_COMMANDS=/opt/payara/config/post-boot-commands.asadmin\
    PREBOOT_COMMANDS=/opt/payara/config/pre-boot-commands.asadmin
ENV PATH="${PATH}:${PAYARA_DIR}/bin"

# Create and set the Payara user and working directory owned by the new user
RUN groupadd -g ${GF_GID} payara && \
    useradd -u ${GF_UID} -M -s /bin/bash -d ${HOME_DIR} payara -g payara && \
    echo payara:payara | chpasswd && \
    mkdir -p ${DEPLOY_DIR} && \
    mkdir -p ${CONFIG_DIR} && \
    mkdir -p ${SCRIPT_DIR} && \
    chown -R payara: ${HOME_DIR}

# create default files_dir
RUN mkdir -p ${FILES_DIR} && \
    chown payara ${FILES_DIR}

# odum: hard-code warfile for now
COPY trsa-web-${TRSA_VERSION}.war $DEPLOY_DIR/

# Install tini as minimized init system
# RUN wget --no-verbose -O /tini https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini && \
#     wget --no-verbose -O /tini.asc https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini.asc && \
#     gpg --batch --keyserver "hkp://p80.pool.sks-keyservers.net:80" --recv-keys 595E85A6B1B4779EA4DAAEC70B588DFF0527A9B7 && \
#     gpg --batch --verify /tini.asc /tini && \
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini

USER payara
WORKDIR ${HOME_DIR}

# Download and unzip the Payara distribution
RUN wget --no-verbose -O payara.zip ${PAYARA_PKG} && \
    echo "${PAYARA_SHA1} *payara.zip" | sha1sum -c - && \
    unzip -qq payara.zip -d ./ && \
    mv payara*/ appserver && \
    # odum: need the h2 jar in glassfish
    cp /opt/h2/bin/*.jar ${PAYARA_DIR}/glassfish/modules/ && \
    # Configure the password file for configuring Payara
    echo "AS_ADMIN_PASSWORD=\nAS_ADMIN_NEWPASSWORD=${ADMIN_PASSWORD}" > /tmp/tmpfile && \
    echo "AS_ADMIN_PASSWORD=${ADMIN_PASSWORD}" >> ${PASSWORD_FILE} && \
    # Configure the payara domain
    ${PAYARA_DIR}/bin/asadmin --user ${ADMIN_USER} --passwordfile=/tmp/tmpfile change-admin-password --domain_name=${DOMAIN_NAME} && \
    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} start-domain ${DOMAIN_NAME} && \
    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} enable-secure-admin && \
    # odum: add jhove confs and trsa.config
    cp /tmp/jhove* ${PAYARA_DIR}/glassfish/domains/domain1/config/ && \
    cp /tmp/trsa.config ${PAYARA_DIR}/glassfish/domains/domain1/config/ && \
    # odum: config settings
    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options '-Dtrsa.configfile.directory=${com.sun.aas.installRoot}/domains/domain1/config' && \
    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options "\-Dtrsa.files.directory=${FILES_DIR}" && \
    # odum: configure h2 jdbc resource/connection pool
    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-connection-pool --datasourceclassname org.h2.jdbcx.JdbcDataSource --restype javax.sql.DataSource --property user=impactUser:password=1mq\@xt6z31:url="jdbc\:h2\:tcp\://localhost/~/trsa" H2impactPool && \
    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jdbc-resource --connectionpoolid H2impactPool jdbc/trsa && \
    for MEMORY_JVM_OPTION in $(${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} list-jvm-options | grep "Xm[sx]"); do\
        ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} delete-jvm-options $MEMORY_JVM_OPTION;\
    done && \
    # FIXME: when upgrading this container to Java 10+, this needs to be changed to '-XX:+UseContainerSupport' and '-XX:MaxRAMPercentage'
    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} create-jvm-options '-XX\:+UnlockExperimentalVMOptions:-XX\:+UseCGroupMemoryLimitForHeap:-XX\:MaxRAMFraction=1' && \
    # FIXME: waiting on fix to https://github.com/payara/Payara/issues/3506
    #${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} set-log-attributes com.sun.enterprise.server.logging.GFFileHandler.logtoFile=false && \
    ${PAYARA_DIR}/bin/asadmin --user=${ADMIN_USER} --passwordfile=${PASSWORD_FILE} stop-domain ${DOMAIN_NAME} && \
    # Cleanup unused files
    rm -rf \
        /tmp/tmpFile \
        payara.zip \
        ${PAYARA_DIR}/glassfish/domains/${DOMAIN_NAME}/osgi-cache \
        ${PAYARA_DIR}/glassfish/domains/${DOMAIN_NAME}/logs

# Copy across docker scripts
COPY --chown=payara:payara bin/*.sh ${SCRIPT_DIR}/
RUN mkdir -p ${SCRIPT_DIR}/init.d && \
    chmod +x ${SCRIPT_DIR}/*

# copy h2 launch script
COPY launch_h2.sh ${SCRIPT_DIR}/init.d/

ENTRYPOINT ["/tini", "--"]
CMD ["scripts/entrypoint.sh"]
