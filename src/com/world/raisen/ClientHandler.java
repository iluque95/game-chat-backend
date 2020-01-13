package com.world.raisen;


import java.io.*;
import java.net.Socket;

// ClientHandler class
class ClientHandler implements Runnable
{

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
                         DataInputStream dis, DataOutputStream dos, Protocol p) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
        this.p = p;
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