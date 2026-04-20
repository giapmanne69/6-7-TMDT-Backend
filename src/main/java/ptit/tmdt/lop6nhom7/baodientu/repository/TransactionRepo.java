package ptit.tmdt.lop6nhom7.baodientu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ptit.tmdt.lop6nhom7.baodientu.entity.Transaction;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
  boolean existsByPackageFieldId(Integer packageId);
}
