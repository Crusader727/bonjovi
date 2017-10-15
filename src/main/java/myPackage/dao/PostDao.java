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
                Post chuf = getPostById(body.getParent());
                if ((chuf == null && body.getParent() != 0) || (chuf != null && chuf.getThread() != body.getThread())) {
                    return 409;
                }
                template.update(con -> {
                    PreparedStatement pst = con.prepareStatement(
                            "insert into post(parent, threadid, isedited, owner, message, forum, created)"
                                    + " values(?,?,?,?,?,?,?::timestamptz)" + " returning id",
                            PreparedStatement.RETURN_GENERATED_KEYS);
                    pst.setLong(1, body.getParent());
                    pst.setLong(2, body.getThread());
                    pst.setBoolean(3, body.isIsedited());
                    pst.setString(4, body.getAuthor());
                    pst.setString(5, body.getMessage());
                    pst.setString(6, body.getForum());
                    pst.setString(7, body.getCreated());
                    return pst;
                }, keyHolder);
                body.setId(keyHolder.getKey().intValue());
                body.setCreated(bodyList.get(0).getCreated());
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

    private static final RowMapper<Post> POST_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        Long parent = res.getLong("parent");
        Long threadid = res.getLong("threadid");
        boolean isedited = res.getBoolean("isedited");
        String owner = res.getString("owner");
        String message = res.getString("message");
        String forum = res.getString("forum");
        Timestamp created = res.getTimestamp("created");
        return new Post(id, parent, threadid, isedited, owner, message, forum, created);
    };
}
