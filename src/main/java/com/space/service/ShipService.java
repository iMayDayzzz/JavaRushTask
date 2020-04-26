package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ShipService {

    List<Ship> getShips(ShipOrder order, String name, String planet, String shipType,
                        String after, String before, String isUsed, String minSpeed, String maxSpeed,
                        String minCrewSize, String maxCrewSize, String minRating, String maxRating);
    long count(String name, String planet, String shipType,
              String after, String before, String isUsed, String minSpeed, String maxSpeed,
              String minCrewSize, String maxCrewSize, String minRating, String maxRating);
    void add(Ship ship);
    boolean edit(Long id, Ship ship);
    boolean delete(Long id);
    Ship getOne(Long aLong);
    Ship createShip(Ship ship);
}
