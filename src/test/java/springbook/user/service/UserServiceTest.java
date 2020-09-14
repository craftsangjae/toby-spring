package springbook.user.service;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
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
    @Autowired
    DataSource dataSource;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;

    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER-1, 0, "bumjin@test.co.kr"),
                new User("joytouch", "강명성", "p2", Level.BASIC, UserService.MIN_LOGCOUNT_FOR_SILVER, 0, "joytouch@test.co.kr"),
                new User("erwins", "신승한", "p3", Level.SILVER, UserService.MIN_LOGCOUNT_FOR_SILVER+1, UserService.MIN_RECOMMEND_FOR_GOLD-1, "erwins@test.co.kr"),
                new User("madnite1", "이상호", "p4", Level.SILVER, UserService.MIN_LOGCOUNT_FOR_SILVER+1, UserService.MIN_RECOMMEND_FOR_GOLD, "madnite1@test.co.kr"),
                new User("green", "오민규", "p5", Level.GOLD, UserService.MIN_LOGCOUNT_FOR_SILVER+1, UserService.MIN_RECOMMEND_FOR_GOLD+1, "green@test.co.kr")
        );
    }

    @Test
    public void bean() {
        assertNotNull(userService);
        assertNotNull(userDao);
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() {
        MockMailSender mockMailSender = new MockMailSender();
        userService.setMailSender(mockMailSender);
        userDao.deleteAll();
        for (User user: users) userDao.add(user);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        List<String> request = mockMailSender.getRequests();
        assertEquals(request.size(), 2);
        assertEquals(request.get(0), users.get(1).getEmail());
        assertEquals(request.get(1), users.get(3).getEmail());
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

    @Test
    public void upgradeAllOrNothing(){
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setDataSource(dataSource);
        testUserService.setTransactionManager(transactionManager);
        testUserService.setMailSender(mailSender);

        userDao.deleteAll();

        for (User user: users) userDao.add(user);

        try {
            testUserService.upgradeLevels();
            fail("TestUserServiceException Failed");
        } catch(TestUserService.TestUserServiceException ignored) {
        }

        checkLevelUpgraded(users.get(1), false);
    }


    static class TestUserService extends UserService {
        private String id;

        public TestUserService(String id) { this.id = id; }

        @Override
        public void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }

        static class TestUserServiceException extends RuntimeException {}
    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<>();
        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            requests.add(simpleMailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage[] simpleMailMessages) throws MailException {
            for (SimpleMailMessage message :simpleMailMessages) requests.add(message.getTo()[0]);
        }
    }
}