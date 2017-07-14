package com.cosmeticos.commons;

import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Customer;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 07/07/2017.
 */
@Data
public class CreditCardResponseBody {
    private String description;

    private List<CreditCard> creditCardList = new ArrayList<>(10);

    public CreditCardResponseBody() {
    }

    public CreditCardResponseBody(CreditCard creditCard) {
        this.creditCardList.add(creditCard);
    }


}
