
CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE users (
        id       SERIAL PRIMARY KEY,
        nickname CITEXT UNIQUE NOT NULL,
        fullname TEXT          NOT NULL,
        email    CITEXT        NOT NULL UNIQUE,
        about    TEXT
);
-- CREATE INDEX IF NOT EXISTS users_nickname_id
--         ON users (lower(nickname), id);
-- CREATE UNIQUE INDEX IF NOT EXISTS users_nickname
--         ON users (lower(nickname));


CREATE TABLE forum (
        id     SERIAL PRIMARY KEY,
        slug        CITEXT UNIQUE NOT NULL ,
        title       TEXT NOT NULL,
        postCount   BIGINT,
        threadCount BIGINT,
        owner       CITEXT REFERENCES users (nickname)
);

--
-- CREATE UNIQUE INDEX IF NOT EXISTS forum_slug
--         ON forum (lower(slug));
-- CREATE UNIQUE INDEX IF NOT EXISTS forum_slug_id
--         ON forum (lower(slug), id);
-- CREATE UNIQUE INDEX IF NOT EXISTS forum_slug_owner
--         ON forum (id, lower(owner));

CREATE TABLE thread (
        tid     SERIAL PRIMARY KEY,
        slug    CITEXT UNIQUE,
        owner   CITEXT REFERENCES users (nickname),
        forum   CITEXT REFERENCES forum (slug),
        forumid INTEGER ,
        created TIMESTAMP WITH TIME ZONE,
        message TEXT NOT NULL,
        title   TEXT NOT NULL,
        votes   BIGINT
);

-- CREATE UNIQUE INDEX IF NOT EXISTS thread_slug
--         ON thread (lower(slug));
-- CREATE UNIQUE INDEX IF NOT EXISTS thread_id_slug
--         ON thread (tid, lower(slug));
-- CREATE INDEX IF NOT EXISTS thread_forum_owner
--         ON thread (lower(owner), forumid);

--
-- CREATE INDEX IF NOT EXISTS thread_forum
--         ON thread (forumid);
CREATE INDEX IF NOT EXISTS thread_forum_created
        ON thread (forumid, created);

CREATE TABLE post (
        id       SERIAL PRIMARY KEY,
        parent   INTEGER DEFAULT 0,
        owner    CITEXT REFERENCES users (nickname),
        message  TEXT,
        isedited BOOLEAN,
        forum    CITEXT REFERENCES forum (slug),
        created  TIMESTAMP WITH TIME ZONE,
        threadid INTEGER REFERENCES thread (tid),
        forumid INTEGER ,
        path     INT []
);

-- CREATE INDEX IF NOT EXISTS post_id
--         ON post (threadid, id);

CREATE INDEX IF NOT EXISTS post_id_path
        ON post (threadid, path);



CREATE TABLE vote (
        id       SERIAL PRIMARY KEY,
        userid   INTEGER NOT NULL REFERENCES users (id),
        threadid INTEGER NOT NULL REFERENCES thread (tid),
        votes    INT
);

-- CREATE UNIQUE INDEX IF NOT EXISTS vote_user_thread
--         ON vote (userid, threadid);

-- for getUsers
CREATE TABLE users_on_forum (
        id       SERIAL PRIMARY KEY,
        nickname CITEXT,
        fullname TEXT,
        email    CITEXT,
        about    TEXT,
        forumid     INTEGER,
        UNIQUE (forumid, nickname)
);
CREATE UNIQUE INDEX IF NOT EXISTS index_users_on_forum
        ON users_on_forum (forumid, lower(nickname));


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

-- vote ended
-- for forum details

CREATE OR REPLACE FUNCTION forum_threads_inc()
        RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
        new.forumid = (SELECT id from forum where lower(forum.slug) = lower(new.forum));
        UPDATE forum
        SET threadCount = threadCount + 1
        WHERE forum.id = new.forumid;
        INSERT INTO users_on_forum (nickname, forumid, fullname, email, about)
                (SELECT
                         new.owner,
                         new.forumid,
                         u.fullname,
                         u.email,
                         u.about
                 FROM users u
                 WHERE lower(new.owner) = lower(u.nickname))
        ON CONFLICT DO NOTHING;

        RETURN new;
END;
$$;

DROP TRIGGER IF EXISTS t_forum_threads_inc
ON thread;

CREATE TRIGGER t_forum_threads_inc
BEFORE INSERT
        ON thread
FOR EACH ROW
EXECUTE PROCEDURE forum_threads_inc();


CREATE OR REPLACE FUNCTION forum_posts_inc()
        RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
        UPDATE forum
        SET postCount = postCount + 1
        WHERE forum.id = new.forumid;
        INSERT INTO users_on_forum (nickname, forumid, fullname, email, about)
                (SELECT
                         new.owner,
                         new.forumid,
                         u.fullname,
                         u.email,
                         u.about
                 FROM users u
                 WHERE lower(new.owner) = lower(u.nickname))
        ON CONFLICT DO NOTHING;
        RETURN new;
END;
$$;

DROP TRIGGER IF EXISTS t_forum_posts_inc
ON post;

CREATE TRIGGER t_forum_posts_inc
BEFORE INSERT
        ON post
FOR EACH ROW
EXECUTE PROCEDURE forum_posts_inc();


