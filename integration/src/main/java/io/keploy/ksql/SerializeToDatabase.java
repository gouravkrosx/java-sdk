package io.keploy.ksql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/*
 * mysql> CREATE TABLE java_objects (
 * id INT AUTO_INCREMENT,
 * name varchar(128),
 * object_value BLOB,
 * primary key (id));
 **/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class SerializeJavaObjects_MySQL {
    static final String WRITE_OBJECT_SQL = "INSERT INTO java_objects(name, object_value) VALUES (?, ?)";

    static final String READ_OBJECT_SQL = "SELECT object_value FROM java_objects WHERE id = ?";

    public static Connection getConnection() throws Exception {
        String driver = "org.postgresql.Driver";
        String url = "jdbc:postgresql://localhost:5438/postgres";
        String username = "postgres";
        String password = "postgres";
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }

    public static long writeJavaObject(Connection conn, Object object) throws Exception {
        String className = object.getClass().getName();
        PreparedStatement pstmt = conn.prepareStatement(WRITE_OBJECT_SQL);

        // set input parameters
        pstmt.setString(1, className);
        pstmt.setObject(2, object);
        pstmt.executeUpdate();

        // get the generated key for the id
        ResultSet rs = pstmt.getGeneratedKeys();
        int id = -1;
        if (rs.next()) {
            id = rs.getInt(1);
        }

        rs.close();
        pstmt.close();
        System.out.println("writeJavaObject: done serializing: " + className);
        return id;
    }

    public static Object readJavaObject(Connection conn, long id) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement(READ_OBJECT_SQL);
        pstmt.setLong(1, id);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        Object object = rs.getObject(1);
        String className = object.getClass().getName();

        rs.close();
        pstmt.close();
        System.out.println("readJavaObject: done de-serializing: " + className);
        return object;
    }
    public static void main(String args[])throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            System.out.println("conn=" + conn);
            conn.setAutoCommit(false);
            List<Object> list = new ArrayList<Object>();
            list.add("This is a short string.");
            list.add(new Integer(1234));
            list.add(new Date());

            long objectID = writeJavaObject(conn, list);
            conn.commit();
            System.out.println("Serialized objectID => " + objectID);
            List listFromDatabase = (List) readJavaObject(conn, objectID);
            System.out.println("[After De-Serialization] list=" + listFromDatabase);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }
}










public class SerializeToDatabase {

    private static final String SQL_SERIALIZE_OBJECT = "INSERT INTO serialized_java_objects(object_name, serialized_object) VALUES (?, ?)";
    private static final String SQL_DESERIALIZE_OBJECT = "SELECT serialized_object FROM serialized_java_objects WHERE serialized_id = ?";

    public static long serializeJavaObjectToDB(Connection connection,
                                               Object objectToSerialize) throws SQLException {

        PreparedStatement pstmt = connection
                .prepareStatement(SQL_SERIALIZE_OBJECT);

        // just setting the class name
        pstmt.setString(1, objectToSerialize.getClass().getName());
        pstmt.setObject(2, objectToSerialize);
        pstmt.executeUpdate();
        ResultSet rs = pstmt.getGeneratedKeys();
        int serialized_id = -1;
        if (rs.next()) {
            serialized_id = rs.getInt(1);
        }
        rs.close();
        pstmt.close();
        System.out.println("Java object serialized to database. Object: "
                + objectToSerialize);
        return serialized_id;
    }

    /**
     * To de-serialize a java object from database
     *
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deSerializeJavaObjectFromDB(Connection connection,
                                                     long serialized_id) throws SQLException, IOException,
            ClassNotFoundException {
        PreparedStatement pstmt = connection
                .prepareStatement(SQL_DESERIALIZE_OBJECT);
        pstmt.setLong(1, serialized_id);
        ResultSet rs = pstmt.executeQuery();
        rs.next();

         Object object = rs.getObject(1);

        byte[] buf = rs.getBytes(1);
        ObjectInputStream objectIn = null;
        if (buf != null)
            objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));

        Object deSerializedObject = objectIn.readObject();

        rs.close();
        pstmt.close();

        System.out.println("Java object de-serialized from database. Object: "
                + deSerializedObject + " Classname: "
                + deSerializedObject.getClass().getName());
        return deSerializedObject;
    }

    /**
     * Serialization and de-serialization of java object from mysql
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String args[]) throws ClassNotFoundException,
            SQLException, IOException {
        Connection connection = null;

        String driver = "org.postgresql.Driver";
        String url = "jdbc:postgresql://localhost:5438/postgres";
        String username = "postgres";
        String password = "postgres";

        Class.forName(driver);
        connection = DriverManager.getConnection(url, username, password);

        // a sample java object to serialize
        Vector obj = new Vector();
        obj.add("java");
        obj.add("papers");

        // serializing java object to mysql database
        long serialized_id = serializeJavaObjectToDB(connection, obj);

        // de-serializing java object from mysql database
        Vector objFromDatabase = (Vector) deSerializeJavaObjectFromDB(
                connection, serialized_id);

        connection.close();
    }
}