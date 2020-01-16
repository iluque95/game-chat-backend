package com.world.raisen;

import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{

    // Game server and client pool threads.
    static private Thread gs;
    static public ServerHandler sh;
    static public ClientPool cp;

    public static void main(String[] args) throws IOException
    {

        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        ss.setReuseAddress(true);

        Socket s;

        System.out.println("Started server on port 1234. Listening new connections.");

        cp = new ClientPool();
        sh = null;

        // running infinite loop for getting
        // client request
        while (true)
        {

            // Accept the incoming request
            s = ss.accept();

            s.setKeepAlive(true);
            s.setTcpNoDelay(true);

            System.out.println("New client request received : " + s.getInetAddress().getHostAddress().toString());


            if (s.getInetAddress().getHostAddress().toString() == "XX.XX.XX.XX")
            {
                sh = new ServerHandler(s);

                // Create a new Thread with this object.
                gs = new Thread(sh);

                // start the thread.
                gs.start();
            }
            else
            {
                if (cp != null)
                {
                    cp.addClient(s);
                }
            }

            System.out.println("User handled by a new thread.");

        }
    }
}