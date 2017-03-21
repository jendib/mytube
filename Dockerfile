FROM sismics/debian-java:7.79.1
MAINTAINER Benjamin Gamard <benjamin.gam@gmail.com>

RUN apt-get update \
  && apt-get install -y supervisor apache2 cron \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/*

ENV APACHE_RUN_USER www-data
ENV APACHE_RUN_GROUP www-data
ENV APACHE_LOG_DIR /var/log/apache2
ENV APACHE_PID_FILE /var/run/apache2.pid
ENV APACHE_RUN_DIR /var/run/apache2
ENV APACHE_LOCK_DIR /var/lock/apache2

RUN mkdir -p /var/lock/apache2

EXPOSE 80

COPY supervisord.conf /etc/supervisor/supervisord.conf
COPY crontab /etc/crontab

RUN rm -fr /var/www/html/*
RUN sed -i "s/AllowOverride None/AllowOverride All/" /etc/apache2/apache2.conf
COPY src/main/resources/client /var/www/html
COPY target/mytube-*.jar /root/mytube.jar

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/supervisord.conf"]
