# Etapa 1: Code First, Test Later (anotações)

## O que foi feito

1. A classe `CalculadoraDescontoTradicional` foi implementada primeiro, sem nenhum teste (commit `step: 0`).
2. Só depois disso foi escrita a suíte `CalculadoraDescontoTradicionalTest` (JUnit 5), com os valores esperados calculados à mão a partir do enunciado, sem olhar o que o código devolve.
3. Para conseguir rodar JUnit, o projeto virou Maven: `pom.xml` na raiz e os fontes movidos para `src/main/java` e `src/test/java`. O código da calculadora não foi alterado.

Para rodar: `mvn test`

## Tempo gasto

- Implementação do código: ______
- Escrita dos testes: ______
- Análise das falhas: ______

## Resultado da execução

17 testes, **12 falharam**, 5 passaram.

| Regra | Entrada | Esperado (enunciado) | Obtido (código) | Resultado |
|---|---|---|---|---|
| 1 | 600,00 | 540,00 | 480,00 | falhou |
| 1 | 500,00 | 500,00 | 450,00 | falhou |
| 1 | 500,01 | 450,009 | 400,008 | falhou |
| 1 | 1000,00 | 900,00 | 800,00 | falhou |
| 2 | 1500,00 | 1200,00 | 1500,00 | falhou |
| 2 | 1000,01 | 800,008 | 1000,01 | falhou |
| 3 | 200,00 VIP | 190,00 | 170,00 | falhou |
| 3 | 600,00 VIP | 513,00 | 450,00 | falhou |
| 3 | 1500,00 VIP | 1140,00 | 1425,00 | falhou |
| 4 | 50,00 | 65,00 | 65,00 | passou |
| 4 | 99,99 | 114,99 | 114,99 | passou |
| 4 | 100,00 | 100,00 | 90,00 | falhou |
| 4 | 100,00 VIP | 110,00 | 85,00 | falhou |
| 4 | 80,00 VIP | 91,00 | 91,00 | passou |
| 5 | 0,00 | exceção | 15,00 (sem exceção) | falhou |
| 5 | -0,01 | exceção | exceção | passou |
| 5 | -100,00 | exceção | exceção | passou |

## Defeitos que os testes encontraram no código

1. **Faixas de desconto deslocadas.** O código dá 10% para valores entre 100 e 500 e 20% entre 500 e 1000. O enunciado pede 10% acima de 500 e 20% acima de 1000.
2. **Acima de 1000 não tem desconto nenhum.** Como nenhuma das condições cobre `price > 1000`, o percentual fica em 0.
3. **Desconto VIP somado em vez de aplicado em cascata.** O código faz `discountPercent += 5`, ou seja, soma pontos percentuais. O enunciado diz que os 5% incidem "sobre o valor já com desconto" (`valor * 0,90 * 0,95`).
4. **Frete verificado sobre o valor errado.** A condição é `price < 100`, usando o valor original, mas o enunciado manda olhar o valor final, depois dos descontos.
5. **Pedido zerado não lança exceção.** A validação é `price < 0`, então o zero passa e o cliente recebe uma cobrança de 15,00 de frete.

## Dificuldades ao escrever testes para um código já pronto

- **Os limites só ficaram claros na hora de escolher os valores de teste.** O enunciado fala "acima de" e "inferior a", mas não diz nada sobre 500,00, 1000,00 ou 100,00 cravados. Como o código veio primeiro, ele já tinha fixado essas decisões por conta própria (usa `>= 100` e `<= 500`, regras que nem existem no enunciado).
- **Tentação de copiar o resultado do código para o teste.** Com o código pronto, o caminho mais fácil é rodar e colar o valor que saiu no `assertEquals`. Isso transformaria o teste numa confirmação do próprio código e esconderia os defeitos. Foi preciso recalcular tudo à mão a partir do enunciado.
- **Quando o teste falha, é preciso decidir quem está errado.** Cada falha exigiu reler o enunciado para confirmar se o erro estava no código ou na minha interpretação da regra (por exemplo, se os 20% substituem os 10% ou somam com eles).
- **Alguns defeitos só aparecem com entradas muito específicas.** A diferença entre desconto somado e em cascata só fica visível quando os dois resultados divergem (600,00 VIP dá 510,00 somando e 513,00 em cascata). O erro do frete só aparece num pedido de pelo menos 100,00 que o desconto derruba para baixo de 100,00. Escolher essas entradas exigiu entender o código por dentro.
- **Um defeito mascara o outro.** Como as faixas estão deslocadas, quase todo teste da regra 3 e da regra 4 falha também por causa da regra 1. Fica difícil saber, só pela falha, quantos defeitos existem de verdade.
- **O `main` não é testável.** Ele lê do `Scanner(System.in)` e imprime direto no console, então só o método `calculateFinalPrice` pôde ser testado. Testar a entrada e a saída exigiria redirecionar `System.in` e `System.out` ou reestruturar o código.
- **Dinheiro em `double`.** Os testes precisam de uma margem (`DELTA`) para comparar valores, e o enunciado não diz como arredondar centavos (500,01 com 10% dá 450,009).
- **Comportamentos que o enunciado não cobre.** Não está definido o que fazer com categoria `null`, por exemplo. O código atual trata `null` como cliente comum, mas não há requisito para testar isso.
- **O projeto não estava preparado para testes.** Os arquivos estavam soltos na raiz, sem ferramenta de build, e foi preciso reorganizar a estrutura antes de escrever o primeiro teste.
