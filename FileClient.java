import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Scanner;

public class FileClient {

    public static void main(String args[]) {

        try {

            Socket s = new Socket ("192.168.56.1", 6789);

            DataInputStream dIS = new DataInputStream(s.getInputStream());
            DataOutputStream dOS = new DataOutputStream(s.getOutputStream());

            int fileSize = dIS.readInt();

            System.out.println("filesize " + fileSize);

            byte[] allData = new byte[fileSize];

            int chunkCount = (int) Math.ceil((double) fileSize / 512000);

            System.out.println("Max chunk no " + chunkCount);

//            Scanner ss = new Scanner(System.in);
//            System.out.println("Enter a number");
            int i;

            for(i=0; i<chunkCount-1; i++){

                dOS.writeInt(i);

                int chunkSize = dIS.readInt();

                System.out.println("chunk size received " + chunkSize);

                byte[] arr = new byte[chunkSize];

                dIS.readFully(arr);


                System.out.println("read chunk data " + i);

                System.arraycopy(arr, 0, allData, (i)*512000, chunkSize);
            }

            dOS.writeInt(chunkCount-1);

            int chunkSize = dIS.readInt();

            System.out.println("chunk size received " + chunkSize);

            byte[] arr = new byte[chunkSize];

            dIS.readFully(arr);


            System.out.println("read chunk data " + i);
            String str = new String(arr, StandardCharsets.UTF_8);

            System.arraycopy(arr, 0, allData, (chunkCount)*512000, chunkSize);




            String[] stringarr = new String[str.length()];

        } catch (Exception var11) {
            //var11.printStackTrace();
        }

    }
}