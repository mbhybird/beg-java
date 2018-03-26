package com.corundumstudio.socketio.demo;

import com.baidu.aip.face.AipFace;
import com.corundumstudio.socketio.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.jms.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatLauncher {

    public static final String IP = "192.168.3.211";
    public static final String GROUP_ID = "group3";
    public static final String REQUEST_IMAGE_URL = "http://" + IP + ":9527" + "/getImageByFaceId?id=";

    public static final String APP_ID = "10546196";
    public static final String API_KEY = "n4i5GdULwGdBIi22SZ65QEZB";
    public static final String SECRET_KEY = "sh2ZLIr4HiB5WwnFCiZmqVQ9gkaVzSet";
    public static java.util.Queue<String> faceQueue = new LinkedList<String>();
    public static AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
    //public static List<SocketIOServer> socketQueue = new ArrayList<SocketIOServer>();
    private static Object object = new Object();
    private static Object objectFilter = new Object();
    private static Object objectCompare = new Object();
    public static java.util.Queue<Long> filterQueue = new LinkedList<Long>();
    public static java.util.Queue<ChatObject> pullMessageQueue = new LinkedList<ChatObject>();
    //驱动程序名
    static String driver = "com.mysql.jdbc.Driver";
    //URL指向要访问的数据库名login
    static String url = "jdbc:mysql://" + IP + ":3306/aidb";
    //MySQL配置时的用户名
    static String user = "root";
    //MySQL配置时的密码
    static String password = "root";
    //声明Connection对象
    static Connection con;

    static {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {

//        Configuration config = new Configuration();
//        config.setHostname("192.168.3.134");
//        config.setPort(9092);

//        final SocketIOServer server = new SocketIOServer(config);
//        server.addEventListener("faceEvent", ChatObject.class, new DataListener<ChatObject>() {
//            @Override
//            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
//                System.out.println(data.getMessage());
//                // broadcast messages to all clients
//                server.getBroadcastOperations().sendEvent("faceEvent", data);
//            }
//        });

//        server.start();
//        socketQueue.add(server);

        MyThread myThread = new MyThread();
        myThread.start();

        FilterThread filterThread = new FilterThread();
        filterThread.start();

        CompareThread compareThread = new CompareThread();
        compareThread.start();

        try
        {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    "admin",
                    "admin",
                    "tcp://" + IP + ":61616");

            // Create a Connection
            javax.jms.Connection connection = connectionFactory.createConnection();
            connection.start();
            boolean b = connectionFactory.isMessagePrioritySupported();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createTopic("FaceNotify");

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            // Wait for a message, loop forever
            boolean loop = true;
            while(loop) {
                Message message = consumer.receive(1);
                if (message == null) {
                    continue;
                }

                if (message instanceof TextMessage) {
                    String text = ((TextMessage) message).getText();
                    faceQueue.offer(text);
                    System.out.println(text);
                } else if (message instanceof MapMessage) {
                    MapMessage mapMessage = (MapMessage) message;
                    //查看以下的测试代码输出，第一行直接打印theTable确实是为空；而下面调用一行根据key获取值之后，再下面一行打印输出theTable就有数了
                    //另外，从MapMessage里面取值基本是用第二行类似的语句，直接是getString类似的语句，根据key值获取
                    //System.out.println(mapMessage);
                    faceQueue.offer(mapMessage.getString("rtId"));
                    System.out.println(mapMessage.getString("rtId"));
                    //System.out.println(mapMessage);

                } else {
                    continue;
                }

            }

            consumer.close();
            session.close();
            connection.close();
            //server.stop();
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {

        }

    }

    static class CompareThread extends Thread{
        @Override
        public void run() {
            while (true) {
                synchronized (objectCompare) {
                    try {
                        if (filterQueue.size() > 0) {
                            long uid = filterQueue.poll();
                            //String path1 = "http://" + IP + ":8080/FaceOS/RTFaceAction!getFacePhoto.do?id=" + uid;
                            //System.out.println(path1);

                            //byte[] image1 = getImageFromNetByUrl(path1);
                            byte[] image1 = readBlob(uid);

                            Statement statement = con.createStatement();
                            String sql = "select id,photo from newuser";
                            ResultSet rs = statement.executeQuery(sql);
                            String ids = "";
                            while (rs.next()) {
                                //byte[] image2 = getImageFromNetByUrl(rs.getString("photo"));
                                byte[] image2 = readBlob(rs.getLong("photo"));
                                Thread.currentThread().sleep(500);
                                JSONObject res = client.match(new byte[][]{image1, image2}, new HashMap<String, String>());
                                //System.out.println(res.toString(2));
                                Double score = ((JSONObject) res.getJSONArray("result").get(0)).getDouble("score");
                                System.out.println(score);

                                if (score > 90) {
                                    long id = rs.getLong("id");
                                    //mark the id need to be remove
                                    ids += "," + id;
                                }
                            }
                            rs.close();

                            if (ids.length() > 0) {
                                //delete the db record
                                sql = "delete from newuser where id in(" + ids.substring(1) + ")";
                                System.out.println(sql);
                                statement.execute(sql);
                            }
                        }

                        Thread.currentThread().sleep(5 * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class FilterThread extends Thread {
        @Override
        public void run() {
            while (true) {
                synchronized (objectFilter) {
                    try {
                        Statement statement = con.createStatement();
                        String sql = "select uid from filteruser limit 1;";
                        ResultSet rs = statement.executeQuery(sql);
                        long uid = -1;
                        if (rs.next()) {
                            uid = rs.getLong("uid");
                            filterQueue.offer(uid);
                            System.out.println(uid);
                        }
                        rs.close();
                        if (uid != -1) {
                            statement.execute("delete from filteruser where uid=" + String.valueOf(uid));
                        }

                        Thread.currentThread().sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            while(true) {
                synchronized (object) {
                    try {
                        if (faceQueue.size() > 0) {
//                            socketQueue.get(0).getBroadcastOperations().sendEvent("loading", new ChatObject());
                            doFaceVerify(GROUP_ID);
                        }
                        else {
//                            List<ChatObject> messageList = new ArrayList<ChatObject>();
//                            while (pullMessageQueue.size() > 0) {
//                                messageList.add(pullMessageQueue.poll());
//                            }
//
//                            if (messageList.size() > 0) {
//                                socketQueue.get(0).getBroadcastOperations().sendEvent("faceEvent", messageList);
//                            }
                        }
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        // TODO: handle exception
                    }
                }
            }
        }
    }

    private static void addNewUser (String uid, String info) {
        String sql = "insert into newuser(uid,photo,info) values(?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) con.prepareStatement(sql);
            pstmt.setLong(1, Long.parseLong(uid));
            pstmt.setString(2, REQUEST_IMAGE_URL + uid);
            pstmt.setString(3, replaceBlank(info));
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addOldUser (String uid, String info, String crmId) {
        String sql = "insert into olduser(uid,photo,info,crm_id) values(?,?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) con.prepareStatement(sql);
            pstmt.setLong(1, Long.parseLong(uid));
            pstmt.setString(2, REQUEST_IMAGE_URL + uid);
            pstmt.setString(3, replaceBlank(info));
            pstmt.setString(4, crmId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String addCustomer () {
        String sql = "insert into t_customer(custname,crmid) values(?,?)";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String crmId = "crm_" + df.format(System.currentTimeMillis());
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) con.prepareStatement(sql);
            pstmt.setString(1, crmId);
            pstmt.setString(2, crmId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return crmId;
    }

    private static void updateUserType (String uid) {
        String sql = "update olduser set isNew=0 where uid=? and isNew=1 and CURRENT_TIMESTAMP()-crdate > 100 ";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) con.prepareStatement(sql);
            pstmt.setLong(1, Long.parseLong(uid));
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateHistory(String uid) {
        String sql = "insert history(uid) values(?) ";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) con.prepareStatement(sql);
            pstmt.setLong(1, Long.parseLong(uid));
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    private static void doFaceVerify(String groupId) {
        try {
            String info;
            String faceId = faceQueue.poll();
            //String path = "http://" + IP + ":8080/FaceOS/RTFaceAction!getFacePhoto.do?id=" + faceId;

            HashMap<String, String> options = new HashMap<String, String>();
            options.put("max_face_num", "1");
            options.put("face_fields", "age,beauty,gender,glasses,race,qualities");

            //byte[] image = getImageFromNetByUrl(path);
            byte[] image = readBlob(Long.parseLong(faceId));
            JSONObject res = client.detect(image, options);
            info = res.toString(2);
            System.out.println(info);

            JSONObject checkList = ((JSONObject) res.getJSONArray("result").get(0)).getJSONObject("qualities");
            //模糊度<0.7,完整度=1,光照度>40
            if (checkList.getDouble("blur") < 0.7
                    && checkList.getInt("completeness") == 1
                    && checkList.getInt("illumination") > 40) {
                JSONObject resIdentify = client.identifyUser(groupId, image, new HashMap<String, String>());
                System.out.println(resIdentify.toString(2));

                if (resIdentify.has("error_code")) {
                    long errCode = resIdentify.getLong("error_code");
                    //216611-user not exist
                    //216616-image existed
                    //216618-no user in group
                    if (errCode != 216616) {
                        JSONObject addUser = client.addUser(faceId, faceId, groupId, image, new HashMap<String, String>());
                        System.out.println(addUser.toString(2));
                        String crmId = addCustomer();
                        addOldUser(faceId, res.toString(2), crmId);
                        Thread.currentThread().sleep(5000);
                    }
                } else {
                    JSONArray scoreArray = ((JSONObject) resIdentify.getJSONArray("result").get(0)).getJSONArray("scores");
                    if (scoreArray.length() > 0) {
                        double score = Double.parseDouble(scoreArray.get(0).toString());
                        if (score > 90) {
                            //old user
//                            ChatObject chatObject = new ChatObject();
//                            chatObject.setUserType(1);
//                            chatObject.setPhoto(path);
//                            chatObject.setUserInfo(info);
//                            chatObject.setUserField(resIdentify.toString(2));
//                            socketQueue.get(0).getBroadcastOperations().sendEvent("faceEvent", chatObject);
                            String uid = ((JSONObject) resIdentify.getJSONArray("result").get(0)).getString("uid");
                            updateUserType(uid);
                            updateHistory(uid);
                        } else {
                            JSONObject addUser = client.addUser(faceId, faceId, groupId, image, new HashMap<String, String>());
                            System.out.println(addUser.toString(2));
                            String crmId = addCustomer();
                            addOldUser(faceId, res.toString(2), crmId);
                            Thread.currentThread().sleep(5000);
                        }
                    }
                }
            }
            else{
                addNewUser(faceId, res.toString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static byte[] readBlob(long faceId) {
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
}
