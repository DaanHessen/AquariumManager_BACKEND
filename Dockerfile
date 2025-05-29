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

# Expose port
EXPOSE 8080

# Set environment variables for Tomcat
ENV CATALINA_HOME=/opt/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

# Create startup script that uses PORT environment variable
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'export CATALINA_OPTS="-Dserver.port=${PORT:-8080}"' >> /app/start.sh && \
    echo 'sed -i "s/8080/${PORT:-8080}/g" /opt/tomcat/conf/server.xml' >> /app/start.sh && \
    echo 'exec /opt/tomcat/bin/catalina.sh run' >> /app/start.sh && \
    chmod +x /app/start.sh

# Use the startup script
CMD ["/app/start.sh"] 