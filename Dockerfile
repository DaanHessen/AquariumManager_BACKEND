# Use Eclipse Temurin JDK Alpine image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy all project files
COPY . ./

# Make Maven wrapper executable and ensure Unix line endings
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

# Build the application
RUN ./mvnw -B -DskipTests clean package

# Verify that WAR file was created
RUN ls -la target/ && \
    test -f target/aquarium-api.war || (echo "WAR file not found!" && exit 1)

# Install Tomcat 10 (Jakarta EE compatible)
RUN apk add --no-cache curl && \
    curl -O https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.15/bin/apache-tomcat-10.1.15.tar.gz && \
    tar -xzf apache-tomcat-10.1.15.tar.gz && \
    mv apache-tomcat-10.1.15 /opt/tomcat && \
    rm apache-tomcat-10.1.15.tar.gz

# Copy WAR file to Tomcat webapps
RUN cp target/aquarium-api.war /opt/tomcat/webapps/ROOT.war

# Set environment variables for Tomcat
ENV CATALINA_HOME=/opt/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

# Create server.xml with dynamic port configuration for Railway
RUN echo '<?xml version="1.0" encoding="UTF-8"?>' > /opt/tomcat/conf/server.xml && \
    echo '<Server port="8005" shutdown="SHUTDOWN">' >> /opt/tomcat/conf/server.xml && \
    echo '  <Service name="Catalina">' >> /opt/tomcat/conf/server.xml && \
    echo '    <Connector port="${PORT}" protocol="HTTP/1.1"' >> /opt/tomcat/conf/server.xml && \
    echo '               connectionTimeout="20000"' >> /opt/tomcat/conf/server.xml && \
    echo '               redirectPort="8443" />' >> /opt/tomcat/conf/server.xml && \
    echo '    <Engine name="Catalina" defaultHost="localhost">' >> /opt/tomcat/conf/server.xml && \
    echo '      <Host name="localhost" appBase="webapps"' >> /opt/tomcat/conf/server.xml && \
    echo '            unpackWARs="true" autoDeploy="true">' >> /opt/tomcat/conf/server.xml && \
    echo '      </Host>' >> /opt/tomcat/conf/server.xml && \
    echo '    </Engine>' >> /opt/tomcat/conf/server.xml && \
    echo '  </Service>' >> /opt/tomcat/conf/server.xml && \
    echo '</Server>' >> /opt/tomcat/conf/server.xml

# Create a simple startup script that handles the PORT variable correctly
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'set -e' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Set the port for Tomcat - Railway provides PORT environment variable' >> /app/start.sh && \
    echo 'export PORT=${PORT:-8080}' >> /app/start.sh && \
    echo 'echo "Starting Aquarium API on port $PORT..."' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Set JVM options for Railway deployment' >> /app/start.sh && \
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -Djava.awt.headless=true"' >> /app/start.sh && \
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -Djava.security.egd=file:/dev/./urandom"' >> /app/start.sh && \
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -XX:+UseG1GC"' >> /app/start.sh && \
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -Xms256m -Xmx1024m"' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start Tomcat' >> /app/start.sh && \
    echo 'echo "Tomcat server.xml port configuration:"' >> /app/start.sh && \
    echo 'grep -A 2 -B 2 "Connector.*PORT" /opt/tomcat/conf/server.xml || echo "PORT variable will be substituted at runtime"' >> /app/start.sh && \
    echo 'echo "Starting Tomcat on port $PORT..."' >> /app/start.sh && \
    echo 'exec /opt/tomcat/bin/catalina.sh run' >> /app/start.sh && \
    chmod +x /app/start.sh

# Expose port (Railway will override this with PORT env var)
EXPOSE 8080

# Use the startup script
ENTRYPOINT ["/app/start.sh"] 