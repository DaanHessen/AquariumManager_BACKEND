FROM tomcat:10.1-jre17

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN rm -rf /usr/local/tomcat/webapps/*
COPY target/aquarium-api.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
