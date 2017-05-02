# Run like this:
# sudo docker run -t -i --rm -p 8884:8884 -v /path/to/config-directory/on/machine:/conf <image-id>
# 
# A customized conf file (application.yml) must be available in the folder mounted to /conf.

FROM java:8

RUN mkdir -p /conf
ADD mc-endorsement-service-0.5.0-SNAPSHOT.war .

EXPOSE 8870

CMD ["java", "-Dspring.profiles.active=docker", "-jar", "mc-endorsement-service-0.5.0-SNAPSHOT.war", "--spring.config.location=/conf/application.yml", "--keycloak.config.file=/conf/keycloak.conf"]
