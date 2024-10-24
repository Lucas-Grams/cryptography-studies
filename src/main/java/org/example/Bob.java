package org.example;

import java.security.*;
import java.net.Socket;
import javax.crypto.Cipher;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class Bob {
    public static void main(String[] args) {
        try {
            //Gerando números q e a
            BigInteger[] qa = Util.geraQA(512);
            BigInteger q = qa[0];
            BigInteger a = qa[1];
            System.out.println("Números (q) e (a) gerados");

            //Gerando chave pública de Bob
            SecureRandom random = new SecureRandom();
            BigInteger bobPrivateKey = new BigInteger(q.bitLength(), random);
            BigInteger bobPublicKey = Util.power(a, bobPrivateKey, q);
            System.out.println("Chave pública de Bob gerada");

            //Aguardando conexão da Alice
            ServerSocket serverSocket = new ServerSocket(3333);
            System.out.println("Aguardando conexão...");
            Socket socket = serverSocket.accept();
            System.out.println("Conexão estabelecida!");

            //nviando dados para Alice
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(qa);
            objectOutputStream.writeObject(bobPublicKey);
            System.out.println("Chaves públicas enviadas para Alice");

            //Recebendo dados de Alice
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            BigInteger alicePublicKey = (BigInteger) objectInputStream.readObject();
            PublicKey publicKey = (PublicKey) objectInputStream.readObject(); // Receive Alice's public key
            byte[] digitalSignature = (byte[]) objectInputStream.readObject(); // Receive the digital assinatura
            System.out.println("Chave pública e assinatura digital recebidas de Alice");

            //Gerando chave secreta compartilhada
            BigInteger sharedSecret = Util.power(alicePublicKey, bobPrivateKey, q);
            byte[] sharedSecretBytes = sharedSecret.toByteArray();
            SecretKeySpec aesKey = new SecretKeySpec(sharedSecretBytes, 0, 16, "AES");
            System.out.println("Chave secreta compartilhada gerada");

            //Recebendo e descriptografando os dados
            InputStream inputStream = socket.getInputStream();
            byte[] encryptedData = inputStream.readAllBytes();
            System.out.println("Dados criptografados recebidos");

            Cipher cipherAES = Cipher.getInstance("AES");
            cipherAES.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedData = cipherAES.doFinal(encryptedData);
            System.out.println("Dados descriptografados com AES");

            //Verificação da assinatura
            Signature assinatura = Signature.getInstance("SHA256withRSA");
            assinatura.initVerify(publicKey);
            assinatura.update(decryptedData);
            boolean isVerified = assinatura.verify(digitalSignature);

            if (isVerified) {
                System.out.println("Arquivo recebido e verificado: " + new String(decryptedData));
                System.out.println("A assinatura foi gerada por Alice.");
            } else {
                System.out.println("Falha na verificação da assinatura!");
                System.out.println("A assinatura não foi gerada por Alice.");
            }

            socket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}