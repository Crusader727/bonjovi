package myPackage.dao;

import java.sql.PreparedStatement;
import java.util.List;

import myPackage.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;

@Transactional
@Service
public class UserDao {
    private final JdbcTemplate template;

    @Autowired
    public UserDao(JdbcTemplate template) {
        this.template = template;

    }

//    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
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

//    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
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

//    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
    public User getUserByNick(String nickname) {
        try {
            return template.queryForObject(
                    "SELECT * FROM users WHERE nickname = ?::citext;",
                     USER_MAPPER, nickname);
        } catch (DataAccessException e) {
            return null;
        }
    }

//    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
    public User getUserIDbyNick(String nickname) {
        try {
            final User fr = template.queryForObject(
                    "SELECT id FROM users WHERE nickname = ?::citext;",
                    new Object[]{nickname}, USER_MAPPER_ID);
            return fr;

        } catch (DataAccessException e) {
            return null;
        }
    }

//    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
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
    private static final RowMapper<User> USER_MAPPER_ID = (res, num) -> {
        long id = res.getLong("id");
        return new User(id, null, null, null, null);
    };
}
