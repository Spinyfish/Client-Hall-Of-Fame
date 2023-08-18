package me.jinthium.straight.api.irc;

import ai.production.backend.messageHandler.MessageHandler;
import ai.production.backend.messageHandler.container.MessageCommand;
import ai.production.backend.messageHandler.container.MessageDirection;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.RunGameLoopEvent;
import me.jinthium.straight.impl.utils.ChatUtil;
import me.jinthium.straight.impl.utils.Multithreading;
import me.jinthium.straight.impl.utils.player.MovementUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.RunGameLoopEvent;
import me.jinthium.straight.impl.utils.ChatUtil;
import me.jinthium.straight.impl.utils.Multithreading;
import me.jinthium.straight.impl.utils.player.MovementUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public enum IRCClient {
    INSTANCE;

    private Socket socket;
    private ConnectedClient connectedClient;

    public void connect() {
        try {
            if(socket != null && socket.isConnected()) {
                socket.close();
                connectedClient.getSocket().close();
                connectedClient = null;
                socket = null;
            }

            socket = new Socket("45.128.232.209", 8081);
            connectedClient = new ConnectedClient(socket);
            new Thread(connectedClient).start();
            ChatUtil.printSystem("[!] Connected to IRC");

        } catch (IOException e) {
            ChatUtil.printSystem("[!] Failed to connect");
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
//        if()

        ConnectedClient.INSTANCE.sendMessageObject(MessageHandler.buildMessage(Client.INSTANCE.getUser().getUsername(), MessageDirection.CLIENT_TO_SERVER, MessageCommand.IRC_MESSAGE, message));
    }


    public void disconnect(){
        try{
            if(this.socket.isConnected()) {
                IRCClient.INSTANCE.sendMessage("-client-disconnect");
                socket.close();
                connectedClient.getSocket().close();
                socket = null;
                connectedClient = null;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }





    /*
    private void disconnect() {
        try {
            connectedChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getConnectedSocket() {
        return connectedChannel.socket();
    }

     */
}