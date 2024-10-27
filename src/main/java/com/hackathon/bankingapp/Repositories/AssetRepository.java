package com.hackathon.bankingapp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.bankingapp.Entities.Asset;
import com.hackathon.bankingapp.Entities.User;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByUserAndAssetSymbol(User user, String assetSymbol);
    List<Asset> findByUser(User user);
}
