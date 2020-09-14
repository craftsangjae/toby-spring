package springbook.user.dao;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;
import springbook.user.exception.DuplicateUserIdException;

import java.util.List;


public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userMapper = (resultSet, i) -> {
        User user = new User();
        user.setId(resultSet.getString("id"));
        user.setName(resultSet.getString("name"));
        user.setPassword(resultSet.getString("password"));
        return user;};

    public UserDaoJdbc() {}

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(User user)  throws DuplicateUserIdException {
        try {
            jdbcTemplate.update("INSERT INTO users(id, name, password) VALUES(?,?,?);",
                    user.getId(), user.getName(), user.getPassword());
        } catch(DuplicateKeyException e) {
            throw new DuplicateUserIdException(e);
        }
    }

    public User get(String id) throws EmptyResultDataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id=?", new String[] {id}, userMapper);}

    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users order by id", userMapper);}

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM users");
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    }

}