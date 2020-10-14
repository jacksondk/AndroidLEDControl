package dk.scicomp.ledcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

class SendUpdatesWorker implements Runnable {
    boolean on;
    RGB first_color = new RGB();
    RGB second_color = new RGB();
    Object monitor = new Object();

    public void setColor(byte red, byte green, byte blue) {
        first_color.red = red;
        first_color.green = green;
        first_color.blue = blue;
    }

    public void setColor2(byte red, byte green, byte blue) {
        second_color.red = red;
        second_color.green = green;
        second_color.blue = blue;
    }

    public void setOn(boolean on) {
        this.on = on;
        update();
    }

    public void update() {
        synchronized (monitor) {
            monitor.notify();
        }
    }

    void workerLoop() {
        while (true) {
            try {
                synchronized (monitor) {
                    monitor.wait();
                    sendTwoColors(first_color, second_color);
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

    void sendTwoColors(RGB first_color, RGB second_color) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress addr = InetAddress.getByName("192.168.1.236");
            byte[] bytes = new byte[1 + 2 * 3];
            bytes[0] = 2; // Two colors
            if (on) {
                bytes[1] = first_color.red;
                bytes[2] = first_color.green;
                bytes[3] = first_color.blue;
                bytes[4] = second_color.red;
                bytes[5] = second_color.green;
                bytes[6] = second_color.blue;
            } else {
                // Java initializes arrays to zero - so no work
            }
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr, 25000);
            clientSocket.send(packet);
            clientSocket.close();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
