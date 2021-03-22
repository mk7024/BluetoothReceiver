import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.InputStream;

public class main {

    private StreamConnectionNotifier mStreamConnectionNotifier = null;
    private StreamConnection mStreamConnection = null;

    public static void main(String[] args) {
        new main();
    }

    public main() {
        try {
            // 服务器端的UUID必须和手机端的UUID相一致。手机端的UUID需要去掉中间的-分割符。
            mStreamConnectionNotifier = (StreamConnectionNotifier) Connector
                    .open("btspp://localhost:0000110100001000800000805F9B34FB");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 开启线程读写蓝牙上接收和发送的数据。
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("开始监听...");
                    while (true) {
                        mStreamConnection = mStreamConnectionNotifier.acceptAndOpen();
                        System.out.println("接受连接");

                        InputStream is = mStreamConnection.openInputStream();

                        byte[] buffer = new byte[1024];

                        System.out.println("开始读数据...");
                        int len;
                        // 读数据。
                        while ((len = is.read(buffer)) != -1) {
                            String s = new String(buffer,0,len);
                            long systemtime = System.currentTimeMillis();
                            System.out.println(s);
                            if(s.lastIndexOf(" ") != -1){
                                long clientTime = Long.valueOf(s.substring(s.lastIndexOf(" ")+1));
                                long ping = systemtime - clientTime;
                                System.out.println(ping + " ms");
                            }
                        }

                        is.close();
                        mStreamConnection.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }
}
