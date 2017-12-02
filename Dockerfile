FROM ubuntu:16.04
RUN apt-get -y update
ENV PGVER 9.5
RUN apt-get install -y postgresql-$PGVER
USER postgres

RUN /etc/init.d/postgresql start &&        psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" && createdb -E UTF8 -T template0 -O docker docker && /etc/init.d/postgresql stop

RUN echo "listen_addresses='*'\nsynchronous_commit = off		# synchronization level;\nwal_writer_delay = 2000ms		# 1-10000 milliseconds\nshared_buffers = 512MB			# min 128kB\neffective_cache_size = 1024MB\n" >> /etc/postgresql/$PGVER/main/postgresql.conf

EXPOSE 5432

VOLUME /etc/postgresql /var/log/postgresql /var/lib/postgresql
USER root
RUN apt-get install -y openjdk-8-jdk-headless
RUN apt-get install -y maven

ENV WORK /opt/
ADD / $WORK/
WORKDIR $WORK
RUN mvn package

EXPOSE 5000

CMD service postgresql start && java -Xms200M -Xmx200M -Xss256K -jar target/Db-1.0-SNAPSHOT.jar