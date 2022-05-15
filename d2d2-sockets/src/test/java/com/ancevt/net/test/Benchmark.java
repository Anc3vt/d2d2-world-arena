
package com.ancevt.net.test;

import com.ancevt.commons.concurrent.Async;
import com.ancevt.commons.concurrent.Lock;
import com.ancevt.commons.unix.UnixDisplay;
import com.ancevt.net.CloseStatus;
import com.ancevt.net.connection.ConnectionListener;
import com.ancevt.net.connection.IConnection;
import com.ancevt.net.connection.TcpB254Connection;
import com.ancevt.net.connection.TcpConnection;
import com.ancevt.net.server.IServer;
import com.ancevt.net.server.ServerListener;
import com.ancevt.net.server.TcpB254Server;
import com.ancevt.net.server.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ancevt.commons.unix.UnixDisplay.debug;

@Slf4j
public class Benchmark {

    public static void main(String[] args) {
        UnixDisplay.setEnabled(true);

        final int totalTimes = 1000;
        final int messageCount = 100000;
        final int messageSize = 1000;
        final int benchmarkCount = 1;

        List<Double> tcp = new ArrayList<>();
        List<Double> tcpB254 = new ArrayList<>();

        long oldTime = 0;
        double average;

        for (int j = 1; j <= totalTimes; j++) {

            debug("Benchmark:39: <a><Y>" + j + "/" + totalTimes);



            Async.wait(25, TimeUnit.MILLISECONDS);
            debug("Benchmark: <y>tcp b254...");
            oldTime = System.currentTimeMillis();
            List<Long> listTcpB245 = new ArrayList<>();
            for (int i = 0; i < benchmarkCount; i++) {
                listTcpB245.add(benchmark1(TcpB254Server.create(), TcpB254Connection.create(), messageCount, messageSize));
            }
            average = calculateAverageLong(listTcpB245);
            tcpB254.add(average);
            debug("Benchmark: <y>tcp b254 total time: " + (System.currentTimeMillis() - oldTime) + ", average: " + average);



            Async.wait(25, TimeUnit.MILLISECONDS);
            debug("Benchmark: <g>tcp...");
            oldTime = System.currentTimeMillis();
            List<Long> listTcp = new ArrayList<>();
            for (int i = 0; i < benchmarkCount; i++) {
                listTcp.add(benchmark1(TcpServer.create(), TcpConnection.create(), messageCount, messageSize));
            }
            average = calculateAverageLong(listTcp);
            tcp.add(average);
            debug("Benchmark: <g>tcp total time: " + (System.currentTimeMillis() - oldTime) + ", average: " + average);




        }

        double tcpAverage = calculateAverageDouble(tcp);
        double tcpB254Average = calculateAverageDouble(tcpB254);


        debug("Benchmark:71: <a><G>tcp: " + tcpAverage + ", tcpB254: " + tcpB254Average);

    }

    private static double calculateAverageDouble(@NotNull List<Double> marks) {
        return marks.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }

    private static double calculateAverageLong(@NotNull List<Long> marks) {
        return marks.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }


    private static long benchmark1(IServer server, IConnection connection, int messageCount, int messageSize) {

        List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < messageCount; i++) {
            list.add(createBytes(messageSize));
        }

        long oldTime = System.currentTimeMillis();

        Lock lock = new Lock();

        server.addServerListener(new ServerListener() {
            @Override
            public void serverStarted() {

            }

            @Override
            public void connectionAccepted(IConnection connectionWithClient) {

            }

            @Override
            public void connectionClosed(IConnection connectionWithClient, CloseStatus status) {

            }

            @Override
            public void connectionBytesReceived(IConnection connectionWithClient, byte[] bytes) {

            }

            @Override
            public void serverClosed(CloseStatus status) {

            }

            @Override
            public void connectionEstablished(IConnection connectionWithClient) {
                debug("Benchmark:132: Server <g>connectionEstablished");
                list.forEach(connectionWithClient::send);
            }
        });
        server.asyncListenAndAwait("0.0.0.0", 7777, 1, TimeUnit.SECONDS);

        final List<byte[]> result = new ArrayList<>();

        connection.addConnectionListener(new ConnectionListener() {


            @Override
            public void connectionEstablished() {

            }

            @Override
            public void connectionBytesReceived(byte[] bytes) {

                if (bytes.length != messageSize) {
                    debug("<a><R> " + bytes.length);
                }

                result.add(bytes);
                if (result.size() >= messageCount) {
                    connection.closeIfOpen();
                    server.close();
                    lock.unlockIfLocked();
                }
            }

            @Override
            public void connectionClosed(CloseStatus status) {
                connection.removeConnectionListener(this);
            }
        });
        connection.asyncConnectAndAwait("127.0.0.1", 7777, 1, TimeUnit.SECONDS);

        lock.lock(10, TimeUnit.SECONDS);

        if (result.size() != messageCount) {
            debug("<a><R>result size: " + result.size() + " of  " + messageCount);
        }


        long diff = System.currentTimeMillis() - oldTime;
        return diff;
    }


    private static byte @NotNull [] createBytes(int size) {
        var b = new byte[size];
        Arrays.fill(b, (byte) 1);
        return b;
    }
}
