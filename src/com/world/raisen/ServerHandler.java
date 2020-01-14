package com.world.raisen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerHandler implements Runnable {

    private class Protocol {

        // Backend updates.
        private static final int NEW_USER = 0x5000;
        private static final int UPDATE_USER = 0x5001;
        private static final int DELETE_USER = 0x5002;
        private static final int QUIT_USER = 0x5003;

        private static final int CONFIG_PARAMS = 0x5100;


        void handleIncomingData(ServerHandler c) throws IOException
        {
            try
            {
                int op = c.dis.readInt();

                switch (op)
                {
                    case NEW_USER:

                        break;

                    case UPDATE_USER:

                        break;

                    case DELETE_USER:

                        break;


                    case CONFIG_PARAMS:

                        setMaps(100);
                        setMax_users(200);
                        setFov_x(10);
                        setFov_y(5);
                        setAreas((getFov_x()*getFov_y()) / getMaps());

                        break;

                    default:
                        System.out.println("Invalid packet received from game server RWAO");
                        break;

                }
            }catch (IOException e)
            {
                throw e;
            }
        }
    }

    private DataInputStream dis;
    private DataOutputStream dos;
    Socket s;
    Protocol p;
    int maps;
    int max_users;
    int fov_x; // field of view.
    int fov_y;

    int areas;

    private volatile boolean running = true;

    // constructor
    public ServerHandler(Socket s) {
        try
        {
            this.dis = new DataInputStream(s.getInputStream());
            this.dos = new DataOutputStream(s.getOutputStream());
            this.s = s;
            this.p = new Protocol();

        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void setMaps(int maps) {
        this.maps = maps;
    }

    public void setMax_users(int max_users) {
        this.max_users = max_users;
    }

    public int getMaps() {
        return maps;
    }

    public int getMax_users() {
        return max_users;
    }

    public int getFov_x() {
        return fov_x;
    }

    public int getFov_y() {
        return fov_y;
    }

    public void setFov_x(int fov_x) {
        this.fov_x = fov_x;
    }

    public void setFov_y(int fov_y) {
        this.fov_y = fov_y;
    }

    public int getAreas() {
        return areas;
    }

    public void setAreas(int areas) {
        this.areas = areas;
    }

    @Override
    public void run() {

        String received;
        while (running)
        {
            try
            {

                p.handleIncomingData(this);

            } catch (IOException e) {
                running = false;
                System.out.println("Disconnected user.");
            }

        }

        // Thread killed

        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();
            this.s.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
