CREATE EXTENSION IF NOT EXISTS citext;
CREATE TABLE users (
  nickname CITEXT PRIMARY KEY,
  fullname TEXT   NOT NULL,
  email    CITEXT NOT NULL UNIQUE,
  about    TEXT
);
CREATE TABLE forum (
  slug        CITEXT PRIMARY KEY,
  title       TEXT NOT NULL,
  postCount   BIGINT,
  threadCount BIGINT,
  owner       CITEXT REFERENCES users (nickname)
);
CREATE TABLE thread (
  tid     SERIAL PRIMARY KEY,
  slug    CITEXT UNIQUE,
  owner   CITEXT REFERENCES users (nickname),
  forum   CITEXT REFERENCES forum (slug),
  created TIMESTAMP WITH TIME ZONE,
  message TEXT NOT NULL,
  title   TEXT NOT NULL,
  votes   BIGINT
);

CREATE TABLE post (
  id       SERIAL PRIMARY KEY,
  parent   INTEGER DEFAULT 0,
  owner    CITEXT REFERENCES users (nickname),
  message  TEXT,
  isedited BOOLEAN,
  forum    CITEXT REFERENCES forum (slug),
  created  TIMESTAMP WITH TIME ZONE,
  threadid INTEGER REFERENCES thread (tid),
  path     INT []
);
CREATE TABLE vote (
  nickname CITEXT REFERENCES users (nickname),
  threadid INTEGER REFERENCES thread (tid),
  votes    INT
);