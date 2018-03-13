package com.cosmeticos.commons;

import com.cosmeticos.model.BalanceItem;
import lombok.Data;

import java.util.List;

@Data
public class Balance {

    private String message;

    private List<BalanceItem> balanceItemList;

}
