package com.example.aiweb;

/**
 * Created by NickChung on 01/02/2018.
 */
import com.baidu.aip.face.AipFace;
import dao.AIDBDao;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@RestController
public class HomeController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${mycfg.ip}")
    private String IP;
    @Value("${mycfg.group}")
    private String GROUP_ID;

    public static final String APP_ID = "10546196";
    public static final String API_KEY = "n4i5GdULwGdBIi22SZ65QEZB";
    public static final String SECRET_KEY = "sh2ZLIr4HiB5WwnFCiZmqVQ9gkaVzSet";
    public static AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);


    @RequestMapping("/getNewUsers")
    public List<Map<String, Object>> getNewUsers(int page, int size) {
        String sql = "select * from newuser limit " + (page - 1) * size + "," + size;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : list) {
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            if (entries != null) {
                Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    System.out.println(key + ":" + value);
                }
            }
        }
        return list;
    }

    @RequestMapping("/getImageByFaceId")
    @ResponseBody
    public void getImageByFaceId(int id, HttpServletResponse response) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            AIDBDao dao = new AIDBDao();
            response.setContentType("image/jpeg; charset=gbk");
            is = dao.readBlobStream(id);
            os = response.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            os.close();
            is.close();
        }
    }

    @RequestMapping("/showCfg")
    public String getMyCfg() {
        return String.format("IP:%s,Group:%s", IP, GROUP_ID);
    }

    @RequestMapping("/deleteOldUser")
    public int deleteOldUser(int uid) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("group_id", GROUP_ID);
        client.deleteUser(String.valueOf(uid), options);
        jdbcTemplate.execute("delete from olduser where uid=" + uid);
        return 1;
    }

    @RequestMapping("/updateUser")
    public int updateUser(int uid) {
        //reg ai.baidu
        //String path = "http://" + IP + ":8080/FaceOS/RTFaceAction!getFacePhoto.do?id=" + uid;
        //byte[] image = getImageFromNetByUrl(path);
        AIDBDao dao = new AIDBDao();
        byte[] image = dao.readBlob(uid);
        String userId = String.valueOf(uid);
        JSONObject addUser = client.addUser(userId, userId, GROUP_ID, image, new HashMap<String, String>());
        System.out.println(addUser.toString(2));

        //move newuser to olduser
        jdbcTemplate.update("insert olduser (uid,photo,info) select uid,photo,info from newuser where uid=" + uid);
        jdbcTemplate.update("delete from newuser where uid=" + uid);

        //insert the filter user then trigger the background schedule
        jdbcTemplate.update("insert filteruser select " + uid);


        return 1;
    }

    @RequestMapping("/deleteNewUser")
    public int deleteNewUser(String uids) {
        int affectedRows = jdbcTemplate.update("delete from newuser where uid in(" + uids + ")");

        return affectedRows;
    }

    @RequestMapping("/deleteAllNewUsers")
    public int deleteAllNewUser() {
        int affectedRows = jdbcTemplate.update("delete from newuser");

        return affectedRows;
    }

    @RequestMapping("/getOldUsers")
    public List<Map<String, Object>> getOldUsers(int page, int size, Integer isNew) {
        String sql = "select * from olduser where isNew=" + isNew + " limit " + (page - 1) * size + "," + size;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : list) {
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            if (entries != null) {
                Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    System.out.println(key + ":" + value);
                }
            }
        }
        return list;
    }

    public static byte[] readInputStream(InputStream inStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    public static byte[] getImageFromNetByUrl(String strUrl){
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
            byte[] btImg = readInputStream(inStream);//得到图片的二进制数据
            return btImg;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}