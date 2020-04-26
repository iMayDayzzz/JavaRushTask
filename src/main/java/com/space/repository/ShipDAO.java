package com.space.repository;

import com.space.model.Ship;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipDAO extends JpaRepository<Ship, Long> {

}
