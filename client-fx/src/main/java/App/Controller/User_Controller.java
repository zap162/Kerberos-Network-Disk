package App.Controller;

import App.ClientApp;
import App.Starter;
import DES.DES_des;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class User_Controller implements Initializable {

    public static User_Show_All_File_ArrayList all_files_List = new User_Show_All_File_ArrayList();
    public static User_Show_Download_Task_List download_List = new User_Show_Download_Task_List();
    public static User_Show_Upload_Task_List upload_List = new User_Show_Upload_Task_List();
    private static final Logger logger = Logger.getLogger(User_Controller.class);
    private Boolean download_Worker_is_started = false;
    private Boolean upload_Worker_is_started = false;

    @FXML
    private Label logo_Label;
    @FXML
    private Label file_IMG_Label;
    @FXML
    private Label uploading_IMG_Label;
    @FXML
    private Label downloading_IMG_Label;
    //    @FXML
//    private Label completed_IMG_Label;
    @FXML
    private Label user_IMG_Label;

    @FXML
    private Button file_Button;
    @FXML
    private Button downloading_Button;
    @FXML
    private Button uploading_Button;
//    @FXML
//    private Button completed_Button;

    @FXML
    private Label title_Label;
    @FXML
    private Label progress_Label;
    @FXML
    private Label upload_IMG_Label;
    //    @FXML
//    private Label search_IMG_Label;
    @FXML
    private Label update_IMG_Label;
    @FXML
    private Label download_IMG_Label;
    @FXML
    private Label delete_File_IMG_Label;
    @FXML
    private Label delete_Download_Task_IMG_Label;
    @FXML
    private Label delete_Upload_Task_IMG_Label;
    @FXML
    private ScrollPane show_File_ScrollPane;
    @FXML
    private Label restart_Download_Task_IMG_Label;
    @FXML
    private Label restart_Upload_Task_IMG_Label;

    //    VBox show_All_File_VBox = new VBox();
    static AnchorPane show_All_File_AnchorPane = new AnchorPane();
    static AnchorPane show_Download_Task_AnchorPane = new AnchorPane();
    static AnchorPane show_Upload_Task_AnchorPane = new AnchorPane();
    // static AnchorPane show_finished_Task_AnchorPane = new AnchorPane();

    final int small_Img_Size = 26;

    private final String style_Left_Button_Radius = "7";
    private final String style_Left_Button_Entered = "-fx-border-width:0;-fx-background-color: #E8E8EB;-fx-background-radius:" + style_Left_Button_Radius + ";-fx-border-radius:" + style_Left_Button_Radius + ";";
    private final String style_Left_Button_Choosed = "-fx-border-width:0;-fx-background-color: #D7D7D9;-fx-background-radius:" + style_Left_Button_Radius + ";-fx-border-radius:" + style_Left_Button_Radius + ";";
    private final String style_Left_Button_Origin = "-fx-border-width:0;-fx-background-color: transparent;-fx-background-radius:" + style_Left_Button_Radius + ";-fx-border-radius:" + style_Left_Button_Radius + ";";

    private Boolean file_Button_Activated = false;
    private Boolean downloading_Button_Activated = false;
    private Boolean uploading_Button_Activated = false;
    //private Boolean completed_Button_Activated = false;
    private static double scroll_Pane_Anchor_Width = 965d;


    @FXML
    private void file_Button_Clicked() throws IOException {
        file_Button_Pane_Init();
    }

    @FXML
    private void file_Button_Enter() {
        if (!file_Button_Activated) {
            file_Button.setStyle(style_Left_Button_Entered);
        }
    }

    @FXML
    private void file_Button_Exit() {
        if (!file_Button_Activated) {
            file_Button.setStyle(style_Left_Button_Origin);
        }
    }

    //??????
    private void show_Info_Alerter(String title, String headText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headText);
        alert.setContentText(contentText);
        alert.show();
    }

    private void show_Error_Alerter(String title, String headText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    //????????????????????????
    private void file_Button_Pane_Init() throws IOException {
        //??????????????????
        file_Button_Activated = true;
        downloading_Button_Activated = false;
        uploading_Button_Activated = false;
        //completed_Button_Activated = false;

        //??????????????????
        file_Button.setStyle(style_Left_Button_Choosed);
        downloading_Button.setStyle(style_Left_Button_Origin);
        uploading_Button.setStyle(style_Left_Button_Origin);
//        completed_Button.setStyle(style_Left_Button_Origin);

        //????????????
        title_Label.setText("??????");
        update_IMG_Label.setVisible(true);
//        search_IMG_Label.setVisible(true);
        upload_IMG_Label.setVisible(true);
        download_IMG_Label.setVisible(true);
        delete_File_IMG_Label.setVisible(true);
        progress_Label.setVisible(false);
        delete_Download_Task_IMG_Label.setVisible(false);
        delete_Upload_Task_IMG_Label.setVisible(false);

        restart_Download_Task_IMG_Label.setVisible(false);
        restart_Upload_Task_IMG_Label.setVisible(false);
        file_List_Update_And_Show();
    }

    //??????????????????????????????????????????
    private void file_List_Update_And_Show() throws IOException {
        int status = update_Files_List();
        switch (status) {
            case 0:
                show_Info_Alerter("????????????????????????", "????????????????????????", "?????????????????????????????????????????????");
                break;
            case 1:
                logger.debug("???????????????????????????");
                if (file_Button_Activated) {//?????????????????????????????????UI
                    //????????????UI
                    Platform.runLater(() -> {
                        show_File_ScrollPane.setContent(null);
                        show_File_ScrollPane.setContent(show_All_File_AnchorPane);
                    });
                }
                break;
            case 7:
                show_Info_Alerter("????????????????????????", "????????????????????????", "???????????????????????????????????????");
                logger.debug("??????????????????????????????login????????????");

                if (file_Button_Activated) {
                    //????????????UI
                    Platform.runLater(() -> {
                        try {
                            Starter.setRoot("Login", "?????? | GHZ??????", 420, 512, 420, 512, 420, 512);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                }
                break;
            case 10:
                show_Info_Alerter("????????????????????????", "????????????????????????", "?????????????????????????????????");
                break;
            case 15:
                show_Info_Alerter("????????????????????????", "????????????????????????", "??????????????????????????????");
                break;
            case 20:
                logger.debug("????????????");
                //????????????UI
                Platform.runLater(() -> {
                    show_All_File_AnchorPane = null;
                    show_All_File_AnchorPane = new AnchorPane();
                    show_File_ScrollPane.setContent(null);
                    show_File_ScrollPane.setContent(show_All_File_AnchorPane);
                });
                break;
            case 100:
                show_Info_Alerter("????????????????????????", "????????????????????????", "????????????");
                break;
        }
    }

    //??????????????????????????????????????????????????????
    private void view_Files_List() {
        show_All_File_AnchorPane = null;
        show_All_File_AnchorPane = new AnchorPane();
        show_All_File_AnchorPane.setPrefWidth(scroll_Pane_Anchor_Width);

        //show files in file list
        int t = all_files_List.get_Total();
        double height = 0d;
        for (int i = 0; i < t; i++) {
            show_All_File_AnchorPane.getChildren().add(all_files_List.child_Pane.get(i).pane);
            AnchorPane.setTopAnchor(all_files_List.child_Pane.get(i).pane, height);
            AnchorPane.setLeftAnchor(all_files_List.child_Pane.get(i).pane, 0d);
            AnchorPane.setRightAnchor(all_files_List.child_Pane.get(i).pane, 0d);
            height += 35d;
        }

    }

    //??????????????????????????????????????????
    public static void stage_Changed(double width) {
        scroll_Pane_Anchor_Width = width - 315d;
        show_All_File_AnchorPane.setPrefWidth(scroll_Pane_Anchor_Width);
        show_Download_Task_AnchorPane.setPrefWidth(scroll_Pane_Anchor_Width);
        show_Upload_Task_AnchorPane.setPrefWidth(scroll_Pane_Anchor_Width);
        //show_finished_Task_AnchorPane.setPrefWidth(scroll_Pane_Anchor_Width);
    }

    //??????????????????
    private int update_Files_List() {
        Socket socket = null;
        OutputStream os = null;
        PrintWriter pw = null;
        InputStream is = null;
        BufferedReader br = null;

        try {
            socket = new Socket(ClientApp.down_Load_Server_IP, ClientApp.down_Load_Server_Port);
            logger.debug("?????????????????????");
            os = socket.getOutputStream();//?????????(?????????)
            pw = new PrintWriter(os);//????????????

            JSONObject message_9_Au_Json = new JSONObject();
            message_9_Au_Json.put("id", 9);
            message_9_Au_Json.put("Ticket_v", ClientApp.ticket_DOWN1);
            Date TS5 = new Date();
            JSONObject au_Origin = new JSONObject();
            au_Origin.put("IDc", ClientApp.User_ID);
            au_Origin.put("ADc", ClientApp.ADc);
            au_Origin.put("TS5", TS5);
            String au_Origin_String = au_Origin.toJSONString();
            String au_Encrypt_String = DES_des.Encrypt_Text(au_Origin_String, ClientApp.K_C_DOWN1);
            DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_DOWN1, "", "", au_Origin_String, au_Encrypt_String);
            message_9_Au_Json.put("Authenticator_c", au_Encrypt_String);

            pw.write(message_9_Au_Json + "\n");
            pw.flush();

            //????????????
            is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String server_Message_10 = br.readLine();
            //?????????
            if (server_Message_10 == null) {//???????????????????????????????????????
                while(true){
                    socket.sendUrgentData(0xFF);//????????????
                }
            }
            JSONObject msg_10_Json = JSON.parseObject(server_Message_10);
            if (msg_10_Json.getInteger("id") == 10) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(TS5);
                calendar.add(Calendar.HOUR, 1);
                Date TS5_TEST = calendar.getTime();
                String TS5_TEST_String = DES_des.Encrypt_Text(TS5_TEST.toString(), ClientApp.K_C_DOWN1);
                DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_DOWN1, "", "", TS5_TEST.toString(), TS5_TEST_String);
                if (msg_10_Json.getString("ACK").equals(TS5_TEST_String)) {
                    logger.debug("??????????????????????????????");

                    logger.debug("????????????????????????");
                    JSONObject msg_13_Get_File_List_Json = new JSONObject();
                    msg_13_Get_File_List_Json.put("id", 13);
                    msg_13_Get_File_List_Json.put("IDc", ClientApp.User_ID);
                    String msg_13 = msg_13_Get_File_List_Json.toJSONString();
                    logger.debug("????????????" + msg_13);
                    pw.write(msg_13 + "\n");
                    pw.flush();

                    //????????????
                    is = socket.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is));
                    String msg_13_Reply_File_List = br.readLine();
                    //?????????
                    if (msg_13_Reply_File_List == null) {//???????????????????????????????????????
                        while(true){
                            socket.sendUrgentData(0xFF);//????????????
                        }
                    }
                    JSONObject msg_13_Reply_Json = JSON.parseObject(msg_13_Reply_File_List);
                    if (msg_13_Reply_Json.getInteger("id") == 13) {
                        logger.debug("???????????????????????????");
                        String encrypted_File_Name_List_Json = msg_13_Reply_Json.getString("data");
                        String decrypted_File_Name_List_Json = DES_des.Decrypt_Text(encrypted_File_Name_List_Json, ClientApp.K_C_DOWN1);
                        DES_RSA_Controller.EC_Show_Appendent(true, false, ClientApp.K_C_DOWN1, "", "", decrypted_File_Name_List_Json, encrypted_File_Name_List_Json);
                        JSONObject msg_13_File_Json = JSON.parseObject(decrypted_File_Name_List_Json);
                        logger.debug("Json????????????????????????" + decrypted_File_Name_List_Json);
                        int file_Num = msg_13_File_Json.getInteger("num");
                        all_files_List = null;
                        all_files_List = new User_Show_All_File_ArrayList();
                        if (file_Num != 0) {
                            //??????????????????
                            List<String> file_Name = msg_13_File_Json.getJSONArray("filename").toJavaList(String.class);
                            for (int i = 0; i < file_Num; i++) {
                                all_files_List.add_New_File(file_Name.get(i));

                            }
                            view_Files_List();//??????
                            return 1;
                        } else {
                            logger.debug("???????????????");
                            return 20;
                        }
                    } else if (msg_13_Reply_Json.getInteger("id") == 0) {
                        if (msg_13_Reply_Json.getInteger("status") == 15) {
                            return 15;//???????????? ????????????
                        } else {
                            return 100;//????????????
                        }
                    }
                } else {
                    return 10;//???????????????
                }
            } else if (msg_10_Json.getInteger("id") == 0) {
                if (msg_10_Json.getInteger("status") == 7) {
                    return 7;//Ticket_Download_Server???????????????????????????
                } else {
                    return 100;//????????????
                }
            } else {
                return 100;//????????????
            }
            return 100;//????????????
        } catch (Exception e) {
            logger.error("???????????????????????????\n");
            e.printStackTrace();
            return 0;//??????????????????????????????
        } finally {
            try {
                if (!(br == null)) {
                    br.close();
                }
                if (!(is == null)) {
                    is.close();
                }
                if (!(os == null)) {
                    os.close();
                }
                if (!(pw == null)) {
                    pw.close();
                }
                if (!(socket == null)) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return 100;
            }
        }
    }

    @FXML
    private void downloading_Button_Clicked() {
        downloading_Button_Pane_Init();
    }

    @FXML
    private void downloading_Button_Enter() {
        if (!downloading_Button_Activated) {
            downloading_Button.setStyle(style_Left_Button_Entered);
        }
    }

    @FXML
    private void downloading_Button_Exit() {
        if (!downloading_Button_Activated) {
            downloading_Button.setStyle(style_Left_Button_Origin);
        }
    }

    //????????????????????????
    private void downloading_Button_Pane_Init() {
        //??????????????????
        file_Button_Activated = false;
        downloading_Button_Activated = true;
        uploading_Button_Activated = false;
        //completed_Button_Activated = false;

        //??????????????????
        file_Button.setStyle(style_Left_Button_Origin);
        downloading_Button.setStyle(style_Left_Button_Choosed);
        uploading_Button.setStyle(style_Left_Button_Origin);
//        completed_Button.setStyle(style_Left_Button_Origin);

        //????????????
        title_Label.setText("?????????");
        update_IMG_Label.setVisible(false);
//        search_IMG_Label.setVisible(false);
        upload_IMG_Label.setVisible(false);
        download_IMG_Label.setVisible(false);
        delete_File_IMG_Label.setVisible(false);
        progress_Label.setVisible(true);
        delete_Download_Task_IMG_Label.setVisible(true);
        delete_Upload_Task_IMG_Label.setVisible(false);

        restart_Download_Task_IMG_Label.setVisible(true);
        restart_Upload_Task_IMG_Label.setVisible(false);
        update_and_view_Downloading_Pane();
    }

    //????????????????????????
    private void update_and_view_Downloading_Pane() {
        //????????????UI
        Platform.runLater(() -> {
            if (downloading_Button_Activated) {
                show_File_ScrollPane.setContent(null);
            }
        });
        view_DownLoad_Task_List();
        //????????????UI
        Platform.runLater(() -> {
            if (downloading_Button_Activated) {
                show_File_ScrollPane.setContent(show_Download_Task_AnchorPane);
            }
        });
    }


    //??????????????????????????????????????????????????????
    private void view_DownLoad_Task_List() {
        show_Download_Task_AnchorPane = null;
        show_Download_Task_AnchorPane = new AnchorPane();
        show_Download_Task_AnchorPane.setPrefWidth(scroll_Pane_Anchor_Width);

        //???????????????
        int t = download_List.get_Total();
        double height = 0d;
        for (int i = 0; i < t; i++) {
            if (!download_List.is_NULL.get(i)) {//????????????????????????
                if (download_List.child_Pane.get(i).is_Downloading) {//????????????????????????
                    int finalI = i;
                    double finalHeight = height;
                    //????????????UI
                    Platform.runLater(() -> {
                        show_Download_Task_AnchorPane.getChildren().add(download_List.child_Pane.get(finalI).pane);
                        AnchorPane.setTopAnchor(download_List.child_Pane.get(finalI).pane, finalHeight);
                        AnchorPane.setLeftAnchor(download_List.child_Pane.get(finalI).pane, 0d);
                        AnchorPane.setRightAnchor(download_List.child_Pane.get(finalI).pane, 0d);
                    });
                    height += 35d;
                }
            }
        }
        for (int i = 0; i < t; i++) {
            if (!download_List.is_NULL.get(i)) {//????????????????????????
                if (!download_List.child_Pane.get(i).is_Downloading) {//?????????????????????
                    if (!download_List.is_Complete.get(i)) {//???????????????
                        int finalI = i;
                        double finalHeight1 = height;
                        //????????????UI
                        Platform.runLater(() -> {
                            show_Download_Task_AnchorPane.getChildren().add(download_List.child_Pane.get(finalI).pane);
                            AnchorPane.setTopAnchor(download_List.child_Pane.get(finalI).pane, finalHeight1);
                            AnchorPane.setLeftAnchor(download_List.child_Pane.get(finalI).pane, 0d);
                            AnchorPane.setRightAnchor(download_List.child_Pane.get(finalI).pane, 0d);
                        });
                        height += 35d;
                    }
                }
            }
        }

        for (int i = 0; i < t; i++) {
            if (!download_List.is_NULL.get(i)) {//????????????????????????
                if (!download_List.child_Pane.get(i).is_Downloading) {//?????????????????????
                    if (download_List.is_Complete.get(i)) {//???????????????
                        int finalI = i;
                        double finalHeight1 = height;
                        //????????????UI
                        Platform.runLater(() -> {
                            show_Download_Task_AnchorPane.getChildren().add(download_List.child_Pane.get(finalI).pane);
                            AnchorPane.setTopAnchor(download_List.child_Pane.get(finalI).pane, finalHeight1);
                            AnchorPane.setLeftAnchor(download_List.child_Pane.get(finalI).pane, 0d);
                            AnchorPane.setRightAnchor(download_List.child_Pane.get(finalI).pane, 0d);
                        });
                        height += 35d;
                    }
                }
            }
        }
    }

    //????????????????????????
    @FXML
    private void delete_Download_Task_Clicked() {
        int t = download_List.get_Total();
        logger.debug("??????????????????");
        String delete_Task_Name = "";
        String delete_Failed = "";
        for (int i = 0; i < t; i++) {
            if (!download_List.is_NULL.get(i)) {//????????????????????????
                if (download_List.child_Pane.get(i).is_Checked) {//??????
                    if (!download_List.child_Pane.get(i).is_Downloading) {//????????????????????????
                        delete_Task_Name += download_List.get_File_Name(i) + "\n";
                        logger.debug("????????????: " + download_List.get_File_Name(i));
                        download_List.delete(i);//??????????????????
                    } else {
                        delete_Failed += download_List.get_File_Name(i) + "\n";
                        logger.debug("??????????????????: ?????????????????????" + download_List.get_File_Name(i));
                    }
                }
            }
        }
        if (delete_Task_Name.equals("") && delete_Failed.equals("")) {
            show_Error_Alerter("??????", "??????????????????", "??????????????????????????????????????????");
        } else {
            if (delete_Failed.toString().equals("")) {
                show_Info_Alerter("??????", "????????????????????????\n", delete_Task_Name.toString());
            } else {
                show_Info_Alerter("??????", "????????????????????????\n" + delete_Task_Name, "??????????????????????????????????????????\n" + delete_Failed);
            }
        }
        delete_Task_Name = null;
        delete_Failed = null;
        downloading_Button_Pane_Init();//????????????????????????
    }

    //?????????????????????????????????
    @FXML
    private void restart_Download_Task_Clicked() {
        int t = download_List.get_Total();
        logger.debug("??????????????????");
        String restart_Task_Name = "";
        String restart_Failed = "";
        for (int i = 0; i < t; i++) {
            if (!download_List.is_NULL.get(i)) {//????????????????????????
                if (download_List.child_Pane.get(i).is_Checked) {//??????????????????
                    if (download_List.is_In_Error.get(i)) {//??????????????????
                        restart_Task_Name += download_List.get_File_Name(i) + "\n";
                        logger.debug("????????????: " + download_List.get_File_Name(i));
                        download_List.show_Restart(i);//????????????
                    } else {
                        restart_Failed += download_List.get_File_Name(i) + "\n";
                        logger.debug("??????????????????: ?????????????????????" + download_List.get_File_Name(i));
                    }
                }
            }
        }
        if (restart_Task_Name.equals("") && restart_Failed.equals("")) {
            show_Error_Alerter("??????", "???????????????", "??????????????????????????????");
        } else {
            if (restart_Failed.equals("")) {
                show_Info_Alerter("??????", "????????????????????????\n", restart_Task_Name);
            } else {
                show_Info_Alerter("??????", "????????????????????????\n" + restart_Task_Name, "??????????????????????????????????????????\n" + restart_Failed);
            }
        }
        restart_Task_Name = null;
        restart_Failed = null;
        downloading_Button_Pane_Init();//????????????????????????
    }

    @FXML
    private void uploading_Button_Clicked() {
        uploading_Button_Pane_Init();
    }

    @FXML
    private void uploading_Button_Enter() {
        if (!uploading_Button_Activated) {
            uploading_Button.setStyle(style_Left_Button_Entered);
        }
    }

    @FXML
    private void uploading_Button_Exit() {
        if (!uploading_Button_Activated) {
            uploading_Button.setStyle(style_Left_Button_Origin);
        }
    }

    //????????????????????????
    private void uploading_Button_Pane_Init() {
        //??????????????????
        file_Button_Activated = false;
        downloading_Button_Activated = false;
        uploading_Button_Activated = true;
        //completed_Button_Activated = false;

        //??????????????????
        file_Button.setStyle(style_Left_Button_Origin);
        downloading_Button.setStyle(style_Left_Button_Origin);
        uploading_Button.setStyle(style_Left_Button_Choosed);
//        completed_Button.setStyle(style_Left_Button_Origin);

        //????????????
        title_Label.setText("?????????");
        update_IMG_Label.setVisible(false);
//       search_IMG_Label.setVisible(false);
        upload_IMG_Label.setVisible(false);
        download_IMG_Label.setVisible(false);
        delete_File_IMG_Label.setVisible(false);
        progress_Label.setVisible(true);
        delete_Download_Task_IMG_Label.setVisible(false);
        delete_Upload_Task_IMG_Label.setVisible(true);
        restart_Download_Task_IMG_Label.setVisible(false);
        restart_Upload_Task_IMG_Label.setVisible(true);

        update_and_view_Uploading_Pane();
    }

    private void update_and_view_Uploading_Pane() {
        Platform.runLater(() -> {
            if (uploading_Button_Activated) {
                show_File_ScrollPane.setContent(null);
            }
        });
        view_Upload_Task_List();
        Platform.runLater(() -> {
            if (uploading_Button_Activated) {
                show_File_ScrollPane.setContent(show_Upload_Task_AnchorPane);
            }
        });
    }

    //??????????????????,?????????????????????????????????
    private void view_Upload_Task_List() {
        show_Upload_Task_AnchorPane = null;
        show_Upload_Task_AnchorPane = new AnchorPane();
        show_Upload_Task_AnchorPane.setPrefWidth(scroll_Pane_Anchor_Width);

        //???????????????
        int t = upload_List.get_Total();
        double height = 0d;
        for (int i = 0; i < t; i++) {
            if (!upload_List.is_NULL.get(i)) {//????????????
                if (upload_List.child_Pane.get(i).is_Uploading) {//???????????????
                    int finalI = i;
                    double finalHeight = height;
                    Platform.runLater(() -> {
                        show_Upload_Task_AnchorPane.getChildren().add(upload_List.child_Pane.get(finalI).pane);
                        AnchorPane.setTopAnchor(upload_List.child_Pane.get(finalI).pane, finalHeight);
                        AnchorPane.setLeftAnchor(upload_List.child_Pane.get(finalI).pane, 0d);
                        AnchorPane.setRightAnchor(upload_List.child_Pane.get(finalI).pane, 0d);
                    });
                    height += 35d;
                }
            }
        }
        for (int i = 0; i < t; i++) {
            if (!upload_List.is_NULL.get(i)) {
                if (!upload_List.child_Pane.get(i).is_Uploading) {
                    if (!upload_List.is_Complete.get(i)) {
                        int finalI = i;
                        double finalHeight1 = height;
                        Platform.runLater(() -> {
                            show_Upload_Task_AnchorPane.getChildren().add(upload_List.child_Pane.get(finalI).pane);
                            AnchorPane.setTopAnchor(upload_List.child_Pane.get(finalI).pane, finalHeight1);
                            AnchorPane.setLeftAnchor(upload_List.child_Pane.get(finalI).pane, 0d);
                            AnchorPane.setRightAnchor(upload_List.child_Pane.get(finalI).pane, 0d);
                        });
                        height += 35d;
                    }
                }
            }
        }
        for (int i = 0; i < t; i++) {
            if (!upload_List.is_NULL.get(i)) {//????????????????????????
                if (!upload_List.child_Pane.get(i).is_Uploading) {//?????????????????????
                    if (upload_List.is_Complete.get(i)) {//???????????????
                        int finalI = i;
                        double finalHeight1 = height;
                        //????????????UI
                        Platform.runLater(() -> {
                            show_Upload_Task_AnchorPane.getChildren().add(upload_List.child_Pane.get(finalI).pane);
                            AnchorPane.setTopAnchor(upload_List.child_Pane.get(finalI).pane, finalHeight1);
                            AnchorPane.setLeftAnchor(upload_List.child_Pane.get(finalI).pane, 0d);
                            AnchorPane.setRightAnchor(upload_List.child_Pane.get(finalI).pane, 0d);
                        });
                        height += 35d;
                    }
                }
            }
        }
    }

    //????????????????????????
    @FXML
    private void delete_Upload_Task_Clicked() {
        int t = upload_List.get_Total();
        logger.debug("??????????????????");
        String delete_Task_Name = "";
        String delete_Failed = "";
        for (int i = 0; i < t; i++) {
            if (!upload_List.is_NULL.get(i)) {//????????????????????????
                if (upload_List.child_Pane.get(i).is_Checked) {//??????
                    if (!upload_List.child_Pane.get(i).is_Uploading) {//????????????????????????
                        delete_Task_Name += upload_List.get_File(i).getName() + "\n";
                        logger.debug("????????????: " + upload_List.get_File(i).getName());
                        upload_List.delete(i);//??????????????????
                    } else {
                        delete_Failed += upload_List.get_File(i).getName() + "\n";
                        logger.debug("??????????????????: ?????????????????????" + upload_List.get_File(i).getName());
                    }
                }
            }
        }
        if (delete_Task_Name.equals("") && delete_Failed.equals("")) {
            show_Error_Alerter("??????", "???????????????", "????????????????????????????????????");
        } else {
            if (delete_Failed.equals("")) {
                show_Info_Alerter("??????", "????????????????????????\n", delete_Task_Name);
            } else {
                show_Info_Alerter("??????", "????????????????????????\n" + delete_Task_Name, "??????????????????????????????????????????\n" + delete_Failed);
            }
        }
        delete_Task_Name = null;
        delete_Failed = null;
        uploading_Button_Pane_Init();//????????????????????????
    }

    //?????????????????????????????????
    @FXML
    private void restart_Upload_Task_Clicked() {
        int t = upload_List.get_Total();
        logger.debug("??????????????????");
        String restart_Task_Name = "";
        String restart_Failed = "";
        for (int i = 0; i < t; i++) {
            if (!upload_List.is_NULL.get(i)) {//????????????????????????
                if (upload_List.child_Pane.get(i).is_Checked) {//??????????????????
                    if (upload_List.is_In_Error.get(i)) {//??????????????????
                        restart_Task_Name += upload_List.get_File(i).getName() + "\n";
                        logger.debug("????????????: " + upload_List.get_File(i).getName());
                        upload_List.show_Restart(i);//????????????
                    } else {
                        restart_Failed += upload_List.get_File(i).getName() + "\n";
                        logger.debug("??????????????????: ?????????????????????" + upload_List.get_File(i).getName());
                    }
                }
            }
        }
        if (restart_Task_Name.equals("") && restart_Failed.equals("")) {
            show_Error_Alerter("??????", "???????????????", "??????????????????????????????");
        } else {
            if (restart_Failed.equals("")) {
                show_Info_Alerter("??????", "????????????????????????\n", restart_Task_Name);
            } else {
                show_Info_Alerter("??????", "????????????????????????\n" + restart_Task_Name, "??????????????????????????????????????????\n" + restart_Failed);
            }
        }
        restart_Task_Name = null;
        restart_Failed = null;
        uploading_Button_Pane_Init();//????????????????????????
    }

