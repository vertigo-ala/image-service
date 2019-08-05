#
# image-service is based on tomcat8 official image
# 
#
#FROM tomcat:8-jre8-alpine
FROM tomcat:8.5-jre8-alpine

RUN mkdir -p /data \
	/data/image-service \
	/data/image-service/config \
    /data/image-service/elasticsearch \
    /data/image-service-store/store \
    /data/image-service/incoming && \
    chmod +rw /data/image-service-store/store
#    /data/images/bin/imgcnv

#ARG ARTIFACT_URL=http://192.168.0.19/web/image-service-1.0.1.war
ARG ARTIFACT_URL=https://nexus.ala.org.au/service/local/repositories/releases/content/au/org/ala/image-service/1.0.5/image-service-1.0.5.war
ARG WAR_NAME=images
ENV IMAGE_SERVICE_BASE_URL http://localhost:8080/images

# Copia war para imagem
# ADD https://nexus.ala.org.au/service/local/repositories/releases/content/au/org/ala/ala-images/0.9/ala-images-0.9.war $CATALINA_HOME/webapps/images.war
# atencao para o fix de SLF4J
RUN wget $ARTIFACT_URL -q -O /tmp/$WAR_NAME && \
    apk add --update tini && \
    mkdir -p $CATALINA_HOME/webapps/$WAR_NAME && \
    unzip /tmp/$WAR_NAME -d $CATALINA_HOME/webapps/$WAR_NAME && \
    rm /tmp/$WAR_NAME

#    rm $CATALINA_HOME/webapps/images/WEB-INF/lib/slf4j-log4j12-*.jar && \

#    mv $CATALINA_HOME/webapps/$WAR_NAME $CATALINA_HOME/webapps/images


# FIX DO ERRO DE JS COM URL HARD-CODED PARA IMAGE-SERVER
#RUN unzip $CATALINA_HOME/webapps/$WAR_NAME plugins/images-client-plugin-0.8/js/ala-image-viewer.js -d /tmp/ && \
#    sed 's#    var imageServiceBaseUrl = .*#    var imageServiceBaseUrl = "https\://images\.ala-dev\.vertigo\.com\.br/images";#g' -i /tmp/plugins/images-client-plugin-0.8/js/ala-image-viewer.js && \
#    cd /tmp/ && \
#    zip -d $CATALINA_HOME/webapps/$WAR_NAME plugins/images-client-plugin-0.8/js/ala-image-viewer.js && \
#    zip -u $CATALINA_HOME/webapps/$WAR_NAME plugins/images-client-plugin-0.8/js/ala-image-viewer.js

# default DB is "jdbc:postgresql://pgdbimage/images"
# 0.9.x
#COPY ./data/images/config/images-config.properties /data/images/config/
# 1.x
COPY ./data/images/config/images-config.properties /data/image-service/config/image-service-config.properties

# Tomcat configs
COPY ./tomcat-conf/* /usr/local/tomcat/conf/	

EXPOSE 8080

# replace-string para corrigir domains (kubernetes)
#COPY ./scripts/* /opt/
#ENV ALA_REPLACE_FILES /data/images/config/images-config.properties
# muda entrypoint, mantém cmd
#ENTRYPOINT ["/opt/ala-entrypoint.sh","tini", "--"]

COPY ./tomcat-conf/logback.groovy $CATALINA_HOME/webapps/$WAR_NAME/WEB-INF/classes/logback.groovy

# NON-ROOT
RUN addgroup -g 101 tomcat && \
    adduser -G tomcat -u 101 -S tomcat && \
    chown -R tomcat:tomcat /usr/local/tomcat && \
    chown -R tomcat:tomcat /data

VOLUME /data/image-service-store /data/image-service/elasticsearch

USER tomcat

ENV CATALINA_OPTS '-Dgrails.env=production'

ENTRYPOINT ["tini", "--"]
CMD ["catalina.sh", "run"]
