package latihan.praktikum2.io;

import java.io.*;
import java.net.*;

public class ChatServer {

    public static void main(String[] x) throws Exception {
        Boolean jalan = true;

        // 1. Input Konfigurasi
        BufferedReader input
                = new BufferedReader(
                        new InputStreamReader(System.in));

        System.out.println("Masukkan nama : ");
        String nama = input.readLine();
        System.out.println("Halo " + nama);

        System.out.println("Masukkan port Anda : ");
        Integer port = Integer.parseInt(input.readLine());

        // 2. Start socket server
        final ServerSocket server = new ServerSocket(port);
        System.out.println("Menunggu koneksi di port " + port);

        while (jalan) {
            Socket koneksi = server.accept();
            // 3. Terima koneksi dalam thread baru
            new ChatThread(koneksi).start();
            new ChatSendThread(koneksi, nama).start();
        }

    }

    private static class ChatSendThread extends Thread {

        private Socket koneksi;
        private String nama;

        ChatSendThread(Socket k, String nama) {
            this.koneksi = k;
            this.nama = nama;

        }

        public void run() {
            try {
                System.out.print("Terima koneksi dari alamat IP ");
                System.out.println(((InetSocketAddress) koneksi.getRemoteSocketAddress()).getAddress());

                BufferedReader chat = new BufferedReader(new InputStreamReader(System.in));
                PrintWriter output = new PrintWriter(koneksi.getOutputStream(), true);

                String msg = chat.readLine();
                while (msg != null && !"bye".equalsIgnoreCase(msg)) {
                    output.println(nama + "> " + msg);
                    msg = chat.readLine();
                }
                chat.close();
                koneksi.close();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private static class ChatThread extends Thread {

        private Socket koneksi;

        ChatThread(Socket k) {
            this.koneksi = k;
        }

        public void run() {
            try {
                System.out.print("Terima koneksi dari alamat IP ");
                System.out.println(((InetSocketAddress) koneksi.getRemoteSocketAddress()).getAddress());

                BufferedReader chatIncoming
                        = new BufferedReader(new InputStreamReader(koneksi.getInputStream()));

                String msg = chatIncoming.readLine();
                while (msg != null && !"bye".equalsIgnoreCase(msg)) {
                    System.out.println(msg);
                    msg = chatIncoming.readLine();
                }
                chatIncoming.close();
                koneksi.close();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
