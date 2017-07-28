package com.cosmeticos.service;

import static com.cosmeticos.model.Order.Status.ACCEPTED;
import static com.cosmeticos.model.Order.Status.CANCELLED;
import static com.cosmeticos.model.Order.Status.CLOSED;
import static com.cosmeticos.model.Order.Status.INPROGRESS;
import static com.cosmeticos.model.Order.Status.OPEN;
import static com.cosmeticos.model.Order.Status.SCHEDULED;
import static com.cosmeticos.model.Order.Status.SEMI_CLOSED;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cosmeticos.model.Order;
import com.cosmeticos.model.Order.Status;

@Service
public class OrderStatusHandler {

	/**
	 * "(Status atual, Status permitidos[])"
	 */
	private Map<Order.Status, Order.Status[]> statusChangeMatrix;
	
	public OrderStatusHandler() {
		statusChangeMatrix = new HashMap<>();
		statusChangeMatrix.put(null, new Status[] { OPEN });
		statusChangeMatrix.put(OPEN, new Status[] { ACCEPTED, SCHEDULED, CANCELLED });
		statusChangeMatrix.put(ACCEPTED, new Status[] { INPROGRESS, CANCELLED });
		statusChangeMatrix.put(SCHEDULED, new Status[] { INPROGRESS, CANCELLED });
		statusChangeMatrix.put(INPROGRESS, new Status[] { SEMI_CLOSED, CANCELLED });
		statusChangeMatrix.put(SEMI_CLOSED, new Status[] { CLOSED });
	}
	
	/**
	 * Antes de atribuir o status, verificamos se a transicao do status antigo pro
	 * novo eh permitida.
	 * @param status
	 * @return
	 */
	public Order.Status handle(final Order order, Status candidateState) {

		Order.Status currentState = order.getStatus();
		
		Status[] allowedStatus = this.statusChangeMatrix.get(currentState);

		boolean valid = false;
		if (allowedStatus != null) {
			for (Status s : allowedStatus) {
				if (s.equals(candidateState)) {
					valid = true;
				}
			}
		}
		
		if(valid)
		{
			order.setLastUpdate(Timestamp.valueOf(LocalDateTime.now()));
			return candidateState;
		} else {
			String msg = String.format("Nao eh permitido mudar do status %s para o status %s.", 
					currentState, candidateState);
			throw new IllegalStateException(msg);
		}
	}
}
