package com.cosmeticos.commons;

import com.cosmeticos.model.CreditCard;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 07/07/2017.
 */
@Data
public class CreditCardResponseBody {

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class
    })
    private String description;

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class
    })
    private List<CreditCard> creditCardList = new ArrayList<>(10);

    public CreditCardResponseBody() {
    }

    public CreditCardResponseBody(CreditCard creditCard) {
        this.creditCardList.add(creditCard);
    }


}
