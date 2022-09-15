package io.keploy.ksql;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    private final String url = "jdbc:mysql://localhost:3306/mysql";
    private final String user = "mysql";
    private final String password = "mysql";


    //    docker exec awesome_mayer mysql -u mysql -pmysql -e “drop schema demo_table; create schema demo_table;”
    public static void main(String[] args) throws SQLException {
        App app = new App();
//        app.connect();
        System.out.println("begin test mysql jdbc");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            java.sql.Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "mysql", "mysql");
            java.sql.PreparedStatement pp = conn.prepareStatement("select * from demo_table where age = ?");
            pp.setInt(1, 21);
            ResultSet re = pp.executeQuery();
            String result;
            if (re.next()) {
                result = "name is :" + re.getString(1) + ", age is :" + re.getInt(2) + ", score is :" + re
                        .getInt(3);
                System.out.println("result is " + result);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("end test mysql jdbc");
    }
}

/*
    Some executable queries :
1)  ResultSet rs = st.executeQuery("SELECT VERSION()");
2) SELECT * from url_map where id is equal to
3)

        int id = 6;
        String author = "Trygve Gulbranssen";
        String query = "INSERT INTO authors(id, name) VALUES(?, ?)";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setInt(1, id);
            pst.setString(2, author);
            pst.executeUpdate();

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(JavaPostgreSqlPrepared.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }




4)
try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("SELECT * FROM authors");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                System.out.print(rs.getInt(1));
                System.out.print(": ");
                System.out.println(rs.getString(2));
            }

        }catch (SQLException ex) {

            Logger lgr = Logger.getLogger(KsqlApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

    }


5)
 String query = "SELECT id, name FROM authors WHERE Id=1;"
                + "SELECT id, name FROM authors WHERE Id=2;"
                + "SELECT id, name FROM authors WHERE Id=3";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query)) {

            boolean isResult = pst.execute();

            do {
                try (ResultSet rs = pst.getResultSet()) {

                    while (rs.next()) {

                        System.out.print(rs.getInt(1));
                        System.out.print(": ");
                        System.out.println(rs.getString(2));
                    }

                    isResult = pst.getMoreResults();
                }
            } while (isResult);

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    KsqlApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }


6)   String query = "SELECT name, title From authors, "
                + "books WHERE authors.id=books.author_id";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();

            String colname1 = meta.getColumnName(1);
            String colname2 = meta.getColumnName(2);

            Formatter fmt1 = new Formatter();
            fmt1.format("%-21s%s", colname1, colname2);
            System.out.println(fmt1);

            while (rs.next()) {

                Formatter fmt2 = new Formatter();
                fmt2.format("%-21s", rs.getString(1));
                System.out.print(fmt2);
                System.out.println(rs.getString(2));
            }

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    KsqlApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
7)
 try (Connection con = DriverManager.getConnection(url, user, password)) {

            try (Statement st = con.createStatement()) {

                con.setAutoCommit(false);
                st.executeUpdate("UPDATE authors SET name = 'Leo Tolstoy' "
                        + "WHERE Id = 1");
                st.executeUpdate("UPDATE books SET title = 'War and Peace' "
                        + "WHERE Id = 1");
                st.executeUpdate("UPDATE books SET titl = 'Anna Karenina' "
                        + "WHERE Id = 2");

                con.commit();

            } catch (SQLException ex) {

                if (con != null) {
                    try {
                        con.rollback();
                    } catch (SQLException ex1) {
                        Logger lgr = Logger.getLogger(KsqlApplication.class.getName());
                        lgr.log(Level.WARNING, ex1.getMessage(), ex1);
                    }
                }

                Logger lgr = Logger.getLogger(KsqlApplication.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(KsqlApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }


  8)         try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement()) {

            st.executeUpdate("UPDATE authors SET name = 'Leo Tolstoy' "
                    + "WHERE Id = 1");
            st.executeUpdate("UPDATE books SET title = 'War and Peace' "
                    + "WHERE Id = 1");
            st.executeUpdate("UPDATE books SET titl = 'Anna Karenina' "
                    + "WHERE Id = 2");

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(
                    KsqlApplication.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }


9)

 */

