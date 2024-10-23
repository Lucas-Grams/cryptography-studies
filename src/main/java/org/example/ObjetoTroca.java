package org.example;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import javax.crypto.SecretKey;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjetoTroca implements Serializable {

    private String nomeArquivo;
    private byte[] arquivoCifrado;
    private SecretKey keyEAS;

}

