package myPackage.dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import myPackage.models.Forum;
import myPackage.models.User;
import org.springframework.dao.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;

@Service
@Transactional
public class ForumDao {
    private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate namedTemplate;

    public ForumDao(JdbcTemplate template, NamedParameterJdbcTemplate namedTemplate) {
        this.template = template;
        this.namedTemplate = namedTemplate;
    }

    public Integer createForum(Forum body) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                PreparedStatement pst = con.prepareStatement(
                        "insert into forum(slug, title, owner, threadcount, postcount)"
                                + " values(?,?,?,?,?)",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getSlug());
                pst.setString(2, body.getTitle());
                pst.setString(3, body.getUser());
                pst.setLong(4, body.getThreads());
                pst.setLong(5, body.getPosts());
                return pst;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            return 409;
        } catch (DataAccessException e) {
            return 404;
        }
        return 201;
    }

    public Forum getForum(String slug) {
        try {
            final Forum fr = template.queryForObject(
                    "SELECT * FROM forum WHERE LOWER(slug) = LOWER(?)",
                    new Object[]{slug}, FORUM_MAPPER);
            return fr;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public void updateForum(String slug) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                PreparedStatement pst = con.prepareStatement(
                        "update forum set " +
                                "postCount = COALESCE((select count(*) from post where lower(forum) = lower(?)), postCount), " +
                                "threadCount = COALESCE((select count(*) from thread where lower(forum) = lower(?)), threadCount) " +
                                "where lower(slug) = lower(?);",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, slug);
                pst.setString(2, slug);
                pst.setString(3, slug);
                return pst;
            }, keyHolder);
        } catch (DataAccessException e) {
        }
    }

    public Object[] getUsers(String forum, Integer limit, String since, Boolean desc) {
        try {
            List<Object> myObj = new ArrayList<>();
            String myStr = "select DISTINCT * from (select DISTINCT u1.* from users u1 JOIN post p1 on (lower(p1.forum) = lower(?) and lower(u1.nickname) = lower(p1.owner)) " +
                    "UNION " +
                    "select DISTINCT u2.* from users u2 JOIN thread t1 on (lower(t1.forum) = lower(?) and lower(u2.nickname) = lower(t1.owner))) as ff ";
            myObj.add(forum);
            myObj.add(forum);
            if (since != null) {
                if (desc != null && desc) {
                    myStr += " where ff.nickname < ?::citext ";
                } else {
                    myStr += " where ff.nickname > ?::citext ";
                }
                myObj.add(since);
            }
            myStr += " order by ff.nickname ";
            if (desc != null && desc) {
                myStr += " desc ";
            }
            if (limit != null) {
                myStr += " limit ? ";
                myObj.add(limit);
            }
            List<User> result = template.query(myStr
                    , myObj.toArray(), USER_MAPPER);
            return result.toArray();
        } catch (DataAccessException e) {
            System.out.println("da acses in get users");
            return null;
        }
    }

    private static final RowMapper<Forum> FORUM_MAPPER = (res, num) -> {
        String slug = res.getString("slug");
        String title = res.getString("title");
        Long postCount = res.getLong("postCount");
        Long threadCount = res.getLong("threadCount");
        String owner = res.getString("owner");
        return new Forum(slug, title, owner, postCount, threadCount);
    };

    private static final RowMapper<User> USER_MAPPER = (res, num) -> {
        String nickname = res.getString("nickname");
        String email = res.getString("email");
        String fullname = res.getString("fullname");
        String about = res.getString("about");
        if (res.wasNull()) {
            about = null;
        }
        return new User(nickname, about, email, fullname);
    };
}
