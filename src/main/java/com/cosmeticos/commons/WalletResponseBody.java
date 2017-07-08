package com.cosmeticos.commons;

import com.cosmeticos.model.Wallet;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lulu on 05/07/2017.
 */
@Data
public class WalletResponseBody {

    @JsonView(ResponseJsonView.WalletsFindAll.class)
    private String description;

    @JsonView(ResponseJsonView.WalletsFindAll.class)
    @Getter
    private List<Wallet> walletList = new ArrayList<>(10);

    public WalletResponseBody() {
    }

    public WalletResponseBody(Wallet wallet) {
        this.walletList.add(wallet);
    }
}
