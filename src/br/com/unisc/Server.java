package br.com.unisc;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) throws IOException {
        try {
            int port = 8088;

            // Server Key
            BigInteger keyB;

            // Client p, g, and key
            BigInteger clientP, clientG, clientA, B, Bdash;
            String Bstr;

            Scanner sc = new Scanner(System.in);
            System.out.println("Informe o número de bits (64, 128 e 256)");
            Integer keySize = sc.nextInt();

            keyB = new BigInteger(keySize, new Random());

            // Established the Connection
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Aguardando cliente conectar na porta " + serverSocket.getLocalPort() + "...");
            Socket server = serverSocket.accept();
            System.out.println("Cliente conectado em " + server.getRemoteSocketAddress());

            // Server's Private Key
            System.out.println("Servidor : Private Key = " + keyB);

            // Accepts the data from client
            DataInputStream in = new DataInputStream(server.getInputStream());

            clientP = BigInteger.valueOf(Long.parseLong(in.readUTF())); // to accept p
            System.out.println("Cliente : P = " + clientP);

            clientG = BigInteger.valueOf(Long.parseLong(in.readUTF())); // to accept g
            System.out.println("Cliente : G = " + clientG);

            clientA = BigInteger.valueOf(Long.parseLong(in.readUTF())); // to accept A
            System.out.println("Cliente : Public Key = " + clientA);

            B = clientG.modPow(keyB, clientP);

            Bstr = B.toString();

            // Sends data to client
            // Value of B
            OutputStream outToclient = server.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToclient);

            out.writeUTF(Bstr); // Sending B

            Bdash = clientA.modPow(keyB, clientP);

            System.out.println("Chave Secreta para Criptografar/Descriptogravar = "
                    + Bdash);

            String encryptedString = in.readUTF();

            byte[] decodedKey = Bdash.toByteArray();
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            System.out.println("Chave AES Gerada: " + Base64.getEncoder().encodeToString(originalKey.getEncoded()));
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
            String decrpted = new String(cipherText);
            System.out.println("Mensagem recebida: " + encryptedString);
            System.out.println("Mensagem descriptografada: " + decrpted);
            server.close();
        } catch (Exception s) {
            System.out.println("Socket timed out!");
        }
    }
}
