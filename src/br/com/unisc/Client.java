package br.com.unisc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Client {
    public static void main(String[] args) {
        try {
            String pstr, gstr, Astr;
            String serverName = "localhost";
            int port = 8088;

            // Declare p, g, and Key of client
            BigInteger p = BigInteger.valueOf(23);
            BigInteger g = BigInteger.valueOf(9);
            BigInteger keyA;
            BigInteger Adash, serverB;


            Scanner sc = new Scanner(System.in);
            System.out.println("Informe o número de bits (64, 128 e 256)");
            Integer keySize = sc.nextInt();

            
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize);
            SecretKey chaveSecreta = keyGenerator.generateKey();
            
            byte[] dataInBytes = "teste".getBytes();
            Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
            encryptionCipher.init(Cipher.ENCRYPT_MODE, chaveSecreta);
            byte[] encryptedBytes = encryptionCipher.doFinal(dataInBytes);
          
            System.out.println("Chave Secreta para Criptografar/Descriptogravar = "
                    + chaveSecreta.toString());
            
            System.out.println("mensagem criptografada = "
                    + encryptedBytes.toString());
            
            System.out.println("Conectando a " + serverName
                    + " na porta" + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Conectado a "
                    + client.getRemoteSocketAddress());

            // Enviar dados para o cliente
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF(encryptedBytes.toString());
            out.writeUTF(chaveSecreta.toString());           


            // Aceita mensagem servidor
            DataInputStream in = new DataInputStream(client.getInputStream());

            serverB = BigInteger.valueOf(Long.parseLong(in.readUTF()));
            System.out.println("Servidor : Public Key = " + serverB);


            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}