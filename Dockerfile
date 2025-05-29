# Use Eclipse Temurin JDK for standardized Java environment
FROM eclipse-temurin:17-jdk

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./
COPY src ./src

# Make Maven wrapper executable and fix line endings
RUN chmod +x mvnw

# Build the application using system Maven
RUN mvn clean package -DskipTests

# Verify WAR file creation
RUN ls -la target/ && test -f target/aquarium-api.war

# Use standard Tomcat 10 with JDK 17 for Jakarta EE compatibility
FROM tomcat:10-jdk17-openjdk

# Remove default webapps
RUN rm -rf /opt/tomcat/webapps/*

# Copy WAR file to ROOT context
COPY --from=0 /app/target/aquarium-api.war /opt/tomcat/webapps/ROOT.war

# Create simple server.xml template
RUN echo '<?xml version="1.0" encoding="UTF-8"?>' > /opt/tomcat/conf/server.xml && \
    echo '<Server port="8005" shutdown="SHUTDOWN">' >> /opt/tomcat/conf/server.xml && \
    echo '  <Service name="Catalina">' >> /opt/tomcat/conf/server.xml && \
    echo '    <Connector port="PORT_PLACEHOLDER" protocol="HTTP/1.1"' >> /opt/tomcat/conf/server.xml && \
    echo '               connectionTimeout="20000" redirectPort="8443" />' >> /opt/tomcat/conf/server.xml && \
    echo '    <Engine name="Catalina" defaultHost="localhost">' >> /opt/tomcat/conf/server.xml && \
    echo '      <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">' >> /opt/tomcat/conf/server.xml && \
    echo '      </Host>' >> /opt/tomcat/conf/server.xml && \
    echo '    </Engine>' >> /opt/tomcat/conf/server.xml && \
    echo '  </Service>' >> /opt/tomcat/conf/server.xml && \
    echo '</Server>' >> /opt/tomcat/conf/server.xml

# Create startup script
RUN echo '#!/bin/sh' > /opt/tomcat/bin/startup.sh && \
    echo 'export PORT=${PORT:-8080}' >> /opt/tomcat/bin/startup.sh && \
    echo 'echo "Starting Aquarium API on port $PORT"' >> /opt/tomcat/bin/startup.sh && \
    echo 'sed "s/PORT_PLACEHOLDER/$PORT/g" /opt/tomcat/conf/server.xml > /tmp/server.xml' >> /opt/tomcat/bin/startup.sh && \
    echo 'mv /tmp/server.xml /opt/tomcat/conf/server.xml' >> /opt/tomcat/bin/startup.sh && \
    echo 'exec /opt/tomcat/bin/catalina.sh run' >> /opt/tomcat/bin/startup.sh && \
    chmod +x /opt/tomcat/bin/startup.sh

# Set JVM options for Railway
ENV CATALINA_OPTS="-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -XX:+UseG1GC -Xms256m -Xmx1024m"

# Expose port (Railway will override with PORT env var)
EXPOSE 8080

# Use startup script
CMD ["/opt/tomcat/bin/startup.sh"] 