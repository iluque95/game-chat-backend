package com.world.raisen;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 1234;

    public static void main(String args[]) throws UnknownHostException, IOException
    {
        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort);

        // obtaining input and out streams
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {

                    // read the message to deliver.
                    String msg = scn.nextLine();

                    try {

                        ByteBuffer buffer = ByteBuffer.allocate(2);

                        buffer.putShort((short)0x7510);


                        // write on the output stream
                        dos.write(buffer.array());
                        //dos.writeUTF(msg);

                        System.out.println(dos.toString());

                        dos.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                        break;

                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}