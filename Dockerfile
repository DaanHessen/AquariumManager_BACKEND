# Simple Tomcat runtime
FROM tomcat:10.1-jre17

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Remove default webapps and copy our WAR as ROOT
RUN rm -rf /usr/local/tomcat/webapps/*
COPY target/aquarium-api.war /usr/local/tomcat/webapps/ROOT.war

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"] 