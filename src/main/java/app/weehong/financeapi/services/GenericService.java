package app.weehong.financeapi.services;

import java.text.ParseException;
import java.util.List;

public interface GenericService<T, K> {
    T create(K t) throws ParseException;

    List<T> all();

    T getById(Long id);

    T update(Long id, K t) throws ParseException;

    boolean delete(Long id);
}
