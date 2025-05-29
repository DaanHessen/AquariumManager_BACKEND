# Use Eclipse Temurin JDK Alpine for standardized Java environment
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Verify WAR file creation
RUN ls -la target/ && test -f target/aquarium-api.war

# Use standard Tomcat 10 Alpine for Jakarta EE compatibility
FROM tomcat:10.1-alpine

# Remove default webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR file to ROOT context
COPY --from=0 /app/target/aquarium-api.war /usr/local/tomcat/webapps/ROOT.war

# Create simple server.xml template
RUN echo '<?xml version="1.0" encoding="UTF-8"?>' > /usr/local/tomcat/conf/server.xml && \
    echo '<Server port="8005" shutdown="SHUTDOWN">' >> /usr/local/tomcat/conf/server.xml && \
    echo '  <Service name="Catalina">' >> /usr/local/tomcat/conf/server.xml && \
    echo '    <Connector port="PORT_PLACEHOLDER" protocol="HTTP/1.1"' >> /usr/local/tomcat/conf/server.xml && \
    echo '               connectionTimeout="20000" redirectPort="8443" />' >> /usr/local/tomcat/conf/server.xml && \
    echo '    <Engine name="Catalina" defaultHost="localhost">' >> /usr/local/tomcat/conf/server.xml && \
    echo '      <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">' >> /usr/local/tomcat/conf/server.xml && \
    echo '      </Host>' >> /usr/local/tomcat/conf/server.xml && \
    echo '    </Engine>' >> /usr/local/tomcat/conf/server.xml && \
    echo '  </Service>' >> /usr/local/tomcat/conf/server.xml && \
    echo '</Server>' >> /usr/local/tomcat/conf/server.xml

# Create startup script
RUN echo '#!/bin/sh' > /usr/local/tomcat/bin/startup.sh && \
    echo 'export PORT=${PORT:-8080}' >> /usr/local/tomcat/bin/startup.sh && \
    echo 'echo "Starting Aquarium API on port $PORT"' >> /usr/local/tomcat/bin/startup.sh && \
    echo 'sed "s/PORT_PLACEHOLDER/$PORT/g" /usr/local/tomcat/conf/server.xml > /tmp/server.xml' >> /usr/local/tomcat/bin/startup.sh && \
    echo 'mv /tmp/server.xml /usr/local/tomcat/conf/server.xml' >> /usr/local/tomcat/bin/startup.sh && \
    echo 'exec /usr/local/tomcat/bin/catalina.sh run' >> /usr/local/tomcat/bin/startup.sh && \
    chmod +x /usr/local/tomcat/bin/startup.sh

# Set JVM options for Railway
ENV CATALINA_OPTS="-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -XX:+UseG1GC -Xms256m -Xmx1024m"

# Expose port (Railway will override with PORT env var)
EXPOSE 8080

# Use startup script
CMD ["/usr/local/tomcat/bin/startup.sh"] 