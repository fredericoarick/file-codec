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


## Codificações

Breve descrição sobre os diferentes algoritmos de codificação suportados.

### Unária

Esta codificação traduz o valor inteiro de cada simbolo para uma sequência de `0`, adicionando um stop bit `1` para indicar o fim do codeword.

Exemplo:

| Símbolo | Codeword |
| ------- | -------- | 
|       4 |    00001 |
|      13 | 00000000000001 |

### Golomb

Este algoritmo utiliza como base um valor de divisor, tipicamente de base 2. O codeword gerado é dividido em três partes: um prefixo, um stop-bit e um sufixo. O prefixo, de tamanho variável, é sempre um valor unário (sequência de zeros) equivalente ao quociente do simbolo pelo divisor. Já o prefixo, de tamanho fixo, é o valor binário do resto da divisão. Tomando como exemplo um divisor `k=4`, podemos ter:


| Símbolo | Quociente | Resto | Codeword |
| -----   | ---------- | ----- | -------- |
| 49 | 12 | 1 | 000000000000 1 01 |
| 34 | 8 | 2 | 00000000 1 10 | 

### Elias-Gama

Este algoritmo é semelhante ao Golomb, com um prefixo, stop-bit e sufixo. Neste caso, o prefixo e sufixo tem tamanho `N` equivalente a maior potência de 2 que é menor que o valor do simbolo. O prefixo é um valor unário representando este valor `N`, e o sufixo tem valor equivalente ao resto `R` para formar o simbolo a partir da forma `2^N + R`

| Símbolo | Expansão | Codeword |
| --------|----------|----------|
| 49 | 2^5 + 17 | 00000 1 10001 |
| 68 | 2^6 + 4 | 000000 1 000100 |

Importante notar que essa codificação não permite codificar o símbolo `0`, por isso os símbolos são codificados com valores incrementados de 1.

### Fibonacci

Esta codificação utiliza do teorema que qualquer número inteiro pode ser formado a partir de uma soma de termos da sequência de Fibonacci. Ignorando os dois primeiros termos, consideremos que uma sequência de bits equivala a termos da sequência de fibonacci, sendo o primeiro bit o primeiro termo, o segundo bit o segundo termo, e assim por diante. O codeword é definido então de forma que cada bit `1` signifique que aquele termo da sequência seja incluído no somatório. Como cada termo da sequência é a soma dos dois anteriores, nunca haverão dois bits `1` seguidos, então essa condição é utilizada como stop-bit.

* Sequência de fibonacci: 1, 2, 3, 5, 8, 13, 21, 34...

| Símbolo | Expansão | Codeword |
| --------|----------|----------|
| 49 | 2 + 13 + 34  | 010001011 |
| 27 | 1 + 5 + 21 | 10010011 |

Assim como a Elias-Gama, não é possível codificar o símbolo `0`, então todos os símbolos são codificados com valores incrementados de 1.

### Delta


Este algoritmo consiste em codificar a variação inteira entre cada símbolo. O primeiro símbolo é apresentado como o próprio valor binário:

| Símbolo | Codeword |
| ------- | -------- | 
|      55 |    00110111 |

Os próximos símbolos são apresentados como a variação, ou delta, do anterior. Se o símbolo é igual é utilizado apenas um bit `1`. Se não, é apresentado um bit `0` seguido do valor binário do delta. Na codificação implementada, não é feito nenhum tipo de pré-processamento para saber o maior delta possível, então é utilizado sempre um delta de 8 bits, mais um bit de sinal. 

| Símbolo | Delta | Codeword |
| ------- | ------ | -------- | 
|      59 |       4 | 0000000100 |
| 81      | 22 | 0000010110 |
| 81 | 0 | 1 |
| 68 | -13 | 0100001101 |






