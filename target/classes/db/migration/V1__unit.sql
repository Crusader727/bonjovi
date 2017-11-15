CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE users (
  id       SERIAL PRIMARY KEY,
  nickname CITEXT UNIQUE NOT NULL,
  fullname TEXT          NOT NULL,
  email    CITEXT        NOT NULL UNIQUE,
  about    TEXT
);
CREATE INDEX IF NOT EXISTS users_nickname_id
  ON users (lower(nickname), id);
CREATE UNIQUE INDEX IF NOT EXISTS users_nickname
  ON users (lower(nickname));
CREATE UNIQUE INDEX IF NOT EXISTS users_email
  ON users (email);
CREATE INDEX IF NOT EXISTS users_nickname_email
  ON users (nickname, email);

CREATE TABLE forum (
  slug        CITEXT PRIMARY KEY,
  title       TEXT NOT NULL,
  postCount   BIGINT,
  threadCount BIGINT,
  owner       CITEXT REFERENCES users (nickname)
);


CREATE UNIQUE INDEX IF NOT EXISTS forum_slug
  ON forum (lower(slug));
CREATE UNIQUE INDEX IF NOT EXISTS forum_slug_owner
  ON forum (lower(slug), lower(owner));

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


CREATE UNIQUE INDEX IF NOT EXISTS thread_slug
  ON thread (lower(slug));
CREATE UNIQUE INDEX IF NOT EXISTS thread_slug_id
  ON thread (lower(slug), tid);
CREATE INDEX IF NOT EXISTS thread_forum
  ON thread (forum);

CREATE INDEX IF NOT EXISTS thread_created
  ON thread (created);

CREATE INDEX IF NOT EXISTS thread_forum_created
  ON thread (forum, created);

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
  id       SERIAL PRIMARY KEY,
  userid   INTEGER NOT NULL REFERENCES users (id),
  threadid INTEGER NOT NULL REFERENCES thread (tid),
  votes    INT
);

CREATE UNIQUE INDEX IF NOT EXISTS vote_user_thread
  ON vote (userid, threadid);

CREATE OR REPLACE FUNCTION vote()
  RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  IF (TG_OP = 'INSERT')
  THEN
    UPDATE thread
    SET votes = votes + new.votes
    WHERE thread.tid = new.threadid;
    RETURN new;
  ELSE
    IF new.votes != old.votes
    THEN
      UPDATE thread
      SET votes = votes + (new.votes * 2)
      WHERE thread.tid = new.threadid;
      RETURN new;
    END IF;
    RETURN new;
  END IF;
END;
$$;

DROP TRIGGER IF EXISTS vote_trigger
ON vote;

CREATE TRIGGER vote_trigger
AFTER INSERT OR UPDATE
  ON vote
FOR EACH ROW
EXECUTE PROCEDURE vote();