//    @FXML
//    private void completed_Button_Clicked() {
//        completed_Button_Pane_Init();
//    }

//    @FXML
//    private void completed_Button_Enter() {
//        if (!completed_Button_Activated) {
//            completed_Button.setStyle(style_Left_Button_Entered);
//        }
//    }

//    @FXML
//    private void completed_Button_Exit() {
//        if (!completed_Button_Activated) {
//            completed_Button.setStyle(style_Left_Button_Origin);
//        }
//    }

//    //?????????????????????????????????
//    private void completed_Button_Pane_Init() {
//        //??????????????????
//        file_Button_Activated = false;
//        downloading_Button_Activated = false;
//        uploading_Button_Activated = false;
//        completed_Button_Activated = true;
//
//        //??????????????????
//        file_Button.setStyle(style_Left_Button_Origin);
//        downloading_Button.setStyle(style_Left_Button_Origin);
//        uploading_Button.setStyle(style_Left_Button_Origin);
////        completed_Button.setStyle(style_Left_Button_Choosed);
//
//        //????????????
//        title_Label.setText("????????????");
//        update_IMG_Label.setVisible(false);
////        search_IMG_Label.setVisible(false);
//        upload_IMG_Label.setVisible(false);
//        download_IMG_Label.setVisible(false);
//        delete_File_IMG_Label.setVisible(false);
//        progress_Label.setVisible(false);
//        delete_Download_Task_IMG_Label.setVisible(false);
//        delete_Upload_Task_IMG_Label.setVisible(false);
//
//        restart_Download_Task_IMG_Label.setVisible(false);
//        restart_Upload_Task_IMG_Label.setVisible(false);
//    }

    //??????????????????
    @FXML
    private void upload_Label_Cliecked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("???????????????????????????");
        File file = fileChooser.showOpenDialog(Starter.get_Current_Stage());
        if (file != null) {
            upload_List.add_NewFile(file);
            if (!upload_Worker_is_started) {
                Runnable upload_Thread_Task = () -> {
                    try {
                        upload_Worker();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };
                Thread upload_worker_Thread = new Thread(upload_Thread_Task);
                upload_worker_Thread.setDaemon(true);
                upload_worker_Thread.start();
                upload_Worker_is_started = true;
            }
        }
    }


    //????????????
    private void upload_Worker() throws InterruptedException {
        while (true) {
            int upload_File_Index = upload_List.latestFile();
            if (upload_List.latestFile() != -1) {
                Socket socket = null;
                OutputStream os = null;
                PrintWriter pw = null;
                InputStream is = null;
                BufferedReader br = null;
                upload_List.show_Uploading(upload_File_Index);
                update_and_view_Uploading_Pane();//??????

                try {
                    socket = new Socket(ClientApp.up_Load_Server_IP, ClientApp.up_Load_Server_Port);
                    logger.debug("?????????????????????");
                    os = socket.getOutputStream();//?????????(?????????)
                    pw = new PrintWriter(os);//????????????

                    JSONObject message_9_Au_Json = new JSONObject();
                    message_9_Au_Json.put("id", 9);
                    message_9_Au_Json.put("Ticket_v", ClientApp.ticket_UP1);
                    Date TS5 = new Date();
                    JSONObject au_Origin = new JSONObject();
                    au_Origin.put("IDc", ClientApp.User_ID);
                    au_Origin.put("ADc", ClientApp.ADc);
                    au_Origin.put("TS5", TS5);
                    String au_Origin_String = au_Origin.toJSONString();
                    String au_Encrypt_String = DES_des.Encrypt_Text(au_Origin_String, ClientApp.K_C_UP1);
                    DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_UP1, "", "", au_Origin_String, au_Encrypt_String);
                    message_9_Au_Json.put("Authenticator_c", au_Encrypt_String);

                    pw.write(message_9_Au_Json + "\n");
                    pw.flush();


                    //????????????
                    is = socket.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is));
                    String server_Message_10 = br.readLine();
                    //?????????
                    if (server_Message_10 == null) {//???????????????????????????????????????
                        while(true){
                            socket.sendUrgentData(0xFF);//????????????
                        }
                    }
                    //????????????
                    JSONObject msg_10_Json = JSON.parseObject(server_Message_10);
                    if (msg_10_Json.getInteger("id") == 10) {
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(TS5);
                        calendar.add(calendar.HOUR, 1);
                        Date TS5_TEST = calendar.getTime();
                        String TS5_TEST_String = DES_des.Encrypt_Text(TS5_TEST.toString(), ClientApp.K_C_UP1);
                        DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_UP1, "", "", TS5_TEST.toString(), TS5_TEST_String);
                        if (msg_10_Json.getString("ACK").equals(TS5_TEST_String)) {
                            logger.debug("??????????????????????????????");
                            //??????????????????????????????
                            //int total_Upload_Num = upload_List.get_Total();
                            if (upload_File_Index != -1) {
                                File file_To_Upload = upload_List.get_File(upload_File_Index);
                                if (file_To_Upload.exists()) {
                                    String file_Content = null;
                                    try {
                                        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file_To_Upload));
                                        byte[] decrypted_File_Bytes = in.readAllBytes();//?????????????????????
                                        //file_Content = Base64.getEncoder().encodeToString(bytes);//?????????string
                                        JSONObject msg_11_data = new JSONObject();

                                        msg_11_data.put("filename", file_To_Upload.getName());
                                        //Key
                                        String content_Key = file_To_Upload.getName() + ClientApp.User_ID;
                                        content_Key = Integer.toString(content_Key.hashCode());
                                        byte[] encrypted_File_Bytes = DES_des.Encrypt_Text(decrypted_File_Bytes, content_Key);
                                        String encrypted_File_String = new String(encrypted_File_Bytes);
                                        String decrypted_File_String = new String(decrypted_File_Bytes);
                                        encrypted_File_String = encrypted_File_String.replace("\n", "").replace("\r", "");
                                        String encrypted_File_String_show = encrypted_File_String;
                                        if (encrypted_File_String_show.length() > 500) {
                                            encrypted_File_String_show = encrypted_File_String_show.substring(0, 499);
                                        }
                                        decrypted_File_String = decrypted_File_String.replace("\n", "").replace("\r", "");
                                        String decrypted_File_String_show = decrypted_File_String;
                                        if (decrypted_File_String_show.length() > 500) {
                                            decrypted_File_String_show = decrypted_File_String_show.substring(0, 499);
                                        }
                                        DES_RSA_Controller.EC_Show_Appendent(true, true, String.valueOf(content_Key), "", "", decrypted_File_String_show, encrypted_File_String_show);
                                        //sig
                                        String Hash_Code = String.valueOf(Base64.getEncoder().encodeToString(encrypted_File_Bytes).hashCode());
                                        File rsa_file = new File("client-fx/target/" + ClientApp.User_ID + "_RSA_Key.txt");
                                        FileInputStream rsa_fip = new FileInputStream(rsa_file);
                                        InputStreamReader rsa_reader = new InputStreamReader(rsa_fip, "UTF-8");
                                        StringBuffer sb = new StringBuffer();
                                        while (rsa_reader.ready()) {
                                            sb.append((char) rsa_reader.read());
                                        }
                                        String rsa_String = sb.toString();
                                        JSONObject rsa_Json = JSON.parseObject(rsa_String);
                                        String pk_String = rsa_Json.getString("PK");
                                        String sk_String = rsa_Json.getString("SK");
                                        JSONObject pk_Json = JSON.parseObject(pk_String);
                                        JSONObject sk_Json = JSON.parseObject(sk_String);
                                        BigInteger c_d = new BigInteger(sk_Json.getString("d").getBytes());//??????
                                        BigInteger c_n = new BigInteger(sk_Json.getString("n").getBytes());//??????
                                        BigInteger c_e = new BigInteger(pk_Json.getString("e").getBytes());//??????
                                        String sig_String = RSA.RSA.Encrypt(Hash_Code, c_d, c_n);
                                        DES_RSA_Controller.EC_Show_Appendent(false, true, "", "???????????????????????????", "d:" + c_d + "\tn:" + c_n, Hash_Code, sig_String);
                                        msg_11_data.put("Sig", sig_String);
                                        msg_11_data.put("Em", Base64.getEncoder().encodeToString(encrypted_File_Bytes));

                                        JSONObject msg_11 = new JSONObject();
                                        msg_11.put("id", 11);
                                        msg_11.put("IDc", ClientApp.User_ID);
                                        String en_msg_11_data = DES_des.Encrypt_Text(msg_11_data.toJSONString(), ClientApp.K_C_UP1);
                                        String msg_11_show_data = msg_11_data.toJSONString();
                                        String en_msg_11_show_data = en_msg_11_data;
                                        String msg_11_show_data_show = msg_11_show_data;
                                        if (msg_11_show_data_show.length() > 500) {
                                            msg_11_show_data_show = msg_11_show_data_show.substring(0, 499);
                                        }
                                        String en_msg_11_show_data_show = en_msg_11_show_data;
                                        if (en_msg_11_show_data_show.length() > 500) {
                                            en_msg_11_show_data_show = en_msg_11_show_data_show.substring(0, 499);
                                        }
                                        DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_UP1, "", "", msg_11_show_data_show, en_msg_11_show_data_show);
                                        msg_11.put("data", en_msg_11_data);

                                        rsa_fip.close();
                                        rsa_reader.close();

                                        pw.write(msg_11 + "\n");
                                        pw.flush();

                                        //????????????
                                        is = socket.getInputStream();
                                        br = new BufferedReader(new InputStreamReader(is));
                                        String msg_0 = br.readLine();
                                        //?????????
                                        if (msg_0 == null) {//???????????????????????????????????????
                                            while(true){
                                                socket.sendUrgentData(0xFF);//????????????
                                            }
                                        }
                                        JSONObject msg_0_Json = JSON.parseObject(msg_0);
                                        if (msg_0_Json.getInteger("id") == 0) {
                                            if (msg_0_Json.getInteger("status") == 11) {
                                                logger.error("??????????????????!");
                                                upload_List.show_Error_Uploading(upload_File_Index);
                                            } else {
                                                logger.debug("??????????????????");
                                                upload_List.show_Complete(upload_File_Index);
                                                file_List_Update_And_Show();
                                            }
                                        } else {
                                            logger.error("???????????????????????????");
                                            upload_List.show_Error_Uploading(upload_File_Index);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        logger.error("??????????????????" + e);
                                        upload_List.show_Error_Uploading(upload_File_Index);
                                    }
                                } else {
                                    //??????????????????????????????
                                    logger.error("??????????????????????????????");
                                    upload_List.show_Error_Uploading(upload_File_Index);
                                }
                            } else {
                                Thread.sleep(1000);
                            }
                        } else {
                            logger.debug("??????????????????");
                            upload_List.show_Error_Uploading(upload_File_Index);
                        }
                    } else {
                        logger.debug("???????????????????????????????????????????????????10?????????");
                        upload_List.show_Error_Uploading(upload_File_Index);
                    }

                } catch (Exception e) {
                    logger.error("???????????????????????????\n");
                    e.printStackTrace();
                    upload_List.show_Error_Uploading(upload_File_Index);//??????????????????
                } finally {
                    try {
                        if (!(br == null)) {
                            br.close();
                        }
                        if (!(is == null)) {
                            is.close();
                        }
                        if (!(os == null)) {
                            os.close();
                        }
                        if (!(pw == null)) {
                            pw.close();
                        }
                        if (!(socket == null)) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        update_and_view_Uploading_Pane();//??????
                        Thread.sleep(100);
                    }
                }
            } else {
                Thread.sleep(1000);
            }
        }
    }


    //??????????????????
    @FXML
    private void update_Label_Clicked() throws IOException {
        //???????????????????????????
        file_Button_Pane_Init();
    }

    //???????????????????????????????????????
    @FXML
    private void download_Label_Clicked() {
        int t = all_files_List.get_Total();
        logger.debug("Download Clicked!");
        String download_File_Name = "";
        for (int i = 0; i < t; i++) {
            if (all_files_List.child_Pane.get(i).is_Checked) {
                download_File_Name += all_files_List.get_File_Name(i) + "\n";
                logger.debug("Download file: " + all_files_List.get_File_Name(i));
                download_List.add_NewFile(all_files_List.get_File_Name(i));
            }
        }
        if (download_File_Name.equals("")) {
            show_Error_Alerter("??????", "??????????????????!", "??????????????????????????????!");
        } else {
            //??????????????????
            show_Info_Alerter("????????????", "???????????????????????????????????????????????????", download_File_Name);
            if (!download_Worker_is_started) {
                //??????????????????
                Runnable download_Thread_Task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            download_Worker();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            download_Worker_is_started = false;
                        }
                    }
                };
                Thread download_worker_Thread = new Thread(download_Thread_Task);
                download_worker_Thread.setDaemon(true);
                download_worker_Thread.start();
                download_Worker_is_started = true;
            }
        }
        download_File_Name = null;
        all_files_List.uncheck_All();//????????????
    }

    //????????????
    private void download_Worker() throws InterruptedException {
        while (true) {
            int download_File_Index = download_List.latestFile();
            if (download_List.latestFile() != -1) {
                Socket socket = null;
                OutputStream os = null;
                PrintWriter pw = null;
                InputStream is = null;
                BufferedReader br = null;
                download_List.show_Downloading(download_File_Index);
                update_and_view_Downloading_Pane();//??????

                try {
                    socket = new Socket(ClientApp.down_Load_Server_IP, ClientApp.down_Load_Server_Port);
                    logger.debug("?????????????????????");
                    os = socket.getOutputStream();//?????????(?????????)
                    pw = new PrintWriter(os);//????????????

                    JSONObject message_9_Au_Json = new JSONObject();
                    message_9_Au_Json.put("id", 9);
                    message_9_Au_Json.put("Ticket_v", ClientApp.ticket_DOWN1);
                    Date TS5 = new Date();
                    JSONObject au_Origin = new JSONObject();
                    au_Origin.put("IDc", ClientApp.User_ID);
                    au_Origin.put("ADc", ClientApp.ADc);
                    au_Origin.put("TS5", TS5);
                    String au_Origin_String = au_Origin.toJSONString();
                    String au_Encrypt_String = DES_des.Encrypt_Text(au_Origin_String, ClientApp.K_C_DOWN1);
                    DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_DOWN1, "", "", au_Origin_String, au_Encrypt_String);
                    message_9_Au_Json.put("Authenticator_c", au_Encrypt_String);

                    pw.write(message_9_Au_Json + "\n");
                    pw.flush();


                    //????????????
                    is = socket.getInputStream();
                    br = new BufferedReader(new InputStreamReader(is));
                    String server_Message_10 = br.readLine();
                    //?????????
                    if (server_Message_10 == null) {//???????????????????????????????????????
                        while(true){
                            socket.sendUrgentData(0xFF);//????????????
                        }
                    }
                    //????????????
                    JSONObject msg_10_Json = JSON.parseObject(server_Message_10);
                    if (msg_10_Json.getInteger("id") == 10) {
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTime(TS5);
                        calendar.add(calendar.HOUR, 1);
                        Date TS5_TEST = calendar.getTime();
                        String TS5_TEST_String = DES_des.Encrypt_Text(TS5_TEST.toString(), ClientApp.K_C_DOWN1);
                        DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_DOWN1, "", "", TS5_TEST.toString(), TS5_TEST_String);
                        if (msg_10_Json.getString("ACK").equals(TS5_TEST_String)) {
                            logger.debug("??????????????????????????????");
                            //??????????????????????????????
                            //int total_Download_Num = download_List.get_Total();
                            if (download_File_Index != -1) {
                                String filename_To_Download = download_List.get_File_Name(download_File_Index);
                                JSONObject msg_12_data = new JSONObject();
                                msg_12_data.put("filename", filename_To_Download);
                                String Origion_msg_12_data = msg_12_data.toJSONString();
                                String Encrypt_msg_12_data = DES_des.Encrypt_Text(Origion_msg_12_data, ClientApp.K_C_DOWN1);
                                DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_DOWN1, "", "", Origion_msg_12_data, Encrypt_msg_12_data);
                                JSONObject msg_12_Json = new JSONObject();
                                msg_12_Json.put("id", 12);
                                msg_12_Json.put("IDc", ClientApp.User_ID);
                                msg_12_Json.put("data", Encrypt_msg_12_data);

                                pw.write(msg_12_Json + "\n");
                                pw.flush();

                                //????????????
                                is = socket.getInputStream();
                                br = new BufferedReader(new InputStreamReader(is));
                                String server_Message_12 = br.readLine();
                                //?????????
                                if (server_Message_12 == null) {//???????????????????????????????????????
                                    while(true){
                                        socket.sendUrgentData(0xFF);//????????????
                                    }
                                }
                                JSONObject msg_12_server_Json = JSON.parseObject(server_Message_12);
                                if (msg_12_server_Json.getInteger("id") == 12) {//?????????????????????12???????????????
                                    String data_encrypted = msg_12_server_Json.getString("data");
                                    String data_decrypted = DES_des.Decrypt_Text(data_encrypted, ClientApp.K_C_DOWN1);
                                    String data_decrypted_show  = data_decrypted;
                                    if (data_decrypted_show.length() > 500) {
                                        data_decrypted_show = data_decrypted_show.substring(0, 499);
                                    }
                                    String data_encrypted_show = data_encrypted;
                                    if (data_encrypted_show.length() > 500) {
                                        data_encrypted_show = data_encrypted_show.substring(0, 499);
                                    }
                                    DES_RSA_Controller.EC_Show_Appendent(true, false, ClientApp.K_C_DOWN1, "", "", data_decrypted_show, data_encrypted_show);

                                    JSONObject data_server = JSON.parseObject(data_decrypted);
                                    logger.debug("12?????????data????????????\t" + data_decrypted);
                                    if (data_server.getString("filename").equals(filename_To_Download)) {//??????????????????????????????
                                        String em = data_server.getString("Em");
                                        String Hash_Code = String.valueOf(em.hashCode());
                                        File rsa_file = new File("client-fx/target/" + ClientApp.User_ID + "_RSA_Key.txt");
                                        FileInputStream rsa_fip = new FileInputStream(rsa_file);
                                        InputStreamReader rsa_reader = new InputStreamReader(rsa_fip, "UTF-8");
                                        StringBuffer sb = new StringBuffer();
                                        while (rsa_reader.ready()) {
                                            sb.append((char) rsa_reader.read());
                                        }
                                        String rsa_String = sb.toString();
                                        JSONObject rsa_Json = JSON.parseObject(rsa_String);
                                        String pk_String = rsa_Json.getString("PK");
                                        String sk_String = rsa_Json.getString("SK");
                                        JSONObject pk_Json = JSON.parseObject(pk_String);
                                        JSONObject sk_Json = JSON.parseObject(sk_String);
                                        BigInteger c_d = new BigInteger(sk_Json.getString("d").getBytes());//??????
                                        BigInteger c_n = new BigInteger(sk_Json.getString("n").getBytes());//??????
                                        BigInteger c_e = new BigInteger(pk_Json.getString("e").getBytes());//??????
                                        String sig_String = RSA.RSA.Encrypt(Hash_Code, c_d, c_n);
                                        DES_RSA_Controller.EC_Show_Appendent(false, false, "", "e:" + c_e + "\tn:" + c_n, "???????????????????????????", Hash_Code, sig_String);
                                        rsa_fip.close();
                                        rsa_reader.close();

                                        if (sig_String.equals(data_server.getString("Sig"))) {
                                            logger.debug("??????????????????");
                                            String content_Key = filename_To_Download + ClientApp.User_ID;
                                            content_Key = Integer.toString(content_Key.hashCode());
                                            byte[] encrypted_File_Bytes = Base64.getDecoder().decode(em);
                                            byte[] decrypted_File_Bytes = DES_des.Decrypt_Text(encrypted_File_Bytes, content_Key);
                                            String encrypted_File_String = new String(encrypted_File_Bytes);
                                            String decrypted_File_String = new String(decrypted_File_Bytes);
                                            encrypted_File_String = encrypted_File_String.replace("\n", "").replace("\r", "");
                                            decrypted_File_String = decrypted_File_String.replace("\n", "").replace("\r", "");
                                            String encrypted_File_String_show = encrypted_File_String;
                                            if (encrypted_File_String_show.length() > 500) {
                                                encrypted_File_String_show = encrypted_File_String_show.substring(0, 499);
                                            }
                                            String decrypted_File_String_show = decrypted_File_String;
                                            if (decrypted_File_String_show.length() > 500) {
                                                decrypted_File_String_show = decrypted_File_String_show.substring(0, 499);
                                            }
                                            DES_RSA_Controller.EC_Show_Appendent(true, false, content_Key, "", "", decrypted_File_String_show, encrypted_File_String_show);

                                            logger.debug("em???????????????");
                                            File new_File = new File("./????????????/" + filename_To_Download);
                                            if (new_File.exists()) {
                                                logger.debug("????????????????????????????????????");
                                            }
                                            rsa_file.createNewFile();//????????????
                                            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new_File));
                                            out.write(decrypted_File_Bytes);
                                            out.flush();
                                            out.close();
                                            logger.debug("????????????");
                                            download_List.show_Complete(download_File_Index);//????????????????????????
                                        } else {
                                            logger.debug("??????????????????");
                                            download_List.show_Error_Downloading(download_File_Index);//?????????????????????????????????????????????
                                        }
                                    } else {//????????????????????????
                                        logger.debug("?????????????????????");
                                        download_List.show_Error_Downloading(download_File_Index);//?????????????????????????????????????????????
                                    }
                                } else {//????????????12?????????
                                    logger.debug("???????????????12???????????????");
                                    download_List.show_Error_Downloading(download_File_Index);//???????????????12????????????????????????????????????
                                }
                            } else {//????????????????????????????????????????????????
                                Thread.sleep(1000);
                            }
                        } else {
                            logger.debug("??????????????????");
                            download_List.show_Error_Downloading(download_File_Index);
                        }
                    } else {
                        logger.debug("???????????????????????????????????????????????????10?????????");
                        download_List.show_Error_Downloading(download_File_Index);
                    }
                } catch (Exception e) {
                    logger.error("???????????????????????????\n");
                    e.printStackTrace();
                    download_List.show_Error_Downloading(download_File_Index);//??????????????????
                } finally {
                    try {
                        if (!(br == null)) {
                            br.close();
                        }
                        if (!(is == null)) {
                            is.close();
                        }
                        if (!(os == null)) {
                            os.close();
                        }
                        if (!(pw == null)) {
                            pw.close();
                        }
                        if (!(socket == null)) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        update_and_view_Downloading_Pane();//??????
                        Thread.sleep(100);
                    }
                }
            } else {
                Thread.sleep(1000);
            }
        }
    }

    //????????????????????????
    @FXML
    private void delete_File_Label_Clicked() throws IOException {
        int t = all_files_List.get_Total();
        logger.debug("delete Clicked!");
        List<String> delete_filename_List = new ArrayList<>();
        String delete_File_Name = "";
        Boolean need_To_Delete = false;
        for (int i = 0; i < t; i++) {
            if (all_files_List.child_Pane.get(i).is_Checked) {
                delete_File_Name = all_files_List.get_File_Name(i);
                logger.debug("delete file: " + all_files_List.get_File_Name(i));
                delete_filename_List.add(delete_File_Name);//???????????????????????????????????????
                need_To_Delete = true;
            }
        }

        if (need_To_Delete) {
            //????????????
            Socket socket = null;
            OutputStream os = null;
            PrintWriter pw = null;
            InputStream is = null;
            BufferedReader br = null;

            try {
                socket = new Socket(ClientApp.down_Load_Server_IP, ClientApp.down_Load_Server_Port);
                logger.debug("?????????????????????");
                os = socket.getOutputStream();//?????????(?????????)
                pw = new PrintWriter(os);//????????????

                JSONObject message_9_Au_Json = new JSONObject();
                message_9_Au_Json.put("id", 9);
                message_9_Au_Json.put("Ticket_v", ClientApp.ticket_DOWN1);
                Date TS5 = new Date();
                JSONObject au_Origin = new JSONObject();
                au_Origin.put("IDc", ClientApp.User_ID);
                au_Origin.put("ADc", ClientApp.ADc);
                au_Origin.put("TS5", TS5);
                String au_Origin_String = au_Origin.toJSONString();
                String au_Encrypt_String = DES_des.Encrypt_Text(au_Origin_String, ClientApp.K_C_DOWN1);
                DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_DOWN1, "", "", au_Origin_String, au_Encrypt_String);
                message_9_Au_Json.put("Authenticator_c", au_Encrypt_String);

                pw.write(message_9_Au_Json + "\n");
                pw.flush();

                //????????????
                is = socket.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String server_Message_10 = br.readLine();
                //?????????
                if (server_Message_10 == null) {//???????????????????????????????????????
                    while(true){
                        socket.sendUrgentData(0xFF);//????????????
                    }
                }
                JSONObject msg_10_Json = JSON.parseObject(server_Message_10);
                if (msg_10_Json.getInteger("id") == 10) {
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(TS5);
                    calendar.add(calendar.HOUR, 1);
                    Date TS5_TEST = calendar.getTime();
                    String TS5_TEST_String = DES_des.Encrypt_Text(TS5_TEST.toString(), ClientApp.K_C_DOWN1);
                    DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_DOWN1, "", "", TS5_TEST.toString(), TS5_TEST_String);
                    if (msg_10_Json.getString("ACK").equals(TS5_TEST_String)) {
                        logger.debug("??????????????????????????????");
                        JSONObject msg_14_data_Json = new JSONObject();
                        msg_14_data_Json.put("num", delete_filename_List.size());
                        msg_14_data_Json.put("filename", delete_filename_List);
                        //??????des??????
                        String msg_14_data_encrypted = DES_des.Encrypt_Text(msg_14_data_Json.toJSONString(), ClientApp.K_C_DOWN1);
                        DES_RSA_Controller.EC_Show_Appendent(true, true, ClientApp.K_C_DOWN1, "", "", msg_14_data_Json.toJSONString(), msg_14_data_encrypted);
                        JSONObject msg_14_Json = new JSONObject();
                        msg_14_Json.put("id", 14);
                        msg_14_Json.put("IDc", ClientApp.User_ID);
                        msg_14_Json.put("data", msg_14_data_encrypted);

                        pw.write(msg_14_Json + "\n");
                        pw.flush();

                        //????????????
                        is = socket.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is));
                        String server_Message_0 = br.readLine();
                        //?????????
                        if (server_Message_0 == null) {//???????????????????????????????????????
                            while(true){
                                socket.sendUrgentData(0xFF);//????????????
                            }
                        }
                        JSONObject msg_0_Status = JSON.parseObject(server_Message_0);
                        if (msg_0_Status.getInteger("id") == 0) {
                            if (msg_0_Status.getInteger("status") == 13) {
                                show_Error_Alerter("????????????", "??????????????????", "????????????????????????????????????????????????");
                            } else if (msg_0_Status.getInteger("status") == 12) {
                                show_Info_Alerter("????????????", "??????????????????", "???????????????????????????");
                            } else {
                                show_Error_Alerter("????????????", "????????????", "?????????????????????????????????");
                            }
                        } else {//?????????????????????????????????
                            show_Error_Alerter("????????????", "????????????", "?????????????????????????????????");
                        }
                    }
                } else {
                    show_Error_Alerter("????????????", "????????????", "???????????????????????????????????????");
                    Starter.setRoot("Login", "?????? | GHZ??????", 420, 512, 420, 512, 420, 512);
                }
            } catch (Exception e) {
                logger.error("???????????????????????????\n");
                e.printStackTrace();
                show_Error_Alerter("????????????", "????????????", "???????????????????????????????????????");
            } finally {
                try {
                    if (!(br == null)) {
                        br.close();
                    }
                    if (!(is == null)) {
                        is.close();
                    }
                    if (!(os == null)) {
                        os.close();
                    }
                    if (!(pw == null)) {
                        pw.close();
                    }
                    if (!(socket == null)) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    file_List_Update_And_Show();
                    logger.debug("??????????????????");

                }
            }
        } else {
            show_Error_Alerter("??????", "?????????????????????????????????", "????????????????????????????????????");
        }
    }

    //?????????
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //????????????
        URL image_Url = Starter.class.getResource("img/logo.png");
        ImageView logo_Image = new ImageView(image_Url.toExternalForm());
        logo_Image.setFitHeight(29);
        logo_Image.setFitWidth(29);
        logo_Label.setGraphic(logo_Image);
        file_IMG_Label.setGraphic(set_User_Img("file_IMG_Label"));
        uploading_IMG_Label.setGraphic(set_User_Img("uploading_IMG_Label"));
        downloading_IMG_Label.setGraphic(set_User_Img("downloading_IMG_Label"));
