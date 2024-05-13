package MoneyManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static MoneyManager.DBUtil.rs;

// this file shows how to use the sql of java
// CRUD operations
public class example {
    public void insertData(String name, int age) {
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = null;
        String insertQuery = "INSERT INTO your_table_name (name, age) VALUES (?, ?)";
        try {
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                DBUtil.closeConnection(conn, pstmt, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteData(int id) {
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = null;
        String deleteQuery = "DELETE FROM your_table_name WHERE id = ?";
        try {
            pstmt = conn.prepareStatement(deleteQuery);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                DBUtil.closeConnection(conn,pstmt,null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateData(int id, String newName, int newAge) {
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = null;
        String updateQuery = "UPDATE your_table_name SET name = ?, age = ? WHERE id = ?";
        try {
            pstmt = conn.prepareStatement(updateQuery);
            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                DBUtil.closeConnection(conn,pstmt,null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void selectData() {
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = null;
        String selectQuery = "SELECT * FROM your_table_name";
        try {
            pstmt = conn.prepareStatement(selectQuery);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Age: " + rs.getInt("age"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                DBUtil.closeConnection(conn,pstmt,rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


