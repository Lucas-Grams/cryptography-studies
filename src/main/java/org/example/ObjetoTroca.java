package org.example;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjetoTroca implements Serializable {

    private String nomeArquivo;
    private byte[] arquivoCifrado;
    private SecretKey keyEAS;

}

