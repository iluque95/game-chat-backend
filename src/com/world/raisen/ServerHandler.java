package com.world.raisen;

import java.io.*;
import java.net.Socket;

public class ServerHandler implements Runnable {

    private class Protocol {

        // Backend updates.
        private static final int NEW_USER = 0x5000;
        private static final int UPDATE_USER = 0x5001;
        private static final int DELETE_USER = 0x5002;

        private static final int CONFIG_PARAMS = 0x5100;

        private static final int TOKEN_CONFIRM = 0x5601;


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

                        int uuid = c.dis.readInt(), map = c.dis.readInt();
                        byte x = c.dis.readByte(), y = c.dis.readByte();

                        Main.server.updatePosition(uuid, map, x, y);

                        break;

                    case DELETE_USER:

                        int to = c.dis.readInt();

                        Main.server.removeClient(to);

                        break;


                    case CONFIG_PARAMS:


                        break;

                    case TOKEN_CONFIRM:

                        to = c.dis.readInt();
                        boolean valid = c.dis.readBoolean();

                        Main.server.handleTokenConfirmation(to, valid);

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

    private volatile boolean running = true;

    // constructor
    public ServerHandler(Socket s)
    {
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

    public <E> void write(E val)
    {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
            oos.writeObject(val);
            oos.flush();
            byte[] bytes = bytesOut.toByteArray();
            bytesOut.close();
            oos.close();

            dos.write(bytes);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void writeUTF(String str)
    {
        try {
            dos.writeUTF(str);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public void send()
    {
        try {
            this.dos.flush();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void run()
    {

        while (running)
        {
            try
            {

                p.handleIncomingData(this);

            } catch (IOException e) {
                running = false;
                System.out.println("Game server RWAO closes connection.");
            }

        }


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