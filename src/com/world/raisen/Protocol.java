package com.world.raisen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Protocol {

    // Backend updates.
    private static final int NEW_USER = 0x5000;
    private static final int UPDATE_USER = 0x5001;
    private static final int DELETE_USER = 0x5002;

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
                case NEW_USER:

                    c.setId(c.dis.readInt());
                    c.setName(c.dis.readUTF());

                    break;

                case UPDATE_USER:

                    Server.map.get(c.getId());

                    break;

                case DELETE_USER:

                    break;


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
