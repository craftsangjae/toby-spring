package springbook.user.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();
            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        } finally {
            if (ps != null) { try {ps.close();} catch (SQLException ignored) {}}
            if (c != null) { try { c.close(); } catch (SQLException ignored) {}}
        }
    }

    public void executeSql(final String query) throws SQLException {
        workWithStatementStrategy(c -> c.prepareStatement(query));
    }

    public void executeSql(final String query, String... varargs) throws SQLException {
        workWithStatementStrategy(c -> {
            PreparedStatement ps = c.prepareStatement(query);
            for (int i=0; i<varargs.length; i++) ps.setString(i+1, varargs[i]);
            return ps;
        });
    }
}
