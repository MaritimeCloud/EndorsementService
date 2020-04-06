# Run like this:
# sudo docker run -t -i --rm -p 8884:8884 -v /path/to/config-directory/on/machine:/conf <image-id>
# 
# A customized conf file (application.yml) must be available in the folder mounted to /conf.

FROM openjdk:8-jre

RUN mkdir -p /conf
ADD mc-endorsement-service-0.9.0-SNAPSHOT.war .

# Set locales for image to support UTF-8
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

EXPOSE 8870

CMD ["java", "-Dspring.profiles.active=docker", "-jar", "mc-endorsement-service-0.9.0-SNAPSHOT.war", "--spring.config.location=/conf/application.yml", "--keycloak.configurationFile=file:/conf/keycloak.json"]
