package springbook.user.dao;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.exception.DuplicateUserIdException;

import javax.sql.DataSource;
import java.util.List;


public class UserDaoJdbc implements UserDao {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userMapper = (resultSet, i) -> {
        User user = new User();
        user.setId(resultSet.getString("id"));
        user.setName(resultSet.getString("name"));
        user.setPassword(resultSet.getString("password"));
        user.setLevel(Level.valueOf(resultSet.getInt("level")));
        user.setLogin(resultSet.getInt("login"));
        user.setRecommend(resultSet.getInt("recommend"));
        user.setEmail(resultSet.getString("email"));
        return user;};

    public UserDaoJdbc(){}

    public UserDaoJdbc(DataSource dataSource) { this.dataSource = dataSource; }

    public void setDataSource(DataSource dataSource) { jdbcTemplate = new JdbcTemplate(dataSource);}

    public void add(User user)  throws DuplicateUserIdException {
        try {
            jdbcTemplate.update(
                    "INSERT INTO users(id, name, password, level, login, recommend, email) VALUES(?,?,?,?,?,?, ?);",
                    user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(),
                    user.getLogin(), user.getRecommend(), user.getEmail());
        } catch(DuplicateKeyException e) {
            throw new DuplicateUserIdException(e);
        }
    }

    public void update(User user) {
        jdbcTemplate.update(
        "update users set name=?, password=?, level=?, login=?, recommend=?, email=? where id=?",
                user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(),
                user.getRecommend(), user.getEmail(), user.getId());
    }

    public User get(String id) throws EmptyResultDataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id=?", new String[] {id}, userMapper);}

    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users order by id", userMapper);}

    public void deleteAll() {
        jdbcTemplate.update("TRUNCATE users");
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    }

}