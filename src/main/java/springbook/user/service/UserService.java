package springbook.user.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


public class UserService {
    UserDao userDao;
    DataSource dataSource;
    PlatformTransactionManager transactionManager;
    MailSender mailSender;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    public void setDataSource(DataSource dataSource) { this.dataSource = dataSource; }
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager; }
    public void setMailSender(MailSender mailSender) { this.mailSender = mailSender; }

    public void upgradeLevels() throws RuntimeException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            List<User> upgradeUsers = new ArrayList<>();
            for (User user: userDao.getAll()){
                if (canUpgrade(user))  {upgradeLevel(user); upgradeUsers.add(user);}
            }
            transactionManager.commit(status);
            for (User user: upgradeUsers) sendUpgradeEmail(user);

        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    public boolean canUpgrade(User user){
        switch (user.getLevel()) {
            case BASIC: return user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER;
            case SILVER: return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown level: "+ user.getLevel());
        }
    }

    public void upgradeLevel(User user){
        user.upgradeLevel();
        userDao.update(user);
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("useradmin@ksug.org");
        mailMessage.setSubject("Upgrade 안내");
        mailMessage.setText("사용자님의 등급이"+user.getLevel().name());

        mailSender.send(mailMessage);
    }
}
