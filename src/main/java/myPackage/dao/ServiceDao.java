package myPackage.dao;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceDao {
    private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate namedTemplate;

    public ServiceDao(JdbcTemplate template, NamedParameterJdbcTemplate namedTemplate) {
        this.template = template;
        this.namedTemplate = namedTemplate;
    }


//    @Transactional(isolation = Isolation.READ_COMMITTED)// TODO UNCOMMEnt
    public myPackage.models.Service getInfo() {
        return new myPackage.models.Service(template.queryForObject(
                "select count(*) from users;",
                new Object[]{}, Long.class) , template.queryForObject(
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
}
