package com.beanhub.beanhub.repository;

import com.beanhub.beanhub.model.Drink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrinkRepository extends JpaRepository<Drink, Long> {
}