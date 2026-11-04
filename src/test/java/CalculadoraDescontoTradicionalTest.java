import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CalculadoraDescontoTradicionalTest {

    private static final double DELTA = 0.0001;

    private static double calcular(double valor, CustomerCategory categoria) {
        return CalculadoraDescontoTradicional.calculateFinalPrice(valor, categoria);
    }

    @Nested
    @DisplayName("regra 1: acima de R$ 500,00 tem 10% de desconto")
    class DescontoDeDezPorCento {

        @Test
        @DisplayName("pedido de 600,00 fica 540,00")
        void deveAplicarDezPorCentoAcimaDeQuinhentos() {
            assertEquals(540.00, calcular(600.00, CustomerCategory.NO_VIP), DELTA);
        }

        @Test
        @DisplayName("pedido de exatamente 500,00 não tem desconto")
        void naoDeveDarDescontoEmQuinhentosCravado() {
            assertEquals(500.00, calcular(500.00, CustomerCategory.NO_VIP), DELTA);
        }

        @Test
        @DisplayName("pedido de 500,01 já recebe 10%")
        void deveAplicarDezPorCentoUmCentavoAcimaDoLimite() {
            assertEquals(450.009, calcular(500.01, CustomerCategory.NO_VIP), DELTA);
        }

        @Test
        @DisplayName("pedido de exatamente 1000,00 continua nos 10%")
        void deveManterDezPorCentoEmMilCravado() {
            assertEquals(900.00, calcular(1000.00, CustomerCategory.NO_VIP), DELTA);
        }
    }

    @Nested
    @DisplayName("regra 2: acima de R$ 1.000,00 tem 20% de desconto")
    class DescontoDeVintePorCento {

        @Test
        @DisplayName("pedido de 1500,00 fica 1200,00")
        void deveAplicarVintePorCentoAcimaDeMil() {
            assertEquals(1200.00, calcular(1500.00, CustomerCategory.NO_VIP), DELTA);
        }

        @Test
        @DisplayName("pedido de 1000,01 já recebe 20%")
        void deveAplicarVintePorCentoUmCentavoAcimaDoLimite() {
            assertEquals(800.008, calcular(1000.01, CustomerCategory.NO_VIP), DELTA);
        }
    }

    @Nested
    @DisplayName("regra 3: VIP ganha 5% adicional sobre o valor já com desconto")
    class DescontoVip {

        @Test
        @DisplayName("VIP com pedido de 200,00 fica 190,00")
        void deveAplicarSoOsCincoPorCentoQuandoNaoTemFaixa() {
            assertEquals(190.00, calcular(200.00, CustomerCategory.VIP), DELTA);
        }

        @Test
        @DisplayName("VIP com pedido de 600,00 fica 513,00")
        void deveAplicarCincoPorCentoSobreOsDezPorCento() {
            assertEquals(513.00, calcular(600.00, CustomerCategory.VIP), DELTA);
        }

        @Test
        @DisplayName("VIP com pedido de 1500,00 fica 1140,00")
        void deveAplicarCincoPorCentoSobreOsVintePorCento() {
            assertEquals(1140.00, calcular(1500.00, CustomerCategory.VIP), DELTA);
        }
    }

    @Nested
    @DisplayName("regra 4: frete de R$ 15,00 quando o valor final fica abaixo de R$ 100,00")
    class TaxaDeFrete {

        @Test
        @DisplayName("pedido de 50,00 paga frete e fica 65,00")
        void deveCobrarFreteAbaixoDeCem() {
            assertEquals(65.00, calcular(50.00, CustomerCategory.NO_VIP), DELTA);
        }

        @Test
        @DisplayName("pedido de 99,99 paga frete e fica 114,99")
        void deveCobrarFreteUmCentavoAbaixoDoLimite() {
            assertEquals(114.99, calcular(99.99, CustomerCategory.NO_VIP), DELTA);
        }

        @Test
        @DisplayName("pedido de exatamente 100,00 não paga frete")
        void naoDeveCobrarFreteEmCemCravado() {
            assertEquals(100.00, calcular(100.00, CustomerCategory.NO_VIP), DELTA);
        }

        @Test
        @DisplayName("VIP com pedido de 100,00 cai pra 95,00 e paga frete, ficando 110,00")
        void deveCobrarFreteQuandoODescontoDerrubaOValor() {
            assertEquals(110.00, calcular(100.00, CustomerCategory.VIP), DELTA);
        }

        @Test
        @DisplayName("VIP com pedido de 80,00 fica 91,00")
        void deveSomarFreteDepoisDoDescontoVip() {
            assertEquals(91.00, calcular(80.00, CustomerCategory.VIP), DELTA);
        }
    }

    @Nested
    @DisplayName("regra 5: valor zerado ou negativo lança exceção")
    class ValorInvalido {

        @ParameterizedTest(name = "valor {0} lança IllegalArgumentException")
        @ValueSource(doubles = {0.0, -0.01, -100.00})
        void deveLancarExcecaoParaValorInvalido(double valor) {
            assertThrows(IllegalArgumentException.class, () -> calcular(valor, CustomerCategory.NO_VIP));
        }
    }
}
