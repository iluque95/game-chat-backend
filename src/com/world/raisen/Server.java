package com.world.raisen;

import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{

    static public HashMap<Integer, ClientHandler> map = new HashMap<>();
    static public Vector<List<Integer>> world_grid = new Vector<List<Integer>>();

    public static void main(String[] args) throws IOException
    {
        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        Protocol p = new Protocol();

        System.out.println("Started server on port 1234. Listening new connections.");

        // running infinite loop for getting
        // client request
        while (true)
        {

            // Accept the incoming request
            s = ss.accept();

            s.setKeepAlive(true);
            s.setTcpNoDelay(true);

            System.out.println("New client request received : " + s.getRemoteSocketAddress().toString());

            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler(s, dis, dos, p);

            // Create a new Thread with this object.
            Thread t = new Thread(mtch);

            // start the thread.
            t.start();
        }
    }
}