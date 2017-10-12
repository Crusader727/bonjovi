package myPackage.dao;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.dao.*;

import myPackage.models.Thread;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.RowMapper;

@Service
@Transactional
public class ThreadDao {
    private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate namedTemplate;

    public ThreadDao(JdbcTemplate template, NamedParameterJdbcTemplate namedTemplate) {
        this.template = template;
        this.namedTemplate = namedTemplate;
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

    public Thread getThreadById(Integer id) {
        try {
            final Thread th = template.queryForObject(
                    "SELECT * FROM thread WHERE tid = ?",
                    new Object[]{id}, THREAD_MAPPER);
            return th;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public Thread getThreadBySlug(String slug) {
        try {
            final Thread th = template.queryForObject(
                    "SELECT * FROM thread WHERE lower(slug) = lower(?)",
                    new Object[]{slug}, THREAD_MAPPER);
            return th;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public Object[] getThreads(String forum, Integer limit, String since, Boolean desc) {
        try {
            List<Object> myObj = new ArrayList<>();
            String myStr = "select * from thread where lower(forum) = lower(?) ";
            myObj.add(forum);
            if (since != null) {
                if (desc != null && desc) {
                    myStr += " and created <= ?::timestamptz ";
                } else {
                    myStr += " and created >= ?::timestamptz ";
                }
                myObj.add(since);
            }
            myStr += " order by created ";
            if (desc != null && desc) {
                myStr += " desc ";
            }
            if(limit != null) {
                myStr += " limit ? ";
                myObj.add(limit);
            }
            List<Thread> result = template.query(myStr
                    , myObj.toArray(), THREAD_MAPPER);
            return result.toArray();
        } catch (DataAccessException e) {
            return null;
        }
    }

    private static final RowMapper<Thread> THREAD_MAPPER = (res, num) -> {
        long votes = res.getLong("votes");
        Long id = res.getLong("tid");
        String slug = res.getString("slug");
        String owner = res.getString("owner");
        String forum = res.getString("forum");
        String created = res.getString("created");
        String message = res.getString("message");
        String title = res.getString("title");
        return new Thread(slug, forum, title, message, owner, id, votes, created);
    };
}
