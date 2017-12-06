package myPackage.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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
