package springbook.user.dao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import springbook.user.domain.User;

import javax.sql.DataSource;


public class UserDao {

    private JdbcTemplate jdbcTemplate;

    public UserDao() {}

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(User user) {
        jdbcTemplate.update("INSERT INTO users(id, name, password) VALUES(?,?,?);",
                user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) throws EmptyResultDataAccessException {
        String[] ids = {id};
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id=?", ids,
                (rs, i) -> {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user; });
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM users");
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    }

}
