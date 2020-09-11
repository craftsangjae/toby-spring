import springbook.user.dao.DConnectionMaker;
import springbook.user.dao.SimpleConnectionMaker;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

import java.sql.SQLException;

public class TestMain {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        SimpleConnectionMaker connectionMaker  = new DConnectionMaker();
        UserDao dao = new UserDao(connectionMaker);

        User user = new User();
        user.setId("whiteship");
        user.setName("백기선");
        user.setPassword("married");

        dao.add(user);

        System.out.println(user.getId()+" 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());

        System.out.println(user2.getPassword());
        System.out.println(user2.getId()+" 조회 성공");

    }
}
