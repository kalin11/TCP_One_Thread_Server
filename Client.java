package hopeLast;

import OneMoreTry.Command;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        boolean flag = true;
        Selector selector = Selector.open();
        SocketChannel clientSocket = SocketChannel.open();
        clientSocket.configureBlocking(false);
        clientSocket.connect(new InetSocketAddress(Server.host, Server.port));

        clientSocket.register(selector, SelectionKey.OP_CONNECT);

        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                SocketChannel client = (SocketChannel) key.channel();

                if (key.isConnectable()){
                    if (client.isConnectionPending()){
                        try{
                            client.finishConnect();
                        }catch (IOException e){
                            System.out.println("IO");
                        }
                    }
                    client.register(selector, SelectionKey.OP_WRITE);
                    continue;
                }
                if (key.isWritable()){
//                    while (flag) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                        Command command = new Command(bufferedReader.readLine());
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
                        outputStream.writeObject(command);
                        outputStream.flush();
                        client.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
                        clientSocket.register(selector, SelectionKey.OP_READ);
//                        flag = false;
//                    }
                }
                if (key.isReadable()){
                    client = (SocketChannel) key.channel();
                    ByteBuffer data = ByteBuffer.allocate(1024);
                    client.read(data);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    Object o = objectInputStream.readObject();
                    System.out.println("server's answer - " + o);
                    clientSocket.register(selector, SelectionKey.OP_WRITE);
                }
            }
        }
    }
}
