package com.cosmeticos.service;

import com.cosmeticos.model.Order;
import com.cosmeticos.model.Point;
import com.cosmeticos.model.User;
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
     * Incrementamos os pontos do profissional e do cliente associados a Order informada.
     * Criamos assincronamente pois eventuais falhas neste procedimento nao podem impactar na execucao da transacao, entretanto devem ser logadas.
     *
     * @return
     */
    public void increase(Order order) {
        Long value = order.getPaymentCollection()
                .stream()
                .findFirst()
                .get()
                .getPriceRule()
                .getPrice();

        Long normalizedPoints = pointNormalizer.normalize(value);

        String description = String.format("Adding %d points, orderStatus: %s", normalizedPoints, order.getStatus());
        User professsionalUser = order.getProfessionalCategory().getProfessional().getUser();
        User customerUser = order.getIdCustomer().getUser();


        Point professionalPoint = new Point();
        professionalPoint.setUserId(professsionalUser.getIdLogin());
        professionalPoint.setValue(normalizedPoints);
        professionalPoint.setDate(Timestamp.valueOf(now()));
        professionalPoint.setOrderId(order.getIdOrder());
        professionalPoint.setDescription(description);
        create(professionalPoint);


        Point customerPoint = new Point();
        customerPoint.setUserId(customerUser.getIdLogin());
        customerPoint.setValue(normalizedPoints);
        customerPoint.setDate(Timestamp.valueOf(now()));
        customerPoint.setOrderId(order.getIdOrder());
        customerPoint.setDescription(description);
        create(customerPoint);
    }

    /**
     * @return The remaining balance after decreasing.
     */
    public Long decrease(User user, Long value) {

        if(value <= 0 )
            throw new IllegalArgumentException("value must be positive greater than zero.");

        // Negativando para fazer o decremento
        Long decreasingValue = value * -1;

        List<Point> balanceItens = findByUser(user.getIdLogin());

        Point negativePoints = new Point();
        negativePoints.setUserId(user.getIdLogin());
        negativePoints.setValue(decreasingValue);
        negativePoints.setDate(Timestamp.valueOf(now()));
        negativePoints.setOrderId(null);
        negativePoints.setDescription(String.format("Decreasing %d points.", decreasingValue));

        // Junta os pontos q serao retirados à lista para q seja realizado o somatorio
        balanceItens.add(negativePoints);

        Long balance = sum(balanceItens);

        if (balance < 0) {
            /*
            Nao permitimos que o saldo de pontos seja negativo. A app cliente deve conhecer o saldo do usuario e pedir
            que estejam dentro do saldo de pontos do usuario.
             */
            throw new OrderValidationException(INVALID_POINT_USAGE, "Nao eh possivel usar " + negativePoints.getValue() + " pontos, pois o saldo atual eh: " + balance);
        } else {
            create(negativePoints);

            return balance;
        }
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
}
