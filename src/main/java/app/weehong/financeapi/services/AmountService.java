package app.weehong.financeapi.services;

import java.util.List;

public interface AmountService<T, K> extends GenericService<T, K> {

    List<T> all(String id);
}
