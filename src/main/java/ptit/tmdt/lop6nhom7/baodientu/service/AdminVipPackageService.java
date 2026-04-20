package ptit.tmdt.lop6nhom7.baodientu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ptit.tmdt.lop6nhom7.baodientu.dto.AdminVipPackageUpdateReq;
import ptit.tmdt.lop6nhom7.baodientu.dto.VipPackageRes;
import ptit.tmdt.lop6nhom7.baodientu.entity.VipPackage;
import ptit.tmdt.lop6nhom7.baodientu.exception.ConflictException;
import ptit.tmdt.lop6nhom7.baodientu.exception.NotFoundException;
import ptit.tmdt.lop6nhom7.baodientu.repository.TransactionRepo;
import ptit.tmdt.lop6nhom7.baodientu.repository.VipPackageRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminVipPackageService {
  private final VipPackageRepo vipPackageRepo;
  private final TransactionRepo transactionRepo;

  @Transactional(readOnly = true)
  public List<VipPackageRes> getAllPackages() {
    return vipPackageRepo.findAll()
        .stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public VipPackageRes getPackageById(Integer packageId) {
    return toResponse(findPackage(packageId));
  }

  @Transactional
  public VipPackageRes createPackage(AdminVipPackageUpdateReq request) {
    String packageName = request.getName().trim();

    validateUniqueName(packageName, null);

    VipPackage vipPackage = new VipPackage();
    vipPackage.setName(packageName);
    vipPackage.setDurationDays(request.getDurationDays());
    vipPackage.setPrice(request.getPrice());
    vipPackage.setDiscountPercent(request.getDiscountPercent());
    vipPackage.setDescription(normalizeDescription(request.getDescription()));
    return toResponse(vipPackageRepo.save(vipPackage));
  }

  @Transactional
  public VipPackageRes updatePackage(Integer packageId, AdminVipPackageUpdateReq request) {
    VipPackage vipPackage = findPackage(packageId);
    String packageName = request.getName().trim();

    validateUniqueName(packageName, packageId);

    vipPackage.setName(packageName);
    vipPackage.setDurationDays(request.getDurationDays());
    vipPackage.setPrice(request.getPrice());
    vipPackage.setDiscountPercent(request.getDiscountPercent());
    vipPackage.setDescription(normalizeDescription(request.getDescription()));
    return toResponse(vipPackageRepo.save(vipPackage));
  }

  @Transactional
  public void deletePackage(Integer packageId) {
    VipPackage vipPackage = findPackage(packageId);

    if (transactionRepo.existsByPackageFieldId(packageId)) {
      throw new ConflictException("Khong the xoa goi VIP da co giao dich");
    }

    vipPackageRepo.delete(vipPackage);
  }

  private void validateUniqueName(String packageName, Integer currentPackageId) {
    if (!vipPackageRepo.existsByNameIgnoreCase(packageName)) {
      return;
    }

    if (currentPackageId != null) {
      VipPackage currentPackage = findPackage(currentPackageId);
      if (currentPackage.getName().equalsIgnoreCase(packageName)) {
        return;
      }
    }

    throw new ConflictException("Ten goi VIP da ton tai");
  }

  private String normalizeDescription(String description) {
    if (description == null) {
      return null;
    }

    String normalizedDescription = description.trim();
    return normalizedDescription.isEmpty() ? null : normalizedDescription;
  }

  private VipPackage findPackage(Integer packageId) {
    return vipPackageRepo.findById(packageId)
        .orElseThrow(() -> new NotFoundException("Khong tim thay goi VIP voi id = " + packageId));
  }

  private VipPackageRes toResponse(VipPackage vipPackage) {
    return new VipPackageRes(
        vipPackage.getId(),
        vipPackage.getName(),
        vipPackage.getDurationDays(),
        vipPackage.getPrice(),
        vipPackage.getDiscountPercent(),
        vipPackage.getDescription()
    );
  }
}
