package myPackage.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import myPackage.models.Forum;
import myPackage.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.RowMapper;

@Service
public class ForumDao {
    private final JdbcTemplate template;

    @Autowired
    public ForumDao(JdbcTemplate template) {
        this.template = template;
    }

    //    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
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

    //    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
    public Forum getForum(String slug) {
        try {
            final Forum fr = template.queryForObject(
                    "SELECT * FROM forum WHERE slug = ?::citext",
                    new Object[]{slug}, FORUM_MAPPER);
            return fr;
        } catch (DataAccessException e) {
            return null;
        }
    }

    //    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
    public Forum getForumById(Long id) {
        try {
            final Forum fr = template.queryForObject(
                    "SELECT * FROM forum WHERE id = ?;",
                    new Object[]{id}, FORUM_MAPPER);
            return fr;
        } catch (DataAccessException e) {
            return null;
        }
    }

    //    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
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


    //    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
    public Object[] getUsers(Long forumid, Integer limit, String since, Boolean desc) {
        try {
            List<Object> myObj = new ArrayList<>();
            StringBuilder myStr = new StringBuilder("SELECT id, nickname, fullname, email, about from users_on_forum  WHERE forumid = ? ");
            myObj.add(forumid);
            if (since != null) {
                if (desc != null && desc) {
                    myStr.append(" AND nickname < ?::citext ");
                } else {
                    myStr.append( " AND nickname > ?::citext ");
                }
                myObj.add(since);
            }
            myStr.append( " order by nickname ");
            if (desc != null && desc) {
                myStr.append( " desc ");
            }
            if (limit != null) {
                myStr.append( " limit ? ");
                myObj.add(limit);
            }
            List<User> result = template.query(myStr.toString()
                    , myObj.toArray(), USER_MAPPER);
            return result.toArray();
        } catch (DataAccessException e) {
            return null;
        }
    }

    private static final RowMapper<Forum> FORUM_MAPPER = (res, num) -> {
        String slug = res.getString("slug");
        String title = res.getString("title");
        Long postCount = res.getLong("postCount");
        Long id = res.getLong("id");
        Long threadCount = res.getLong("threadCount");
        String owner = res.getString("owner");
        return new Forum(id, slug, title, owner, postCount, threadCount);
    };

    private static final RowMapper<User> USER_MAPPER = (res, num) -> {
        long id = res.getLong("id");
        String nickname = res.getString("nickname");
        String email = res.getString("email");
        String fullname = res.getString("fullname");
        String about = res.getString("about");
        if (res.wasNull()) {
            about = null;
        }
        return new User(id, nickname, about, email, fullname);
    };
}
