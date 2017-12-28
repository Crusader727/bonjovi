package myPackage.dao;


import myPackage.models.Details;
import myPackage.models.Forum;
import myPackage.models.Post;
import myPackage.models.Thread;
import myPackage.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;

//@Transactional
@Service
public class ServiceDao {
    private final JdbcTemplate template;

    @Autowired
    public ServiceDao(JdbcTemplate template) {
        this.template = template;

    }


    //    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
    public myPackage.models.Service getInfo() {
        return new myPackage.models.Service(template.queryForObject(
                "select count(*) from users;",
                new Object[]{}, Long.class), template.queryForObject(
                "select count(*) from post;",
                new Object[]{}, Long.class), template.queryForObject(
                "select count(*) from forum;",
                new Object[]{}, Long.class), template.queryForObject(
                "select count(*) from thread;",
                new Object[]{}, Long.class));
    }

    public void truncateDB() {
        template.update(
                "truncate post, forum, thread, users, vote CASCADE;"
        );
    }

//    @Transactional
    /*public Details getPostFull(String[] related, Post buf) {
        Details dt = new Details(null, null, buf, null);
        if (related != null) {
            if (Arrays.asList(related).contains("user")) {
                dt.setAuthor(template.queryForObject(
                        "SELECT * FROM users WHERE nickname = ?::citext;",
                        USER_MAPPER, buf.getAuthor()));
            }
            if (Arrays.asList(related).contains("forum")) {
                dt.setForum(template.queryForObject(
                        "SELECT * FROM forum WHERE id = ?;",
                        FORUM_MAPPER, buf.getForumid()));
            }
            if (Arrays.asList(related).contains("thread")) {
                dt.setThread(template.queryForObject(
                        "SELECT * FROM thread WHERE tid = ?",
                        THREAD_MAPPER, buf.getThread()));
            }
        }
        return dt;
    }*/

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
