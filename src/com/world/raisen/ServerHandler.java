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

    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    Protocol p;

    private volatile boolean running = true;

    // constructor
    public ServerHandler(Socket s,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.p = new Protocol();
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
