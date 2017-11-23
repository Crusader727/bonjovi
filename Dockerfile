FROM ubuntu:16.04
RUN apt-get -y update
ENV PGVER 9.5
RUN apt-get install -y postgresql-$PGVER
USER postgres

RUN /etc/init.d/postgresql start &&        psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" && createdb -E UTF8 -T template0 -O docker docker && /etc/init.d/postgresql stop

RUN echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/$PGVER/main/pg_hba.conf
RUN echo "listen_addresses='*'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "synchronous_commit=off" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "fsync = 'off'" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "max_wal_size = 1GB" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "shared_buffers = 128MB" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "effective_cache_size = 256MB" >> /etc/postgresql/$PGVER/main/postgresql.conf
RUN echo "work_mem = 64MB" >> /etc/postgresql/$PGVER/main/postgresql.conf

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

CMD service postgresql start && java -Xmx512M -Xmx512M -jar target/Db-1.0-SNAPSHOT.jar