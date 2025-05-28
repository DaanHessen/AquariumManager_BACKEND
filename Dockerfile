FROM tomcat:10-jdk17

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Create directory for H2 database files with proper permissions
RUN mkdir -p /opt/h2-data && \
    chmod -R 777 /opt/h2-data

# Set working directory
WORKDIR /usr/local/tomcat

# Add JAX-RS compatibility libraries - these need to be manually downloaded
RUN apt-get update && apt-get install -y wget && \
    mkdir -p /usr/local/tomcat/lib && \
    wget -O /usr/local/tomcat/lib/javax.ws.rs-api-2.1.1.jar https://repo1.maven.org/maven2/javax/ws/rs/javax.ws.rs-api/2.1.1/javax.ws.rs-api-2.1.1.jar && \
    wget -O /usr/local/tomcat/lib/jersey-common.jar https://repo1.maven.org/maven2/org/glassfish/jersey/core/jersey-common/3.1.3/jersey-common-3.1.3.jar && \
    wget -O /usr/local/tomcat/lib/jersey-client.jar https://repo1.maven.org/maven2/org/glassfish/jersey/core/jersey-client/3.1.3/jersey-client-3.1.3.jar && \
    wget -O /usr/local/tomcat/lib/jersey-server.jar https://repo1.maven.org/maven2/org/glassfish/jersey/core/jersey-server/3.1.3/jersey-server-3.1.3.jar

# No need to copy the WAR file as it will be mounted as a volume

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"] 