package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DeveloperController {
    public Map<Integer, Developer> developers;
    private Taxable taxable;

    @Bean
    public double getTaxRate(){
        return .10;
    }
    @Bean
    public int getDeveloperId(){
        return 0;
    }

    @Bean
    public String getDeveloperName(){
        return "dev";
    }
    @Bean
    public Experience getDevExperience(){
        return Experience.MID;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
        Developer juniorDev = new JuniorDeveloper(1, "Fatih", 123, Experience.JUNIOR);
        Developer midDev = new MidDeveloper(2, "Eker", 231, Experience.MID);
        Developer seniorDev = new SeniorDeveloper(3, "Ayşe", 321, Experience.SENIOR);
        developers.put(juniorDev.getId(),juniorDev);
        developers.put(midDev.getId(),midDev);
        developers.put(seniorDev.getId(),seniorDev);
    }

    @GetMapping("/developers")
    public List<Developer> getDeveloperList() {
        return developers.values().stream().toList();
    }

    @GetMapping("/developers/{id}")
    public Developer getDeveloper(@PathVariable int id) {
        return developers.get(id);
    }

    @PostMapping("/developers")
    public ResponseEntity<Developer> addDeveloper(@RequestBody Developer developer) {
        Experience experience = developer.getExperience();
        Developer newDeveloper;
        switch (experience) {
            case JUNIOR ->
                    newDeveloper = new JuniorDeveloper(developer.getId(), developer.getName(), developer.getSalary() - developer.getSalary() * taxable.getSimpleTaxRate(), developer.getExperience());
            case MID ->
                    newDeveloper = new MidDeveloper(developer.getId(), developer.getName(), developer.getSalary() - developer.getSalary() * taxable.getMiddleTaxRate(), developer.getExperience());
            case SENIOR ->
                    newDeveloper = new SeniorDeveloper(developer.getId(), developer.getName(), developer.getSalary() - developer.getSalary() * taxable.getUpperTaxRate(), developer.getExperience());
            default -> newDeveloper = null;
        }
        if (newDeveloper == null) return null;
        developers.put(developer.getId(), newDeveloper);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/developers/{id}")
    public Developer updateDeveloper(@PathVariable int id, @RequestBody Developer dev) {
        Developer developer = developers.get(id);
        developer.setId(dev.getId());
        developer.setName(dev.getName());
        developer.setSalary(dev.getSalary());
        developer.setExperience(dev.getExperience());
        return developer;
    }

    @DeleteMapping("/developers/{id}")
    public ResponseEntity<Developer> deleteDeveloper(@PathVariable int id) {
        return ResponseEntity.ok(developers.remove(id));
    }



    public DeveloperController( Taxable taxable) {
        this.taxable = taxable;
    }


}
