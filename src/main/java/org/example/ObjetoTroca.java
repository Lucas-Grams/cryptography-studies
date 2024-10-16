package org.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.crypto.SecretKey;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjetoTroca implements Serializable {

    private String nomeArquivo;
    private byte[] arquivoCifrado;
    private SecretKey keyEAS;

}
