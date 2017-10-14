package myPackage.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
            for (Post body: bodyList) {
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
            }
            return 201;
        } catch (Exception e) {
            return 409;
        }
    }
}
