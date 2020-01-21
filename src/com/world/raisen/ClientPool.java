package com.world.raisen;


import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    void removeClient(ClientHandler c)
    {
        c.disconnectUser();
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

                byte[] buffer = new byte[1024];

                int len = c.dis.read(buffer, 0, buffer.length);

                if (len < 0)
                {
                    removeClient(c);
                    return;
                }


                Packet packet = new Packet(buffer, len);

                //System.out.println("Input stream: " + Arrays.toString(buffer) + ", size: " + len + ", Available bytes: " + c.dis.available());

                short op = packet.readShort();

                System.out.println("Opcode packet: " + Integer.toHexString(op));

                switch (op) {

                    case TOINDEX:

                        int from = packet.readInt();
                        int to = packet.readInt();

                        ClientHandler ch = map.get(to);


                        if (c != null && !c.pending_validation && ch != null) {
                            ch.write(packet.read());
                            ch.send();
                        }

                        break;


                    case TOMAP:

                        int pos_map = packet.readInt();

                        if (c != null && !c.pending_validation)
                        {

                            for (Integer i : world_grid.get(pos_map)) {

                                ClientHandler tmp = map.get(i);

                                if (tmp != c)
                                {
                                    tmp.write(packet.read());
                                    tmp.send();
                                }
                            }
                        }

                        break;

                    case BROADCAST:

                        for (ClientHandler cl : map.values()) {
                            if (cl != c) {
                                cl.write(packet.read());
                                cl.send();
                            }
                        }

                        break;

                    case PARAMS:

                        Main.server.writeToGameServer(packet.read());

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