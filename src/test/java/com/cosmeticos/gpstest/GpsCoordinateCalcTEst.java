package com.cosmeticos.gpstest;

import com.cosmeticos.commons.google.GoogleMapsResponseBody;
import com.cosmeticos.commons.google.LocationGoogle;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Lulu on 04/07/2017.
 */

@Ignore // Ignorado por ser teste de integracao com o geocode.
@RunWith(SpringRunner.class)
@SpringBootTest
public class GpsCoordinateCalcTEst {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Test
    public void calculaDistanciaEmMetrosDaMinhaCasaAtualAteMinhaAntigaCasa()
    {
        System.out.println(getDistancia(
                /*
                 Ponto de origem: casa atual - Rua da Abolicao, 5, Austin, Nova Iguacu, RJ
                https://maps.googleapis.com/maps/api/geocode/json?address=Rua%20da%20Abolicao,%205,%20Austin,%20Nova%20Iguacu,%20RJ&key=AIzaSyDdNCXGRO5OxTcBkpzgURHceKgbZyj1g9w
                  */
                -22.7332428,  -43.5208845,

                /*
                 Ponto de destino: casa antiga - Rua Perlita, 42, Austin, Nova Iguacu, RJ
                https://maps.googleapis.com/maps/api/geocode/json?address=Rua%20Perlita,%2042,%20Austin,%20Nova%20Iguacu,%20RJ&key=AIzaSyDdNCXGRO5OxTcBkpzgURHceKgbZyj1g9w
                  */
                -22.951115,  -43.181162
        ));
    }

    @Test
    public void aceesaGoogleMapsApiPraBuscarDoisEnderecoseCalcularDistancia()
    {
        String urlSource = "https://maps.googleapis.com/maps/api/geocode/json?address=Rua+da+Abolicao,+5,+Austin,+Nova+Iguacu,+RJ&key=AIzaSyDdNCXGRO5OxTcBkpzgURHceKgbZyj1g9w";
        String urlDestiny = "https://maps.googleapis.com/maps/api/geocode/json?address=Rua%20Perlita,%2042,%20Austin,%20Nova%20Iguacu,%20RJ&key=AIzaSyDdNCXGRO5OxTcBkpzgURHceKgbZyj1g9w";

        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<GoogleMapsResponseBody> sourceResponse =
                restTemplate.getForEntity(urlSource, GoogleMapsResponseBody.class);

        ResponseEntity<GoogleMapsResponseBody> destinyResponse =
                restTemplate.getForEntity(urlDestiny, GoogleMapsResponseBody.class);

        LocationGoogle sourceLocation = sourceResponse.getBody().getResults()
                .stream().findFirst().get()
                .getGeometry().getLocation();

        LocationGoogle destinyLocation = destinyResponse.getBody().getResults()
                .stream().findFirst().get()
                .getGeometry().getLocation();

        // Como sei que sao menos de 3km entre os pontos, faco:
        double distance = getDistancia(
                // Origem
                sourceLocation.getLat(), sourceLocation.getLng(),

                // Destino
                destinyLocation.getLat(), destinyLocation.getLng()
        );
        System.out.println("A distancia entre Origem e Destino eh: " + distance);
        Assert.assertThat(distance, org.hamcrest.Matchers.lessThan(3000.0));

    }

    private double getDistancia(double latitude, double longitude, double latitudePto, double longitudePto){

        latitude = Math.toRadians(latitude);
        longitude = Math.toRadians(longitude);
        latitudePto = Math.toRadians(latitudePto);
        longitudePto = Math.toRadians(longitudePto);

        double dlon, dlat, a, distancia;
        dlon = longitudePto - longitude;
        dlat = latitudePto - latitude;
        a = Math.pow(Math.sin(dlat/2),2) + Math.cos(latitude) * Math.cos(latitudePto) * Math.pow(Math.sin(dlon/2),2);
        distancia = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6378140 * distancia; /* 6378140 is the radius of the Earth in meters*/
    }

}
