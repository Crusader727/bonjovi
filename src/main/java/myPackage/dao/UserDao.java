package myPackage.dao;

import java.sql.PreparedStatement;

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
public class UserDao {
    private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate namedTemplate;

    public UserDao(JdbcTemplate template, NamedParameterJdbcTemplate namedTemplate) {
        this.template = template;
        this.namedTemplate = namedTemplate;
    }

    public Integer createUser(User body) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                PreparedStatement pst = con.prepareStatement(
                        "insert into users(nickname, about, email, fullname)"
                                + " values(?,?,?,?)",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getNickname());
                pst.setString(2, body.getAbout());
                pst.setString(3, body.getEmail());
                pst.setString(4, body.getFullname());
                return pst;
            }, keyHolder);
        } catch (Exception e) {
            return 409;
        }
        return 201;
    }

    public Integer changeUser(User body) {
        if (getUserByNick(body.getNickname()) == null) {
            return 404;
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(con -> {
                PreparedStatement pst = con.prepareStatement(
                        "update users set" +
                                "  fullname = COALESCE(?, fullname)," +
                                "  about = COALESCE(?, about)," +
                                "  email = COALESCE(?, email)" +
                                "where LOWER(nickname) = LOWER(?)",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                pst.setString(1, body.getFullname());
                pst.setString(2, body.getAbout());
                pst.setString(3, body.getEmail());
                pst.setString(4, body.getNickname());
                return pst;
            }, keyHolder);
        } catch (Exception e) {
            return 409;
        }
        return 201;
    }

    public User getUserByNick(String nickname) {
        try {
            final User fr = template.queryForObject(
                    "SELECT * FROM users WHERE lower(nickname) = LOWER(?)",
                    new Object[]{nickname}, USER_MAPPER);
            return fr;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public User getUserByEmail(String mail) {
        try {
            final User fr = template.queryForObject(
                    "SELECT * FROM users WHERE LOWER(email) = LOWER(?)",
                    new Object[]{mail}, USER_MAPPER);
            return fr;
        } catch (DataAccessException e) {
            return null;
        }
    }


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
