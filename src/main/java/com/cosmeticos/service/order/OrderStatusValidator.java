package com.cosmeticos.service.order;

import com.cosmeticos.model.Order;
import com.cosmeticos.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.cosmeticos.model.OrderStatus.*;

@Service
public class OrderStatusValidator {

	/**
	 * "(OrderStatus atual, OrderStatus permitidos[])"
	 */
	private Map<OrderStatus, OrderStatus[]> statusChangeMatrix;
	
	public OrderStatusValidator() {
		statusChangeMatrix = new HashMap<>();
		statusChangeMatrix.put(null, new OrderStatus[] { OPEN });
		statusChangeMatrix.put(OPEN, new OrderStatus[] { ACCEPTED, SCHEDULED, CANCELLED, EXPIRED });
		statusChangeMatrix.put(ACCEPTED, new OrderStatus[] { INPROGRESS, CANCELLED  });
		statusChangeMatrix.put(SCHEDULED, new OrderStatus[] { INPROGRESS, CANCELLED });
		statusChangeMatrix.put(INPROGRESS, new OrderStatus[] { READY2CHARGE, SEMI_CLOSED, CANCELLED });
		statusChangeMatrix.put(READY2CHARGE, new OrderStatus[] { SEMI_CLOSED });
		statusChangeMatrix.put(SEMI_CLOSED, new OrderStatus[] { CLOSED, AUTO_CLOSED });

	}
	
	/**
	 * Antes de atribuir o status, verificamos se a transicao do status antigo pro
	 * novo eh permitida.
	 * @return
	 */
	public OrderStatus validate(final Order order, OrderStatus candidateState) {

		OrderStatus currentState = order.getStatus();
		
		OrderStatus[] allowedStatus = this.statusChangeMatrix.get(currentState);

		boolean valid = false;
		if (allowedStatus != null) {
			for (OrderStatus s : allowedStatus) {
				if (s.equals(candidateState)) {
					valid = true;
				}
			}
		}
		
		if(valid)
		{
			order.setLastStatusUpdate(Timestamp.valueOf(LocalDateTime.now()));
			return candidateState;
		} else {
			String msg = String.format("Nao eh permitido mudar do status %s para o status %s.", 
					currentState, candidateState);
			throw new IllegalStateException(msg);
		}
	}
}
