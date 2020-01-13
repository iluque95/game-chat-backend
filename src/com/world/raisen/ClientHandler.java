package com.world.raisen;


import java.io.*;
import java.net.Socket;

// ClientHandler class
class ClientHandler implements Runnable
{

    private class Protocol {

        // Client I/O.
        private static final int TOINDEX = 0x5100;
        private static final int TOAREA = 0x5101;
        private static final int TOMAP = 0x5102;
        private static final int BROADCAST = 0x5555;


        void handleIncomingData(ClientHandler c) throws IOException
        {
            try
            {
                int op = c.dis.readInt();

                switch (op)
                {

                    case TOINDEX:

                        int from = c.dis.readInt();
                        int to = c.dis.readInt();
                        String message = c.dis.readUTF();


                        ClientHandler ch = Server.map.get(to);

                        if (ch != null)
                        {
                            ch.write(from);
                            ch.writeUTF(message);
                            ch.send();
                        }

                        break;


                    case TOMAP:

                        from = c.dis.readInt();
                        int map = c.dis.readInt();
                        message = c.dis.readUTF();

                        for (Integer i : Server.world_grid.get(map))
                        {
                            ch = Server.map.get(i);

                            ch.write(from);
                            ch.writeUTF(message);
                            ch.send();
                        }

                    case BROADCAST:

                        from = c.dis.readInt();
                        message = c.dis.readUTF();

                        for (ClientHandler cl : Server.map.values())
                        {
                            if (cl != c)
                            {
                                cl.write(from);
                                cl.writeUTF(message);
                                cl.send();
                            }
                        }

                        break;

                    default:
                        System.out.println("Invalid packet received from user " + c.getName());
                        break;

                }
            }catch (IOException e)
            {
                throw e;
            }
        }
    }

    private class WorldPos
    {
        private int map;
        private int x;
        private int y;

        WorldPos(int map, int x, int y)
        {
            this.map = map;
            this.x = x;
            this.y = y;
        }

        void setMap (int map) {this.map = map;}
        void setX (int x) {this.x = x;}
        void setY (int y) {this.y = y;}

        int getMap() {return map;}
        int getX() {return x;}
        int getY() {return y;}

    }

    int id;
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    Protocol p;
    WorldPos wp;

    private volatile boolean running = true;

    // constructor
    public ClientHandler(Socket s,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.p = new Protocol();
        this.id = 0;
    }

    public String getName()
    {
        return this.name;
    }

    public int getId()
    {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public <E> void write(E val)
    {
        try
        {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
            oos.writeObject(val);
            oos.flush();
            byte[] bytes = bytesOut.toByteArray();
            bytesOut.close();
            oos.close();

            dos.write(bytes);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }

    }


    public void writeUTF(String str)
    {
        try
        {
            dos.writeUTF(str);
        }catch(IOException e)
        {
            System.out.println(e.getMessage());
        }

    }

    public void send()
    {
        try
        {
            this.dos.flush();

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

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