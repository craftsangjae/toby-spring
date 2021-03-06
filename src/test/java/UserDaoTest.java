import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.exception.DuplicateUserIdException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserDaoTest {

    @Autowired
    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {
        user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0, "naver.com");
        user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10, "google.com");
        user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40, "daum.net");
    }

    @Test
    public void addAndGet() {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.add(user1);
        dao.add(user2);
        assertEquals(dao.getCount(), 2);

        User userget1 = dao.get(user1.getId());
        assertEquals(userget1, user1);

        User userget2 = dao.get(user2.getId());
        assertEquals(userget2, user2);

    }
    @Test(expected= DuplicateUserIdException.class)
    public void addDuplicate() {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.add(user1);
        dao.add(user1);
    }

    @Test
    public void count() {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.add(user1);
        assertEquals(dao.getCount(), 1);

        dao.add(user2);
        assertEquals(dao.getCount(), 2);

        dao.add(user3);
        assertEquals(dao.getCount(), 3);
    }

    @Test
    public void getAll() {
        dao.deleteAll();

        Object[] users0 = dao.getAll().toArray();
        Object[] answer0 = {};
        assertArrayEquals(users0, answer0);

        dao.add(user1);
        Object[] users1 = dao.getAll().toArray();
        Object[] answer1 = {user1};
        assertArrayEquals(users1, answer1);

        dao.add(user2);
        Object[] users2 = dao.getAll().toArray();
        Object[] answer2 = {user1, user2};
        assertArrayEquals(users2, answer2);

        dao.add(user3);
        Object[] users3 = dao.getAll().toArray();
        Object[] answer3 = {user3, user1, user2};
        assertArrayEquals(users3, answer3);
    }

    @Test
    public void update() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);

        user1.setName("오민규");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        user1.setEmail("test@test.co.kr");

        dao.update(user1);
        assertEquals(dao.get(user1.getId()), user1);
        assertEquals(dao.get(user2.getId()), user2);
    }



    @Test(expected=EmptyResultDataAccessException.class)
    public void getUserFailure(){
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);
        dao.get("1000");
    }

}
