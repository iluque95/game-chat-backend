package com.world.raisen;

import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{

    // Game server and client pool threads.
    static private Thread gs;
    static private ClientPool cp;

    public static void main(String[] args) throws IOException
    {

        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        ss.setReuseAddress(true);

        Socket s;

        System.out.println("Started server on port 1234. Listening new connections.");

        cp = new ClientPool();

        // running infinite loop for getting
        // client request
        while (true)
        {

            // Accept the incoming request
            s = ss.accept();

            s.setKeepAlive(true);
            s.setTcpNoDelay(true);

            System.out.println("New client request received : " + s.getInetAddress().getHostAddress().toString());

            // obtain input and output streams
            //DataInputStream dis = new DataInputStream(s.getInputStream());
            //DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            System.out.println("Creating a new handler for him...");


            if (s.getRemoteSocketAddress().toString() == "XX.XX.XX.XX")
            {
                ServerHandler sh = new ServerHandler(s);

                // Create a new Thread with this object.
                gs = new Thread(sh);

                // start the thread.
                gs.start();
            }
            else
            {
                // TODO: Add client to pool

                if (cp != null)
                {
                    cp.addClient(s);
                }

            }

            System.out.println("Created.");

        }
    }
}