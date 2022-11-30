package br.com.unisc;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            String pstr, gstr, Astr;
            String serverName = "localhost";
            int port = 8088;

            // Declare p, g, and Key of client
            BigInteger p = BigInteger.valueOf(234423442);
            BigInteger g = BigInteger.valueOf(944423441);
            BigInteger keyA;
            BigInteger Adash, serverB;


            Scanner sc = new Scanner(System.in);
            System.out.println("Informe o número de bits (64, 128 e 256)");
            Integer keySize = sc.nextInt();

            keyA = new BigInteger(keySize, new Random());

            // Established the connection
            System.out.println("Conectando a " + serverName
                    + " on port na porta" + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Conectado a "
                    + client.getRemoteSocketAddress());

            // Sends the data to client
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            pstr = p.toString();
            out.writeUTF(pstr); // Sending p

            gstr = g.toString();
            out.writeUTF(gstr); // Sending g

            BigInteger A = g.modPow(keyA, p);
            Astr = A.toString();
            out.writeUTF(Astr); // Sending A

            // Client's Private Key
            System.out.println("Cliente : Private Key = " + keyA);

            // Accepts the data
            DataInputStream in = new DataInputStream(client.getInputStream());

            serverB = BigInteger.valueOf(Long.parseLong(in.readUTF()));
            System.out.println("Servidor : Public Key = " + serverB);

            Adash = serverB.modPow(keyA, p);


            System.out.println("Chave Secreta para Criptografar/Descriptogravar = "
                    + Adash);
            byte[] decodedKey = Adash.toByteArray();


            System.out.println("Informe a mensagem a ser enviada criptografada: ");
            String message = sc.next();

            Cipher cipher = Cipher.getInstance("AES");
            SecretKey originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            System.out.println("Chave AES Gerada: " + Base64.getEncoder().encodeToString(originalKey.getEncoded()));
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
            System.out.println("Mensagem original: " + message);
            String encrypted = Base64.getEncoder().encodeToString(cipherText);
            System.out.println("Mensagem criptografa: " + encrypted);
            out.writeUTF(encrypted);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
