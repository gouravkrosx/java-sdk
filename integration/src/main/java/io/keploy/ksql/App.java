package io.keploy.ksql;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import io.keploy.utils.ProcessD;

import java.sql.*;

import static io.keploy.utils.ProcessD.*;


public class App {
    private static final String url = "jdbc:postgresql://localhost:5438/postgres";
    private static final String user = "postgres";
    private static final String password = "postgres";


    //    docker exec awesome_mayer mysql -u mysql -pmysql -e “drop schema demo_table; create schema demo_table;”
    public static <T> void main(String[] args) throws SQLException {

//        app.connect();
        System.out.println("begin test mysql jdbc");
        Connection conn = null;
        try {
//            KDriver driverInst = new KDriver();
//            DriverManager.registerDriver(driverInst);
            conn = DriverManager.getConnection(url, user, password);
            PreparedStatement pp = conn.prepareStatement("select * from books");

            byte[] data = ProcessD.encodedPreparedStatement(pp);
            //test
            PreparedStatement qq = ProcessD.decodePreparedStatement(data);//(PreparedStatement)xstream.fromXML(xml);
            System.out.println(qq);


//            pp.setInt(1, 21);
            ResultSet rs = qq.executeQuery();
            while (rs.next())
                if (rs.getString(2) == "gourav"){
                    break;
            }
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }


        Deserial f = new KDeserialize(12745);
        byte[] data = ProcessD.encoded(f);
        //test
        Deserial g = ProcessD.decode(data);
        System.out.println(g);

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

