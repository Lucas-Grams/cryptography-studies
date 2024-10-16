package org.example;

import javax.crypto.Cipher;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

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
                PublicKey publicKey = (PublicKey) objectInputStream.readObject();

                System.out.println("Arquivo selecionado: " + fileChooser.getSelectedFile().getName());

                Cipher cipherAES = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipherAES.init(Cipher.ENCRYPT_MODE, publicKey);
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