package com.frio.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by frio on 16-6-12.
 */
public class Server {
    private static ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private static ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

    private static void deal(SelectionKey selectionKey) throws IOException {
        if(selectionKey.isAcceptable()){
            ServerSocketChannel socketChannel = (ServerSocketChannel) selectionKey.channel();
            System.out.println("Socket channel can be accepted");
            SocketChannel sc = socketChannel.accept();
            System.out.println("Accept channel and regists to selector");
            sc.configureBlocking(false);
            sc.register(selectionKey.selector(), SelectionKey.OP_READ|SelectionKey.OP_WRITE);
        }else if(selectionKey.isReadable()){
            SocketChannel sc = (SocketChannel) selectionKey.channel();
            if(!sc.isOpen()){
                System.out.println("connection is closed,so we do close operation");
                sc.close();
                return;
            }
            readBuffer.clear();
            try {
                sc.read(readBuffer);
            }catch(IOException e){
                System.out.println("connection closed, so we close SocketChannel");
                sc.close();
                return;
            }
            System.out.println(new String(readBuffer.array()));
            writeBuffer.clear();
            readBuffer.flip();
            writeBuffer.put(new String(readBuffer.array()).toUpperCase().getBytes());
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }else if(selectionKey.isWritable()){
            writeBuffer.flip();
            SocketChannel sc = (SocketChannel) selectionKey.channel();
            while(writeBuffer.hasRemaining()){
                System.out.println(String.format("We have writtern a message to client! %s", new String(writeBuffer.array())));
                sc.write(writeBuffer);
            }
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket socket = serverSocketChannel.socket();
        socket.bind(new InetSocketAddress(9999));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while(true){
            //avoid do a a lot serverSocketChannel.accept() is not null, we use selector
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while(iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                deal(selectionKey);
            }
        }
    }
}
