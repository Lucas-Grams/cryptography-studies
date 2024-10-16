package org.example;

import java.net.Socket;
import java.io.InputStream;
import javax.crypto.Cipher;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.security.SecureRandom;
import java.io.ObjectOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class Bob {
    public static void main(String[] args) {
        try {
            BigInteger[] qa = Util.geraQA(512);
            BigInteger q = qa[0];
            BigInteger a = qa[1];

            SecureRandom random = new SecureRandom();
            BigInteger bobPrivateKey = new BigInteger(q.bitLength(), random);
            BigInteger bobPublicKey = Util.power(a, bobPrivateKey, q);

            ServerSocket serverSocket = new ServerSocket(3333);
            System.out.println("Aguardando conexão...");
            Socket socket = serverSocket.accept();
            System.out.println("Conexão estabelecida!");

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(qa);
            objectOutputStream.writeObject(bobPublicKey);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            BigInteger alicePublicKey = (BigInteger) objectInputStream.readObject();

            BigInteger sharedSecret = Util.power(alicePublicKey, bobPrivateKey, q);
            byte[] sharedSecretBytes = sharedSecret.toByteArray();
            SecretKeySpec aesKey = new SecretKeySpec(sharedSecretBytes, 0, 16, "AES");

            InputStream inputStream = socket.getInputStream();
            byte[] encryptedData = inputStream.readAllBytes();

            Cipher cipherAES = Cipher.getInstance("AES");
            cipherAES.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedData = cipherAES.doFinal(encryptedData);

            System.out.println("Arquivo recebido: " + new String(decryptedData));

            socket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}