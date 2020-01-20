package com.world.raisen;


import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

// ClientHandler class
class ClientPool
{


    // HashMap --> <UUID, Client>
    // Vector --> <Map, UUID>

    static public HashMap<Integer, ClientHandler> map = new HashMap<>();
    static public Vector<List<Integer>> world_grid = new Vector<List<Integer>>();

    void addClient(Socket s)
    {

        ClientHandler c = new ClientHandler(s);

        Thread t = new Thread(c);

        t.start();
    }

    void removeClient(int uuid)
    {

        // Delete from the data structs.
        if (uuid != 0)
        {
            ClientHandler c = map.remove(uuid);

            world_grid.elementAt(c.wp.map).remove(uuid);

            c.disconnectUser();
        }

    }

    void updatePosition(int uuid, int map, byte x, byte y)
    {
        ClientHandler c = this.map.get(map);

        c.wp.setMap(map);
        c.wp.setX(x);
        c.wp.setY(y);

        // TODO: Update in world_grid vector if user switch to other map.
    }

    void handleTokenConfirmation(int uuid, boolean valid)
    {
        ClientHandler c = map.get(uuid);

        if (valid && c != null)
        {
            c.pending_validation = false;
        }
        else
        {
            removeClient(uuid);
        }
    }


    private class Protocol
    {


        private static final int PARAMS = 0x5200;

        // Client I/O.
        private static final int TOINDEX = 0x5500;
        private static final int TOAREA = 0x5501;
        private static final int TOMAP = 0x5502;
        private static final int BROADCAST = 0x5555;

        private static final int VALIDATE_TOKEN = 0x5600;


        void handleIncomingData(ClientHandler c) throws IOException {
            try {

                byte[] b = new byte[2048];

                c.dis.read(b, 0, 2048);

                int op = 50;//c.dis.readInt();

                System.out.println("Opcode packet: " + Integer.toHexString(op));
                //System.out.println("Message: " + c.dis.readUTF());

                switch (op) {

                    case TOINDEX:

                        int from = c.dis.readInt();
                        int to = c.dis.readInt();
                        String message = c.dis.readUTF();

                        ClientHandler ch = map.get(to);


                        if (c != null && !c.pending_validation && ch != null) {
                            ch.write(from);
                            ch.writeUTF(message);
                            ch.send();
                        }

                        break;


                    case TOMAP:

                        from = c.dis.readInt();
                        int pos_map = c.dis.readInt();
                        message = c.dis.readUTF();

                        if (c != null && !c.pending_validation)
                        {

                            for (Integer i : world_grid.get(pos_map)) {

                                ClientHandler tmp = map.get(i);

                                if (tmp != c)
                                {
                                    tmp.write(from);
                                    tmp.writeUTF(message);
                                    tmp.send();
                                }
                            }
                        }

                        break;

                    case BROADCAST:

                        from = c.dis.readInt();
                        message = c.dis.readUTF();

                        for (ClientHandler cl : map.values()) {
                            if (cl != c) {
                                cl.write(from);
                                cl.writeUTF(message);
                                cl.send();
                            }
                        }

                        break;

                    case PARAMS:

                        from = c.dis.readInt();
                        c.token = c.dis.readInt();

                        Main.server.writeToGameServer(VALIDATE_TOKEN);
                        Main.server.writeToGameServer(from);
                        Main.server.writeToGameServer(c.token);

                        Main.server.sendToGameServer();


                    default:
                        System.out.println("Invalid packet received from user " + c.getName());
                        break;

                }
            } catch (IOException e) {
                //e.printStackTrace();
                throw e;
            }
        }

    }

    private class WorldPos
    {
        private int map;
        private byte x;
        private byte y;

        WorldPos(int map, byte x, byte y)
        {
            this.map = map;
            this.x = x;
            this.y = y;
        }

        void setMap(int map) {
            this.map = map;
        }

        void setX(byte x) {
            this.x = x;
        }

        void setY(byte y) {
            this.y = y;
        }

        int getMap() {
            return map;
        }

        byte getX() {
            return x;
        }

        byte getY() {
            return y;
        }

    }

    private class ClientHandler implements Runnable
    {
        private volatile boolean running = true;

        int uuid;
        private String name;
        private DataInputStream dis;
        private DataOutputStream dos;
        Socket s;
        Protocol p;
        WorldPos wp;

        boolean pending_validation;
        int token;

        // constructor
        ClientHandler(Socket s)
        {

            try
            {
                this.dis = new DataInputStream(s.getInputStream());
                this.dos = new DataOutputStream(s.getOutputStream());
                this.s = s;
                this.p = new Protocol();
                this.uuid = 0;
                this.pending_validation = true;
                this.token = 0;

            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public String getName() {
            return this.name;
        }

        public int getId() {
            return this.uuid;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(int id) {
            this.uuid = id;
        }

        public <E> void write(E val)
        {
            try {
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
                oos.writeObject(val);
                oos.flush();
                byte[] bytes = bytesOut.toByteArray();
                bytesOut.close();
                oos.close();

                dos.write(bytes);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }


        public void writeUTF(String str)
        {
            try {
                dos.writeUTF(str);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }

        public void send()
        {
            try {
                this.dos.flush();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        public void disconnectUser()
        {
            running = false;

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


        @Override
        public void run()
        {
            int line;

            try
            {
                while (running)
            {
                p.handleIncomingData(this);
            }

                System.out.println("Disconnected user.");
            }
            catch (IOException e)
            {
                System.out.println("Error. Killing thread.");
            }

            removeClient(getId());
        }
    }
}