package myPackage.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import myPackage.models.Forum;
import myPackage.models.Post;
import myPackage.models.User;
import org.springframework.dao.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;

@Service
@Transactional
public class PostDao {
    private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate namedTemplate;

    public PostDao(JdbcTemplate template, NamedParameterJdbcTemplate namedTemplate) {
        this.template = template;
        this.namedTemplate = namedTemplate;
    }

    public Integer createPosts(ArrayList<Post> bodyList) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            for (Post body : bodyList) {
                body.setCreated(bodyList.get(0).getCreated());
                Post chuf = getPostById(body.getParent());
                if ((chuf == null && body.getParent() != 0) || (chuf != null && chuf.getThread() != body.getThread())) {
                    return 409;
                }
                ///NEED TO CREATE PATH
                template.update(con -> {
                    PreparedStatement pst = con.prepareStatement(
                            "insert into post(parent, threadid, isedited, owner, message, forum, created)"
                                    + " values(?,?,?,?,?,?,?::timestamptz)" + " returning id",
                            PreparedStatement.RETURN_GENERATED_KEYS);
                    pst.setLong(1, body.getParent());
                    pst.setLong(2, body.getThread());
                    pst.setBoolean(3, body.getIsEdited());
                    pst.setString(4, body.getAuthor());
                    pst.setString(5, body.getMessage());
                    pst.setString(6, body.getForum());
                    pst.setString(7, body.getCreated());
                    return pst;
                }, keyHolder);
                body.setId(keyHolder.getKey().intValue());
                setPostsPath(chuf, body);
            }
            return 201;
        } catch (Exception e) {
            return 404;
        }
    }


    public Post getPostById(long id) {
        try {
            final Post pst = template.queryForObject(
                    "SELECT * FROM post WHERE id = ?",
                    new Object[]{id}, POST_MAPPER);
            return pst;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public void changePost(Post body) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    "update post set" +
                            "  message = COALESCE(?, message), " +
                            "  isedited = COALESCE(true, isedited) " +
                            "where id = ?",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, body.getMessage());
            pst.setLong(2, body.getId());
            return pst;
        }, keyHolder);
    }

    public void setPostsPath(Post chuf, Post body) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            PreparedStatement pst = con.prepareStatement(
                    "update post set" +
                            "  path = ? " +
                            "where id = ?",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            if (body.getParent() == 0) {
                pst.setString(1, String.valueOf(body.getId()));
            } else {
                pst.setString(1, chuf.getPath() + "-" + String.valueOf(body.getId()));
            }
            pst.setLong(2, body.getId());
            return pst;
        }, keyHolder);

    }
    private static final RowMapper<Post> POST_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        Long parent = res.getLong("parent");
        Long threadid = res.getLong("threadid");
        boolean isedited = res.getBoolean("isedited");
        String owner = res.getString("owner");
        String message = res.getString("message");
        String forum = res.getString("forum");
        String path = res.getString("path");
        Timestamp created = res.getTimestamp("created");
        return new Post(id, parent, threadid, isedited, owner, message, forum, created, path);
    };
}
