package ru.job4j.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.auth.model.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {
    List<Person> findAll();

    Optional<Person> findByUsername(String username);
}