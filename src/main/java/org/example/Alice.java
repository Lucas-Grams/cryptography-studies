package org.example;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.net.Socket;
import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.SecureRandom;
import javax.crypto.spec.SecretKeySpec;

public class Alice {
    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        System.out.println("Selecionando arquivo");

        try {
            if (fileChooser.showDialog(new Frame(), "Selecionar") == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] data = new byte[(int) fileInputStream.getChannel().size()];
                fileInputStream.read(data);

                Socket socket = new Socket("localhost", 3333);
                System.out.println("Conexão estabelecida!");

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                BigInteger[] qa = (BigInteger[]) objectInputStream.readObject();
                BigInteger q = qa[0];
                BigInteger a = qa[1];
                BigInteger bobPublicKey = (BigInteger) objectInputStream.readObject();

                SecureRandom random = new SecureRandom();
                BigInteger alicePrivateKey = new BigInteger(q.bitLength(), random);
                BigInteger alicePublicKey = Util.power(a, alicePrivateKey, q);

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(alicePublicKey);

                BigInteger sharedSecret = Util.power(bobPublicKey, alicePrivateKey, q);
                byte[] sharedSecretBytes = sharedSecret.toByteArray();
                SecretKeySpec aesKey = new SecretKeySpec(sharedSecretBytes, 0, 16, "AES");

                Cipher cipherAES = Cipher.getInstance("AES");
                cipherAES.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] encryptedData = cipherAES.doFinal(data);

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(encryptedData);
                outputStream.flush();

                System.out.println("Arquivo enviado!");

                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}