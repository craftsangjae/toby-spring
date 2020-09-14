package springbook.user.domain;

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
public class UserTest extends TestCase {
    User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test()
    public void upgradeLevel() {
        for (Level level: Level.values()) {
            if (level.nextLevel()==null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertEquals(user.getLevel(), level.nextLevel());
        }
    }

    @Test(expected=IllegalStateException.class)
    public void cannotupgradeLevel() {
        for (Level level: Level.values()) {
            if (level.nextLevel()!=null) continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }
}