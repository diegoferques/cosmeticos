package com.cosmeticos.service.point;

public interface PointNormalizer {

    /**
     * Responsavel por receber um valor Long e devolver outro long normalizado.
     *
     * @param source
     * @return Dependendo da implementacao desta interface, o tipo de retorno pode variar. A estrategia de pontuacao vai determinar
     * o tipo do retorno. Inicialmente, a classe {@link DefaultPointNormalizer} eh quem dita a regra de pontuacao
     */
    Long normalize(Long source);

}
