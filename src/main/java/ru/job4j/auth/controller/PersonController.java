package ru.job4j.auth.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.auth.dto.PersonDTO;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.PersonService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService personService;

    private final BCryptPasswordEncoder encoder;

    private final ModelMapper modelMapper;

    @Autowired
    public PersonController(PersonService personService, BCryptPasswordEncoder encoder, ModelMapper modelMapper) {
        this.personService = personService;
        this.encoder = encoder;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PersonDTO>> findAll() {
        return new ResponseEntity<>(personService.findAll().stream().map(this::convertToPersonDTO)
                .collect(Collectors.toList()), HttpStatus.OK);
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
    public ResponseEntity<Person> create(@Valid @RequestBody PersonDTO personDTO) {
        Person person = convertToPerson(personDTO);
        if (personService.findByUsername(person.getUsername()).isPresent()) {
            throw new IllegalArgumentException("This name already exists!");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        return new ResponseEntity<>(personService.create(person), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@Valid @RequestBody PersonDTO personDTO) {
        personService.create(convertToPerson(personDTO));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        personService.delete(person);
        return ResponseEntity.ok().build();
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    public PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}