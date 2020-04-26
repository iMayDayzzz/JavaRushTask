package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ShipController {

    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(value = "/rest/ships", method = RequestMethod.GET)
    public ResponseEntity<List<Ship>> showShips(
                                    @RequestParam(required = false, name = "name") String name,
                                    @RequestParam (required = false, name = "planet") String planet,
                                    @RequestParam (required = false, name = "shipType") String shipType,
                                    @RequestParam (required = false, name = "after") String after,
                                    @RequestParam (required = false, name = "before") String before,
                                    @RequestParam (required = false, name = "isUsed") String isUsed,
                                    @RequestParam (required = false, name = "minSpeed") String minSpeed,
                                    @RequestParam (required = false, name = "maxSpeed") String maxSpeed,
                                    @RequestParam (required = false, name = "minCrewSize") String minCrewSize,
                                    @RequestParam (required = false, name = "maxCrewSize") String maxCrewSize,
                                    @RequestParam (required = false, name = "minRating") String minRating,
                                    @RequestParam (required = false, name = "maxRating") String maxRating,
                                  @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
                                  @RequestParam( name = "pageSize", required = false, defaultValue = "3") Integer pageSize,
                                  @RequestParam(required = false) ShipOrder order)
    {
    List<Ship> listSorted = shipService.getShips(order, name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        List<Ship> listOutput = listSorted.stream().skip(pageNumber * pageSize).limit(pageSize).collect(Collectors.toList());
        /*for (Ship test : listOutput) {
            System.out.println(test.getId());
        }*/
     return new ResponseEntity<>(listOutput, HttpStatus.OK);
     }

    @RequestMapping(value = "/rest/ships/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Long> showCount(
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) String planet,
                                            @RequestParam(required = false) String shipType,
                                            @RequestParam(required = false) String after,
                                            @RequestParam(required = false) String before,
                                            @RequestParam(required = false) String isUsed,
                                            @RequestParam(required = false) String minSpeed,
                                            @RequestParam(required = false) String maxSpeed,
                                            @RequestParam(required = false) String minCrewSize,
                                            @RequestParam(required = false) String maxCrewSize,
                                            @RequestParam(required = false) String minRating,
                                            @RequestParam(required = false) String maxRating)
    {

        long count = shipService.count(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }


    @RequestMapping(value = "/rest/ships", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> createShips(@RequestBody( required =  false) Ship ship) {

        if (ship == null) {
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getName() == null || ship.getName().equals("") || ship.getName().length() > 50 || ship.getPlanet() == null || ship.getPlanet().equals("") || ship.getPlanet().length() > 50 ||
        ship.getShipType() == null || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null || ship.getSpeed() < 0.01 ||
                ship.getSpeed() > 0.99 || ship.getCrewSize() < 1 || ship.getCrewSize() > 9999 || ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019 || ship.getProdDate().getTime() < 0) {
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship createdShip = shipService.createShip(ship);

        return new ResponseEntity<>(createdShip, HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> showShipById(@PathVariable("id") String id)
    {
        long idLong;
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            idLong = Long.parseLong(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if ( idLong <= 0 ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship ship = shipService.getOne(idLong);
        if (ship == null ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(@PathVariable("id") String id,
                                                @RequestBody( required =  false) Ship ship)   {


        long idLong;
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            idLong = Long.parseLong(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if ( idLong <= 0 ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship == null) {
            return  new ResponseEntity<>(shipService.getOne(idLong), HttpStatus.OK);
        }
        if (ship.getName() != null && ship.getName().equals("")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (ship.getCrewSize() != null) {
           if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999) {
               return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
           }
        }
        if (ship.getProdDate() != null) {
            if (ship.getProdDate().getTime() < 0 || ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        boolean isEdited = shipService.edit(idLong, ship);
        if (!isEdited) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(shipService.getOne(idLong), HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") String id ) {
        long idLong;
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            idLong = Long.parseLong(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if ( idLong <= 0 ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isDeleted = shipService.delete(idLong);
        if (!isDeleted) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
