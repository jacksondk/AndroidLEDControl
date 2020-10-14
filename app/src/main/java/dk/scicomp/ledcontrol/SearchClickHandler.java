package dk.scicomp.ledcontrol;

import android.view.View;

import androidx.arch.core.util.Function;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

class SearchClickHandler implements View.OnClickListener {
    private final int listenPort;
    protected final Function<String, String> onResult;

    public SearchClickHandler(int listenPort, Function<String, String> onResult) {
        this.listenPort = listenPort;
        this.onResult = onResult;

        final Function<String,String> callback = onResult;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket listen = new DatagramSocket(SearchClickHandler.this.listenPort);
                    while (true) {
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        listen.receive(packet);
                        String str;
                        if (packet.getLength() == 4) {
                            // Create string to display readable result of IPv4 address sent
                            // from controller
                            str = "Got " + (int) (0xff & buffer[0]) + "." +
                                    (int) (0xff & buffer[1]) + "." +
                                    (int) (0xff & buffer[2]) + "." +
                                    (int) (0xff & buffer[3]);
                            callback.apply(str);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket clientSocket = new DatagramSocket();
                    InetAddress addr = InetAddress.getByName("192.168.1.255");
                    byte[] bytes = new byte[1];
                    // bytes[0] = 0; // Byte array initialized with zeros - Command 0 -> Get me your ip
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr, 25000);
                    clientSocket.send(packet);
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
