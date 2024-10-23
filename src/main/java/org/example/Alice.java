package org.example;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.net.Socket;
import java.security.*;
import javax.crypto.Cipher;
import java.math.BigInteger;
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
                System.out.println("Arquivo lido com sucesso");

                // Generate key pair for Alice
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(2048);
                KeyPair keyPair = keyGen.generateKeyPair();
                PrivateKey privateKey = keyPair.getPrivate();
                PublicKey publicKey = keyPair.getPublic();
                System.out.println("Par de chaves RSA gerado");

                // Sign the data
                Signature assinatura = Signature.getInstance("SHA256withRSA");
                assinatura.initSign(privateKey);
                assinatura.update(data);
                byte[] digitalSignature = assinatura.sign();
                System.out.println("Assinatura digital gerada");

                Socket socket = new Socket("localhost", 3333);
                System.out.println("Conexão estabelecida!");

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                BigInteger[] qa = (BigInteger[]) objectInputStream.readObject();
                BigInteger q = qa[0];
                BigInteger a = qa[1];
                BigInteger bobPublicKey = (BigInteger) objectInputStream.readObject();
                System.out.println("Chaves públicas recebidas de Bob");

                SecureRandom random = new SecureRandom();
                BigInteger alicePrivateKey = new BigInteger(q.bitLength(), random);
                BigInteger alicePublicKey = Util.power(a, alicePrivateKey, q);
                System.out.println("Chave pública de Alice gerada");

                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(alicePublicKey);
                objectOutputStream.writeObject(publicKey); // Send Alice's public key
                objectOutputStream.writeObject(digitalSignature); // Send the digital assinatura
                System.out.println("Chave pública e assinatura digital enviadas para Bob");

                BigInteger sharedSecret = Util.power(bobPublicKey, alicePrivateKey, q);
                byte[] sharedSecretBytes = sharedSecret.toByteArray();
                SecretKeySpec aesKey = new SecretKeySpec(sharedSecretBytes, 0, 16, "AES");
                System.out.println("Chave secreta compartilhada gerada");

                Cipher cipherAES = Cipher.getInstance("AES");
                cipherAES.init(Cipher.ENCRYPT_MODE, aesKey);
                byte[] encryptedData = cipherAES.doFinal(data);
                System.out.println("Dados criptografados com AES");

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