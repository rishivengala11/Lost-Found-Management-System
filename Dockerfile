FROM tomcat:9.0-jdk8

# Remove default Tomcat apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy your project into ROOT (so it runs directly)
COPY . /usr/local/tomcat/webapps/ROOT

# Expose port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]