package dk.scicomp.ledcontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.CyclicBarrier;

public class MainActivity extends AppCompatActivity {

    private SeekBar redBar;
    private SeekBar greenBar;
    private SeekBar blueBar;
    private SendUpdats updater = new SendUpdats();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(updater).start();
        setContentView(R.layout.activity_main);

        redBar = findViewById(R.id.redBar);
        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new ColorSeekBarChange();
        redBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        greenBar = findViewById(R.id.greenBar);
        greenBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        blueBar = findViewById(R.id.blueBar);
        blueBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    class SendUpdats implements Runnable {

        byte red, green, blue;
        Object monitor = new Object();

        public void setColor(byte red, byte green, byte blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            synchronized (monitor) {
                monitor.notify();
            }
        }

        void workerLoop() {
            while (true) {
                try {
                    synchronized (monitor) {
                        monitor.wait();
                        sendColor(red, green, blue);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void run() {
            workerLoop();
        }

        void sendColor(byte red, byte green, byte blue) {
            try {
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress addr = InetAddress.getByName("192.168.1.236");
                byte[] bytes = new byte[3];
                bytes[0] = red;
                bytes[1] = green;
                bytes[2] = blue;
                DatagramPacket packet = new DatagramPacket(bytes, 3, addr, 25000);
                clientSocket.send(packet);
                clientSocket.close();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class ColorSeekBarChange implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            byte red = (byte) Math.min(redBar.getProgress(), 255);
            byte green = (byte) Math.min(greenBar.getProgress(), 255);
            byte blue = (byte) Math.min(blueBar.getProgress(), 255);
            updater.setColor(red, green, blue);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}