package hopeLast;

import OneMoreTry.Command;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Server {
    static String host = "localhost";
    static int port = 9000;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        boolean flag = true;
        Selector selector = Selector.open();
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.socket().bind(new InetSocketAddress(host,port));
        server.register(selector, SelectionKey.OP_ACCEPT);
        LinkedList<Command> l = new LinkedList<>();
            while (true){
                SocketChannel client;
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SocketChannel channel = null;
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()){
                        client = server.accept();
                        System.out.println("подключился пользователь " + client.toString().substring(42));
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    }
                    if (key.isReadable()) {
                        channel = (SocketChannel) key.channel();
                        ByteBuffer data = ByteBuffer.allocate(1024);
                        channel.read(data);
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.array());
                        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                        Command cmd = (Command) objectInputStream.readObject();
                        System.out.println("client's message - " + cmd);
                        l.add(cmd);
//                        ByteBuffer sendData = ByteBuffer.allocate(1024);
                        channel = (SocketChannel) key.channel();
                        if (channel != null) {
//                            channel.write(sendData);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
                            outputStream.writeObject(l);
                            outputStream.flush();
                            channel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
                            System.out.println("list was sent");
                        }
                    }

                            //получили команду, в зависимости от нее
                            //отправляем дальше на исполнение
                            //получаем какой-то результат
                            //получаем его
                            //а дальше отправялем клиенту

//                            channel.write(sendData);
//                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
//                            outputStream.writeObject(l);
//                            outputStream.flush();
//                            channel.write(ByteBuffer.wrap(Arrays.toString(l.toArray()).getBytes()));
                    System.out.println("сервер ожидает дальнейших действий.");
                }
            }
    }
//    private static void getSocketObject(SocketChannel channel) throws IOException, ClassNotFoundException {
//        ByteBuffer data = ByteBuffer.allocate(1024);
//        channel.read(data);
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.array());
//        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//        Command cmd = (Command) objectInputStream.readObject();
//        System.out.println(cmd);
//    }
}
