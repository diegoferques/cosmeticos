package com.cosmeticos.service;

import com.cosmeticos.model.Order;
import com.cosmeticos.model.Point;
import com.cosmeticos.repository.PointRepository;
import com.cosmeticos.service.point.PointNormalizer;
import com.cosmeticos.validation.OrderValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.cosmeticos.commons.ResponseCode.INVALID_POINT_USAGE;
import static java.time.LocalDateTime.now;

/**
 * Classe responsavel por controlar os pontos promocionais de client/profissional.
 * <p>
 * Cada ponto recebido entra como um registro na tabela POINTS com o type UNUSED.
 * <p>
 * Cada uso de pontos entra como um registro na tabela POINTS com o type USED.
 * <p>
 * Registros do tipo USED devem sempre registrar valores negativos e UNUSED valores positivos, conforme definido em {@link Point#value}.
 * <p>
 * O saldo de pontos eh calculado fazendo somatorio de todos os registros de pontos (+ ou -) acumulados pelo usuario. O saldo nunca deve ser negativo.
 */
@Slf4j
@Service
public class PointService {

    @Autowired
    private PointRepository repository;

    @Autowired
    private PointNormalizer pointNormalizer;

    /**
     * Criamos assincronamente pois eventuais falhas neste procedimento nao podem impactar na execucao da transacao, entretanto devem ser logadas.
     *
     * @return
     */
    public Point increase(Order order) {
        Long value = order.getPaymentCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRule()
                .getPrice();

        Long normalizedPoints = pointNormalizer.normalize(value);

        return create(fromOrder(order, normalizedPoints));
    }

    /**
     * O resgate eh sempre total. O que o profissional tiver na conta sera transferido na sua totalidade.
     *
     * @return The full balance withdrawaled
     */
    public Long decrease(Point decreasedPoint) {
        List<Point> balanceItens = findByUser(decreasedPoint.getUserId());

        // Junta os pontos q serao retirados à lista para q seja realizado o somatorio
        balanceItens.add(decreasedPoint);

        Long balance = sum(balanceItens);

        if (balance < 0) {
            /*
            Nao permitimos que o saldo de pontos seja negativo. A app cliente deve conhecer o saldo do usuario e pedir
            que estejam dentro do saldo de pontos do usuario.
             */
            throw new OrderValidationException(INVALID_POINT_USAGE, "Nao eh possivel usar " + decreasedPoint.getValue() + " pontos, pois o saldo atual eh: " + balance);
        } else {
            create(decreasedPoint);

            return balance;
        }
    }

    private Point fromOrder(Order order, Long value) {

        Point item = new Point();
        item.setDescription(String.format("%s %s", order.getClass().getSimpleName(), String.valueOf(order.getStatus())));
        item.setOrderId(order.getIdOrder());
        item.setDate(Timestamp.valueOf(now()));
        item.setUserId(order
                .getProfessionalCategory()
                .getProfessional()
                .getUser()
                .getIdLogin()
        );
        item.setValue(value);

        return item;
    }

    public List<Point> findByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    private Long sum(List<Point> balanceItems) {

        Long total = balanceItems.stream().mapToLong(i -> i.getValue()).sum();

        return total;
    }

    private Point create(Point point) {
        return repository.save(point);
    }

   /* public List<Point> listBy(Point point) {
        return repository.findAll(Example.of(point));
    }

    public List<Point> listAll() {
        return repository.findAll();
    }*/

}
