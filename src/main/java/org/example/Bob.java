package org.example;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Bob {
    public static void main(String[] args) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            ServerSocket serverSocket = new ServerSocket(3333);
            System.out.println("Aguardando conexão...");
            Socket socket = serverSocket.accept();
            System.out.println("Conexão estabelecida!");

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(publicKey);

            InputStream inputStream = socket.getInputStream();
            byte[] encryptedData = inputStream.readAllBytes();

            Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipherRSA.doFinal(encryptedData);

            System.out.println("Arquivo recebido: " + new String(decryptedData));

            socket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}