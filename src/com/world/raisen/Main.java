package com.world.raisen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main
{

    public static  Server server;

    public static void main(String args[]) throws IOException
    {

        // server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        ss.setReuseAddress(true);

        Socket s;

        server = Server.getInstance();

        System.out.println("Started server on port 1234. Listening new connections.");


        // running infinite loop for getting
        // client request
        while (true) {

            // Accept the incoming request
            s = ss.accept();

            s.setKeepAlive(true);

            System.out.println("New client request received : " + s.getInetAddress().getHostAddress().toString());


            if (s.getInetAddress().getHostAddress().toString() == "XX.XX.XX.XX")
            {
                server.newGameServerConnection(s);

            } else {
                server.newClientConnection(s);
            }

            System.out.println("User handled by a new thread.");
        }

    }
}
