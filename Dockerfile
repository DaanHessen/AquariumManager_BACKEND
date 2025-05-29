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

# Create a simple startup script that handles the PORT variable correctly
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'set -e' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Set the port for Tomcat - Railway provides PORT environment variable' >> /app/start.sh && \
    echo 'export CATALINA_PORT=${PORT:-8080}' >> /app/start.sh && \
    echo 'echo "Starting Aquarium API on port $CATALINA_PORT..."' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Replace the default port in server.xml with the Railway PORT' >> /app/start.sh && \
    echo 'sed -i "s/port=\"8080\"/port=\"$CATALINA_PORT\"/g" /opt/tomcat/conf/server.xml' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Set JVM options for Railway deployment' >> /app/start.sh && \
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -Djava.awt.headless=true"' >> /app/start.sh && \
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -Djava.security.egd=file:/dev/./urandom"' >> /app/start.sh && \
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -XX:+UseG1GC"' >> /app/start.sh && \
    echo 'export CATALINA_OPTS="$CATALINA_OPTS -Xms256m -Xmx1024m"' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start Tomcat' >> /app/start.sh && \
    echo 'echo "Starting Tomcat on port $CATALINA_PORT..."' >> /app/start.sh && \
    echo 'exec /opt/tomcat/bin/catalina.sh run' >> /app/start.sh && \
    chmod +x /app/start.sh

# Expose port (Railway will override this with PORT env var)
EXPOSE 8080

# Use the startup script
ENTRYPOINT ["/app/start.sh"] 