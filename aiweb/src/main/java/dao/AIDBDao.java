package dao;

import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

/**
 * Created by NickChung on 23/03/2018.
 */
public class AIDBDao {
    private String IP = "192.168.3.211";

    //驱动程序名
    String driver = "com.mysql.jdbc.Driver";
    //URL指向要访问的数据库名login
    String url = "jdbc:mysql://" + IP + ":3306/aidb";
    //MySQL配置时的用户名
    String user = "root";
    //MySQL配置时的密码
    String password = "root";
    //声明Connection对象
    Connection con;

    public AIDBDao() {
        try {
            // 加载驱动
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }


    public byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public byte[] readBlob(long faceId) {
        try {
            InputStream is = null;
            String sql = "select image from facePic where faceId = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, faceId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                is = rs.getBlob("image").getBinaryStream();
            }
            rs.close();
            ps.close();
            byte[] imgBytes = readInputStream(is);
            return imgBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public InputStream readBlobStream(long faceId) {
        try {
            InputStream is = null;
            String sql = "select image from facePic where faceId = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, faceId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                is = rs.getBlob("image").getBinaryStream();
            }
            rs.close();
            ps.close();
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
