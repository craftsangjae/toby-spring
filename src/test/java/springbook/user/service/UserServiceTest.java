package springbook.user.service;

import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest extends TestCase {
    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;

    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER-1, 0),
                new User("joytouch", "강명성", "p2", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", Level.SILVER, UserService.MIN_LOGCOUNT_FOR_SILVER+1, UserService.MIN_RECOMMEND_FOR_GOLD-1),
                new User("madnite1", "이상호", "p4", Level.SILVER, UserService.MIN_LOGCOUNT_FOR_SILVER+1, UserService.MIN_RECOMMEND_FOR_GOLD),
                new User("green", "오민규", "p5", Level.GOLD, UserService.MIN_LOGCOUNT_FOR_SILVER+1, UserService.MIN_RECOMMEND_FOR_GOLD+1)
        );
    }

    @Test
    public void bean() {
        assertNotNull(userService);
        assertNotNull(userDao);
    }

    @Test
    public void upgradeLevels(){
        userDao.deleteAll();
        for (User user: users) userDao.add(user);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);
    }

    public void checkLevelUpgraded(User user, boolean upgraded) {
        User dataUser = userDao.get(user.getId());
        if (upgraded) assertEquals(dataUser.getLevel(), user.getLevel().nextLevel());
        else assertEquals(dataUser.getLevel(), user.getLevel());
    }

    @Test
    public void add() {
        userDao.deleteAll();
        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        assertEquals(userDao.get(users.get(0).getId()), users.get(0));
        assertEquals(userDao.get(users.get(4).getId()), users.get(4));
    }

}