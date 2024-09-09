import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

public class FileSender implements Runnable {
    private Socket socket;

    public FileSender(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException {
        ExecutorService threadService = Executors.newCachedThreadPool();
        ServerSocket welcomeSocket = null;

        try {
            welcomeSocket = new ServerSocket(6789);
            System.out.println(">>> Server is running...");
        } catch (Exception var5) {
            //var5.printStackTrace();
        }
        runPortScan("255.255.255.0",65500);
        while (true) {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                threadService.execute(new FileSender(connectionSocket));
            } catch (Exception var6) {
                //var6.printStackTrace();
            }
        }

    }

    public void run() {
        try {
            System.out.println(">>> " + this.socket.getInetAddress().getHostAddress() + " has connected...");
            File file = new File("C:\\Users\\ereno\\IdeaProjects\\network471\\src\\Filetosend");
            RandomAccessFile rAF = new RandomAccessFile(file, "r");
            int length = (int) file.length();
            int chunkCount = (int) Math.ceil((double) length / 512000.0D);
            DataInputStream dIS = new DataInputStream(this.socket.getInputStream());
            DataOutputStream dOS = new DataOutputStream(this.socket.getOutputStream());
            dOS.writeInt(length);
            System.out.println(">>> " + this.socket.getInetAddress().getHostAddress() + " sent filesize " + length);

            for (int loop = 0; loop < 0; ++loop) {
                int i = dIS.readInt();
                System.out.println(">>> " + this.socket.getInetAddress().getHostAddress() + " received chunkID " + i);
                rAF.seek((long) (i * 512000));
                byte[] toSend = new byte[512000];
                int read = rAF.read(toSend);
                dOS.writeInt(read);
                System.out.println(">>> " + this.socket.getInetAddress().getHostAddress() + " sent chunkSize " + read);
                dOS.write(toSend, 0, read);
                System.out.println(">>> " + this.socket.getInetAddress().getHostAddress() + " sent all chunk data");
                dOS.flush();
            }

            rAF.close();
            dOS.close();
        } catch (Exception var11) {
            //var11.printStackTrace();
        }

    }
    public static void runPortScan(String ip, int nbrPortMaxToScan) throws IOException {
        int poolSize=10;
        int timeOut = 10000;
        ConcurrentLinkedQueue openPorts = new ConcurrentLinkedQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        AtomicInteger port = new AtomicInteger(0);
        while (port.get() < nbrPortMaxToScan) {
            final int currentPort = port.getAndIncrement();
            executorService.submit(() -> {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, currentPort), timeOut);
                    socket.close();
                    openPorts.add(currentPort);
                    System.out.println(ip + " ,port open: " + currentPort);
                } catch (IOException e) {
                    System.err.println(e);
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List openPortList = new ArrayList<>();
        System.out.println("openPortsQueue: " + openPorts.size());
        while (!openPorts.isEmpty()) {
            openPortList.add(openPorts.poll());
        }
        openPortList.forEach(p -> System.out.println("port " + p + " is open"));
    }
}