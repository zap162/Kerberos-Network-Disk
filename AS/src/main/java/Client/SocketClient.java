package Client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SocketClient {
    private static String message_1() throws IOException {
        File f=new File("AS/target/client1_CA.txt");
        FileInputStream fis=new FileInputStream(f);
        InputStreamReader reader=new InputStreamReader(fis,"UTF-8");
        StringBuffer sb=new StringBuffer();
        while(reader.ready()){
            sb.append((char)reader.read());
        }
        String str=sb.toString();
        JSONObject obj=new JSONObject();
        obj.put("id",1);
        obj.put("CA",str);
        obj.put("CAHash",Integer.toString(str.hashCode()));
        return obj.toJSONString();
    }
    private static String message_3(){
        JSONObject obj=new JSONObject();
        obj.put("id",3);
        obj.put("user_ID","lzt");
        obj.put("password","123456");
        return obj.toJSONString();
    }

    private static String message_4() throws IOException {
        JSONObject obj=new JSONObject();
        obj.put("id",4);
        File f=new File("AS/target/AS1_RSA_Key.txt");
        FileInputStream fis=new FileInputStream(f);
        InputStreamReader reader=new InputStreamReader(fis,"UTF-8");
        StringBuffer sb=new StringBuffer();
        while(reader.ready()){
            sb.append((char)reader.read());
        }
        String str= sb.toString();
        JSONObject AS_obj= JSON.parseObject(str);
        JSONObject PK_obj=JSON.parseObject(AS_obj.getString("PK"));
        BigInteger n=new BigInteger(PK_obj.getString("n"));
        BigInteger e=new BigInteger(PK_obj.getString("e"));
        JSONObject LoginData=new JSONObject();
        LoginData.put("user_ID","lzt");
        LoginData.put("password","123456");
        String LoginData_str=LoginData.toJSONString();
        LoginData_str=RSA.RSA.Encrypt(LoginData_str,e,n);
        obj.put("loginData",LoginData_str);
        return obj.toJSONString();
    }

    private static String message_5(){
        JSONObject obj=new JSONObject();
        obj.put("id",5);
        obj.put("user_ID","client1");
        obj.put("IDtgs","TGS1");
        Date TS1 = new Date();
        obj.put("TS1",TS1);
        return obj.toJSONString();
    }

    public static void main(String[] args) throws InterruptedException {
        Socket socket=null;
        OutputStream os=null;
        PrintWriter pw=null;
        InputStream is=null;
        BufferedReader br=null;
        try {
            // ????????????????????????
            socket = new Socket("localhost",8888);
            System.out.println("??????????????????\n");
            // ??????????????????????????????
            int a=2;
            while(--a!=0){
                os = socket.getOutputStream();//?????????(?????????)
                pw = new PrintWriter(os);//????????????

                String str=message_5();
                System.out.println("str = " + str);
                pw.write(str+"\n");
                pw.flush();
                //socket.shutdownOutput();//???????????????
                // ???????????????????????????

                is = socket.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));

                String info =br.readLine();
                //?????????
                //if(info.toString()==null){//???????????????????????????????????????
                //    socket.sendUrgentData(0xFF);//????????????
                //}
                //while((info = br.readLine())!=null){
                System.out.println("??????????????????"+info);
                //}
                /*String str1=server.creat_msg11();
                pw.write(str1+"\n");
                pw.flush();
                String info1 =br.readLine();
                System.out.println("??????????????????"+info1);*/
            }
        } catch (Exception e) {
            System.out.println("???????????????????????????\n");
            e.printStackTrace();
        }finally {
            try {
                br.close();
                is.close();
                os.close();
                pw.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void test() throws IOException {
        //RSA.RSA.Generate("client1");
        RSA.RSA.GenerateCA("AS","client1");
    }
}
