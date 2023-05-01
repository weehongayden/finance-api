package app.weehong.financeapi.services;

import java.util.List;

public interface GenericService<T, K> {

  T create(String userId, K t);

  List<T> all(String userId);

  T getById(Long id, String userId);

  T update(Long id, String userId, K t);

  boolean delete(Long id, String userId);
}
