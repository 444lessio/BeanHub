package com.beanhub.beanhub.repository;

import com.beanhub.beanhub.model.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, String> {
}