package com.cosmeticos.service.point;

import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_UP;
import static org.springframework.util.Assert.isTrue;

/**
 * Responsavel por receber o preco em centavos (1389 = R$ 13,89) e transformar em pontos. Os centavos sao sempre arredondados
 * para mais.
 */
@Service
class DefaultPointNormalizer implements PointNormalizer {
    @Override
    public Long normalize(@NotNull Long source) {

        isTrue(source != null, "source cannot be null");

        BigDecimal priceWithCents = new BigDecimal(source);

        // Dividimos por 100 para transformar centavos em real, arredondando centavos quebrados para mais.
        BigDecimal point = priceWithCents.divide(BigDecimal.valueOf(100), ROUND_UP);

        // Devolvendo os centavos recebidos como pontos.
        return point.longValue();
    }
}
