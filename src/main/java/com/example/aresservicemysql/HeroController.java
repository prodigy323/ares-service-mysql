package com.example.aresservicemysql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping(path="/admin")
public class HeroController {

    @Autowired
    private HeroRepository heroRepository;

    @RequestMapping (value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Hero> createHero (@RequestBody Hero hero) {

        log.info("POST hero");
        heroRepository.save(hero);

        return new ResponseEntity<>(hero, HttpStatus.OK);

    }

    @RequestMapping (value = "/addList", method = RequestMethod.POST)
    public ResponseEntity<List<Hero>> createHeroes (@RequestBody List<Hero> heroes) {

        log.info("POST list of heroes");
        heroRepository.saveAll(heroes);

        return new ResponseEntity<>(heroes, HttpStatus.OK);

    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<Hero> updateUser(@RequestBody Hero hero) {

        log.info("PUT hero");
        heroRepository.save(hero);

        return new ResponseEntity<>(hero, HttpStatus.OK);
    }

    @RequestMapping (value = "/updateList", method = RequestMethod.PUT)
    public ResponseEntity<List<Hero>> updateHeroes (@RequestBody List<Hero> heroes) {

        log.info("PUT list of heroes");
        heroRepository.saveAll(heroes);

        return new ResponseEntity<>(heroes, HttpStatus.OK);

    }

    @RequestMapping (value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Optional<Hero>> deleteHero (@PathVariable("id") Long id) {

        log.info("DELETE hero");
        Optional<Hero> deletedHero = heroRepository.findById(id);

        heroRepository.deleteById(id);

        return new ResponseEntity<>(deletedHero, HttpStatus.OK);

    }

    @RequestMapping (value = "/deleteAll", method = RequestMethod.DELETE)
    public ResponseEntity<Iterable<Hero>> deleteAll () {

        log.info("DELETE hero");
        Iterable<Hero> deletedHeroes = heroRepository.findAll();

        heroRepository.deleteAll();

        return new ResponseEntity<>(deletedHeroes, HttpStatus.OK);

    }

}
