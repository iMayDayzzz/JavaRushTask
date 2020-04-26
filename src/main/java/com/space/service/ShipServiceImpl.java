package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ShipServiceImpl implements ShipService {


    ShipDAO shipDAO;
    @Autowired
    public void setShipDAO(ShipDAO shipDAO) {
        this.shipDAO = shipDAO;
    }

    @Transactional
    @Override
    public List<Ship> getShips(ShipOrder order, String name, String planet, String shipType,
                               String after, String before, String isUsed, String minSpeed, String maxSpeed,
                               String minCrewSize, String maxCrewSize, String minRating, String maxRating) {

        List<Ship> ships = shipDAO.findAll();
        if (name != null) {
            ships = ships.stream().filter(ship -> ship.getName().matches("(.*)"+name+"(.*)")).collect(Collectors.toList());
        }
        if (planet != null) {
            ships = ships.stream().filter(ship -> ship.getPlanet().matches("(.*)"+planet+"(.*)")).collect(Collectors.toList());
        }
        if (shipType != null) {
            ships = ships.stream().filter(ship -> ship.getShipType().name().equals(shipType)).collect(Collectors.toList());
        }
        if (after != null) {
            ships = ships.stream().filter(ship -> ship.getProdDate().getTime() >= Long.parseLong(after)).collect(Collectors.toList());
        }
        if (before != null) {
            ships = ships.stream().filter(ship -> ship.getProdDate().getTime() <= Long.parseLong(before)).collect(Collectors.toList());
        }
        if (isUsed != null) {
            ships = ships.stream().filter(ship -> ship.getUsed().toString().equals(isUsed)).collect(Collectors.toList());
        }
        if (minSpeed != null) {
            ships = ships.stream().filter(ship -> ship.getSpeed() >= Double.parseDouble(minSpeed)).collect(Collectors.toList());
        }
        if (maxSpeed != null) {
            ships = ships.stream().filter(ship -> ship.getSpeed() <= Double.parseDouble(maxSpeed)).collect(Collectors.toList());
        }
        if (minCrewSize !=null) {
            ships = ships.stream().filter(ship -> ship.getCrewSize() >= Integer.parseInt(minCrewSize)).collect(Collectors.toList());
        }
        if (maxCrewSize !=null) {
            ships = ships.stream().filter(ship -> ship.getCrewSize() <= Integer.parseInt(maxCrewSize)).collect(Collectors.toList());
        }
        if (minRating != null) {
            ships = ships.stream().filter(ship -> ship.getRating() >= Double.parseDouble(minRating)).collect(Collectors.toList());
        }
        if (maxRating != null) {
            ships = ships.stream().filter(ship -> ship.getRating() <= Double.parseDouble(maxRating)).collect(Collectors.toList());
        }


        if (order == null) return ships.stream().sorted(Comparator.comparing(ship -> ship.getId())).collect(Collectors.toList());
        switch (order) {
            case ID:
                ships = ships.stream().sorted(Comparator.comparing(ship -> ship.getId())).collect(Collectors.toList());
            break;
            case DATE:
                ships = ships.stream().sorted(Comparator.comparing(ship -> ship.getProdDate())).collect(Collectors.toList());
            break;
            case SPEED:
                ships = ships.stream().sorted(Comparator.comparing(ship -> ship.getSpeed())).collect(Collectors.toList());
            break;
            case RATING:
                ships = ships.stream().sorted(Comparator.comparing(ship -> ship.getRating())).collect(Collectors.toList());
            break;
        }

        return ships;
    }


    @Override
    public long count(String name, String planet, String shipType, String after, String before, String isUsed, String minSpeed, String maxSpeed, String minCrewSize, String maxCrewSize, String minRating, String maxRating) {
        return getShips(null, name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @Transactional
    @Override
    public void add(Ship ship) {
        ship.setRating(ratingCount(ship));
        if (ship.getUsed() == null) ship.setUsed(false);
        shipDAO.save(ship);
    }

    @Transactional
    @Override
    public boolean edit(Long id, Ship ship) {
        Ship ship4Edit = shipDAO.findById(id).orElse(null);
        if (ship4Edit == null) return false;

        if (ship.getName() != null) ship4Edit.setName(ship.getName());
        if (ship.getPlanet() != null) ship4Edit.setPlanet(ship.getPlanet());
        if (ship.getShipType() != null) ship4Edit.setShipType(ship.getShipType());
        if (ship.getProdDate() != null) ship4Edit.setProdDate(ship.getProdDate());
        if (ship.getUsed() != null) {
            if (ship.getUsed()) {
                ship4Edit.setUsed(true);
            } else {
                ship4Edit.setUsed(false);
            }
        }
        if (ship.getSpeed() != null) ship4Edit.setSpeed(ship.getSpeed());
        if (ship.getCrewSize() != null) ship4Edit.setCrewSize(ship.getCrewSize());

            ship4Edit.setRating(ratingCount(ship4Edit));
        shipDAO.save(ship4Edit);
        return true;
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        if (!shipDAO.existsById(id)) return false;
        shipDAO.deleteById(id);
        return true;
    }

    @Transactional
    @Override
    public Ship getOne(Long aLong) {
        if (!shipDAO.existsById(aLong)) return null;
        return shipDAO.findById(aLong).orElse(null);
    }


    @Override
    public Ship createShip(Ship ship) {
        if (ship.getUsed() == null) ship.setUsed(false);

        ship.setRating(ratingCount(ship));
        return shipDAO.save(ship);
    }


    public Double ratingCount(Ship ship) {
        Date date = ship.getProdDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int prodYear = localDate.getYear();
        final int currentYear = 3019;
        Double v = ship.getSpeed();
        Double k = ship.getUsed() ? 0.50 : 1.00;

        Double rating = ( 80.00  *  v *  k)  / ((double) currentYear - (double) prodYear + 1.00);

        return Math.round(rating * 100.0) / 100.0;
    }
}
