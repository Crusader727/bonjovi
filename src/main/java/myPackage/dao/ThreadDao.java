package myPackage.dao;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import myPackage.models.Post;
import myPackage.models.SlugOrID;
import myPackage.models.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.*;

import myPackage.models.Thread;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.RowMapper;

@Service
public class ThreadDao {
    private final JdbcTemplate template;

    @Autowired
    public ThreadDao(JdbcTemplate template) {
        this.template = template;

    }

    public Integer[] createThread(Thread body) {
        Integer[] result = {0, 0};
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                PreparedStatement pst = con.prepareStatement(
                        "insert into thread(slug, forum, title, message, owner, votes, created)"
                                + " values(?,?,?,?,?,?,?::timestamptz)" + " returning tid",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getSlug());
                pst.setString(2, body.getForum());
                pst.setString(3, body.getTitle());
                pst.setString(4, body.getMessage());
                pst.setString(5, body.getAuthor());
                pst.setLong(6, body.getVotes());
                pst.setString(7, body.getCreated());
                return pst;
            }, keyHolder);
            result[0] = 201;
            result[1] = keyHolder.getKey().intValue();
            return result;
        } catch (DuplicateKeyException e) {
            result[0] = 409;
            return result;
        } catch (DataAccessException e) {
            result[0] = 404;
            return result;
        }
    }

    public Thread getThreadById(long id) {
        try {
            return template.queryForObject(
                    "SELECT * FROM thread WHERE tid = ?",
                    THREAD_MAPPER, id);
        } catch (DataAccessException e) {
            return null;
        }
    }


    public Integer getThreadIDbySlugOrID(SlugOrID key) {
        if (key.IsLong) {
            return template.queryForObject(
                    "SELECT tid FROM thread WHERE tid = ?",
                    Integer.class, key.id);
        } else {
            return template.queryForObject(
                    "SELECT tid FROM thread WHERE slug = ?::citext",
                    Integer.class, key.slug);

        }
    }

    public Thread getThreadbySlugOrID(SlugOrID key) {
        if (key.IsLong) {
            return template.queryForObject(
                    "SELECT * FROM thread WHERE tid = ?",
                    THREAD_MAPPER, key.id);
        } else {
            return template.queryForObject(
                    "SELECT * FROM thread WHERE slug = ?::citext",
                    THREAD_MAPPER, key.slug);

        }

    }


    public Thread getThreadBySlug(String slug) {
        try {
            return template.queryForObject(
                    "SELECT * FROM thread WHERE slug = ?::citext",
                    THREAD_MAPPER, slug);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public List<Thread> getThreads(Integer forumid, Integer limit, String since, Boolean desc) {
        List<Object> myObj = new ArrayList<>();
        final StringBuilder myStr = new StringBuilder("select * from thread where forumid = ? ");
        myObj.add(forumid);
        if (since != null) {
            if (desc) {
                myStr.append(" and created <= ?::timestamptz ");
            } else {
                myStr.append(" and created >= ?::timestamptz ");
            }
            myObj.add(since);
        }
        myStr.append(" order by created ");
        if (desc) {
            myStr.append(" desc ");
        }
        if (limit != null) {
            myStr.append(" limit ? ");
            myObj.add(limit);
        }
        return template.query(myStr.toString()
                , myObj.toArray(), THREAD_MAPPER);
    }


    public void vote(SlugOrID key, Vote vt) {
        if (key.IsLong) {
            template.update("INSERT INTO vote (userid, threadid, votes)  SELECT( SELECT id FROM users WHERE nickname = ?::citext) AS uid, ?, ?  ON CONFLICT (userid, threadid) DO UPDATE SET votes = EXCLUDED.votes;", vt.getNickname(), key.id, vt.getVoice());
        } else {
            template.update("INSERT INTO vote (userid, threadid, votes) VALUES ((SELECT id FROM users WHERE nickname = ?::citext), (SELECT tid FROM thread WHERE slug =  ?::citext),(?)) ON CONFLICT (userid, threadid)   DO UPDATE SET votes = EXCLUDED.votes;", vt.getNickname(), key.slug, vt.getVoice());
        }
    }


    public Integer chagenThread(Thread body) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                PreparedStatement pst = con.prepareStatement(
                        "update thread set" +
                                "  message = COALESCE(?, message)," +
                                "  title = COALESCE(?, title)" +
                                "where tid = ?",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getMessage());
                pst.setString(2, body.getTitle());
                pst.setLong(3, body.getId());
                return pst;
            }, keyHolder);
        } catch (Exception e) {
            return 409;
        }
        return 201;
    }

    //*****************************//
    public List<Post> getPosts(long threadId, Integer limit, Integer since, String sort, Boolean desc) {
        List<Object> myObj = new ArrayList<>();
        if (sort.equals("flat")) {
            StringBuilder myStr = new StringBuilder("select * from post where threadid = ?");
            myObj.add(threadId);
            if (since != null) {
                if (desc) {
                    myStr.append(" and id < ?");
                } else {
                    myStr.append(" and id > ?");
                }
                myObj.add(since);
            }
            myStr.append(" order by created ");
            if (desc) {
                myStr.append(" desc, id desc ");
            } else {
                myStr.append(", id");
            }
            if (limit != null) {
                myStr.append(" limit ? ");
                myObj.add(limit);
            }

            return template.query(myStr.toString()
                    , myObj.toArray(), POST_MAPPER);
        } else if (sort.equals("tree")) {
            StringBuilder myStr = new StringBuilder("select * from post where threadid = ?");
            myObj.add(threadId);
            if (since != null) {
                if (desc) {
                    myStr.append(" and path < (select path from post where id = ?) ");
                } else {
                    myStr.append(" and path > (select path from post where id = ?) ");
                }
                myObj.add(since);
            }
            myStr.append(" order by path ");
            if (desc) {
                myStr.append(" desc, id desc ");
            }
            if (limit != null) {
                myStr.append(" limit ? ");
                myObj.add(limit);
            }

            return template.query(myStr.toString()
                    , myObj.toArray(), POST_MAPPER);
        } else {

            StringBuilder myStr = new StringBuilder("select * from post join ");
            if (since != null) {
                if (desc) {
                    myStr.append(" (select id from post where parent = 0 and threadid = ? and path < (select path from post where id = ?)  order by path desc, threadid desc  limit ? ) as TT on threadid = ? and path[1] = TT.id ");

                } else {
                    myStr.append(" (select id from post where parent = 0 and threadid = ? and path > (select path from post where id = ?)  order by path , threadid  limit ? ) as TT on threadid = ? and path[1] = TT.id ");
                }
                myObj.add(threadId);
                myObj.add(since);
                myObj.add(limit);
                myObj.add(threadId);
            } else if (limit != null) {
                if (desc) {
                    myStr.append(" (select id  from post where parent = 0 and threadid = ? order by path desc, threadid desc limit ? ) as TT on threadid = ? and path[1] = TT.id ");
                } else {
                    myStr.append(" (select id  from post where parent = 0 and threadid = ? order by path , threadid  limit ? ) as TT on threadid = ? and path[1] = TT.id ");
                }
                myObj.add(threadId);
                myObj.add(limit);
                myObj.add(threadId);
            }
            myStr.append(" order by path ");
            if (desc) {
                myStr.append(" desc ");
            }
            myStr.append(" ,threadid ");
            if (desc) {
                myStr.append(" desc ");
            }
            return template.query(myStr.toString()
                    , myObj.toArray(), POST_MAPPER);
        }

    }
    //*****************************//

    private static final RowMapper<Thread> THREAD_MAPPER = (res, num) -> {
        long votes = res.getLong("votes");
        Long id = res.getLong("tid");
        Long forumid = res.getLong("forumid");
        String slug = res.getString("slug");
        String owner = res.getString("owner");
        String forum = res.getString("forum");
        Timestamp created = res.getTimestamp("created");
        String message = res.getString("message");
        String title = res.getString("title");
        return new Thread(slug, forum, title, message, owner, id, votes, created, forumid);
    };
    private static final RowMapper<Vote> VOTE_MAPPER = (res, num) -> {
        long votes = res.getLong("votes");
        long id = res.getLong("id");
        Integer threadid = res.getInt("threadid");
        Integer userid = res.getInt("userid");
        return new Vote(id, userid, null, votes, threadid);
    };
    private static final RowMapper<Post> POST_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        Long forumid = res.getLong("forumid");
        Long parent = res.getLong("parent");
        Long threadid = res.getLong("threadid");
        boolean isedited = res.getBoolean("isedited");
        String owner = res.getString("owner");
        String message = res.getString("message");
        String forum = res.getString("forum");
        Array path = res.getArray("path");
        Timestamp created = res.getTimestamp("created");
        return new Post(id, forumid, parent, threadid, isedited, owner, message, forum, created, (Object[]) path.getArray());
    };
}
