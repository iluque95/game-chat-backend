package com.world.raisen;

import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{


    private static Server instance;

    // Game server and client pool threads.
    private Thread gs;
    private ServerHandler sh;
    private ClientPool cp;


    Server()
    {
        cp = new ClientPool();
        sh = null;
    }

    public static Server getInstance()
    {
        if (instance == null)
            instance = new Server();

        return instance;
    }

    public void newGameServerConnection(Socket s)
    {
        sh = new ServerHandler(s);

        // Create a new Thread with this object.
        gs = new Thread(sh);

        // start the thread.
        gs.start();
    }

    public void newClientConnection(Socket s)
    {
        if (cp != null) {
            cp.addClient(s);
        }
    }

    void updatePosition(int uuid, int map, byte x, byte y)
    {
        cp.updatePosition(uuid, map, x, y);
    }

    void removeClient(int uuid)
    {
        cp.removeClient(uuid);
    }

    void handleTokenConfirmation(int uuid, boolean valid)
    {
        cp.handleTokenConfirmation(uuid, valid);
    }

    public <E> void writeToGameServer(E val)
    {
        sh.write(val);
    }

    public void sendToGameServer()
    {
        sh.send();
    }

}