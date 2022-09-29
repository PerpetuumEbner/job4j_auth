package ru.job4j.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    private final BCryptPasswordEncoder encoder;

    public PersonController(PersonService personService, BCryptPasswordEncoder encoder) {
        this.personService = personService;
        this.encoder = encoder;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Person>> findAll() {
        return new ResponseEntity<>(personService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.personService.findById(id);
        if (person.isEmpty()) {
            throw new NullPointerException("Username and Password cannot be empty!");
        }
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        if (person.getPassword().isEmpty() || person.getUsername().isEmpty()) {
            throw new NullPointerException("Username and Password cannot be empty!");
        }
        if (person.getUsername().length() < 3 || person.getUsername().length() > 16) {
            throw new IllegalArgumentException("The name must be no shorter than three and no longer than sixteen characters!");
        }
        if (person.getPassword().length() < 8) {
            throw new IllegalArgumentException("The password cannot be less than eight characters!");
        }
        if (personService.findByUsername(person.getUsername()).isPresent()) {
            throw new IllegalArgumentException("This name already exists!");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        return new ResponseEntity<>(personService.create(person), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        personService.create(person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        personService.delete(person);
        return ResponseEntity.ok().build();
    }
}