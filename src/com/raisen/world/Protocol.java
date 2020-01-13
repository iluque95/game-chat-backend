package com.raisen.world;

import java.io.DataInputStream;
import java.io.IOException;

public class Protocol {

    private static final int NEW_USER = 0x5000;
    private static final int UPDATE_USER = 0x5001;
    private static final int DELETE_USER = 0x5002;


    void handleIncomingData(DataInputStream ds) throws IOException
    {

        try
        {
            int op = ds.readInt();

            switch (op)
            {
                case NEW_USER:
                    System.out.println("NOTHING");
                    break;

                case UPDATE_USER:
                    System.out.println("PUTA");
                    break;

                case DELETE_USER:
                    break;

                default:
                    System.out.print("Invalid packet received !");
                    break;

            }
        }catch (IOException e)
        {
            throw e;
        }
    }
}