//        completed_IMG_Label.setGraphic(set_User_Img("completed_IMG_Label"));
        user_IMG_Label.setGraphic(set_User_Img("user_IMG_Label"));
        upload_IMG_Label.setGraphic(set_User_Img("upload_IMG_Label"));
        //search_IMG_Label.setGraphic(set_User_Img("search_IMG_Label"));
        update_IMG_Label.setGraphic(set_User_Img("update_IMG_Label"));
        download_IMG_Label.setGraphic(set_User_Img("download_IMG_Label"));
        delete_File_IMG_Label.setGraphic(set_User_Img("delete_File_IMG_Label"));
        delete_Download_Task_IMG_Label.setGraphic(set_User_Img("delete_Download_Task_IMG_Label"));
        delete_Upload_Task_IMG_Label.setGraphic(set_User_Img("delete_Upload_Task_IMG_Label"));
        restart_Download_Task_IMG_Label.setGraphic(set_User_Img("update_IMG_Label"));
        restart_Upload_Task_IMG_Label.setGraphic(set_User_Img("update_IMG_Label"));
        //??????????????????
        try {
            file_Button_Pane_Init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //????????????
    private ImageView set_User_Img(String file_Name) {
        URL image_Url = Starter.class.getResource("img/User/" + file_Name + ".png");
        ImageView image = new ImageView(image_Url.toExternalForm());
        image.setFitHeight(small_Img_Size);
        image.setFitWidth(small_Img_Size);
        return image;
    }
}
