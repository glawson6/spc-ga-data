package com.taptech.spoonscore.repository;

import com.taptech.spoonscore.entity.ZipCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by tap on 3/29/15.
 */
public interface ZipCodesRepository extends JpaRepository<ZipCodes, Long> {

    ZipCodes findOneByCounty(String activationKey);

    List<ZipCodes> findAllByCity(String city);

    ZipCodes findOneByZipCode(Integer zipCode);

}

