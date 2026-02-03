package com.congratulator.beta.repository;

import com.congratulator.beta.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {

    @Query("SELECT p FROM Person p " +
            "WHERE EXTRACT(MONTH FROM p.birthDate) = :month " +
            "AND EXTRACT(DAY FROM p.birthDate) = :day")
    List<Person> findByBirthdayMonthAndDay(int month, int day);

    @Query(nativeQuery = true, value = """
        SELECT * FROM persons
        WHERE 
          (
            birthdate + (EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM birthdate)) * INTERVAL '1 YEAR'
            BETWEEN CURRENT_DATE + ( 1 || ' DAY')::INTERVAL AND CURRENT_DATE + (:days || ' DAY')::INTERVAL
          )
          OR
          (
            birthdate + (EXTRACT(YEAR FROM CURRENT_DATE) + 1 - EXTRACT(YEAR FROM birthdate)) * INTERVAL '1 YEAR'
            BETWEEN CURRENT_DATE + ( 1 || ' DAY')::INTERVAL AND CURRENT_DATE + (:days || ' DAY')::INTERVAL
          )
        """)
        List<Person> findNearBirthday(@Param("days") int days);
}
