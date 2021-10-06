# file-codec

Este projeto consiste de um conjunto de algoritmos bit-wise para codificar e decodificar arquivos, bem como um segundo codec para adicionar tratamento de ruídos (ECC) a um arquivo previamente codificado. 
São suportadas as seguintes codificações: Golomb, Elias-Gama, Fibonacci, Unária e Delta.

## Requisitos

* Java 8+

## Guia de utilização

Baixe o arquivo `.jar` disponibilizado em uma pasta de sua preferência e acesse via terminal.

### Codificar um arquivo:

Execute o seguinte comando, passando como parâmetros os arquivos de entrada e saída e um dos algoritmos suportados (`golomb`, `eliasgama`, `fibonacci`, `unary`, `delta`):

```
java -jar file-codec.jar encode arquivo-entrada.txt arquivo-saida.cod eliasgama
```

No caso da codificação Golomb, também é necessário passar o tamanho do divisor a ser utilizado no algoritmo:

```
java -jar file-codec.jar encode arquivo-entrada.txt arquivo-saida.cod golomb 8
```

O arquivo de saída, além dos dados codificados, contém um cabeçalho de dois bytes, sendo o primeiro referente ao tipo de codificação e o segundo ao divisor utilizado, no caso de codificação Golomb.

### Decodificar um arquivo:

Execute o seguinte comando, passando os arquivos de entrada e saída:

```
java -jar file-codec.jar decode arquivo-entrada.cod arquivo-saida.txt
```

### Codificar um arquivo com tratamento de ruídos (ECC):

Para adicionar codificação de tratamento de ruídos em um arquivo já codificado, execute:

```
java -jar file-codec.jar encodeecc arquivo-entrada.cod arquivo-saida.ecc
```

Esta codificação adiciona dois tipos de tratamento de erros:

* Adiciona um terceiro byte ao cabeçalho do arquivo com o resultado CRC-8 dos dois primeiros bytes.
* Codifica cada 4 bits do arquivo gerando um codeword Hamming de 7 bits contendo os bits de paridade.

### Decodificar um arquivo com tratamento de ruídos (ECC):

```
java -jar file-codec.jar decodeecc arquivo-entrada.ecc arquivo-saida.cod
```

* Se ao decodificar é encontrado erro no valor de CRC-8, a decodificação é interrompida.
* Se é encotrada alguma incosistência nos bits de paridade, é aplicada coreção.
