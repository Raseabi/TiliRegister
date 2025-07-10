package com.tiliregister.app.controller;

import com.tiliregister.app.model.TillFunction;
import com.tiliregister.app.service.TillFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/till-functions")
public class TillFunctionController {

    private final TillFunctionService tillFunctionService;

    @Autowired
    public TillFunctionController(TillFunctionService tillFunctionService) {
        this.tillFunctionService = tillFunctionService;
    }
    @PreAuthorize("hasAuthority('till:function:create')")
    @PostMapping
    ResponseEntity<TillFunction> createTillFunction(@RequestBody TillFunction tillFunction, Authentication authentication){
        String username = authentication.getName();
        TillFunction savedTillFunction = tillFunctionService.saveTillFunction(tillFunction, username);

        return ResponseEntity.ok(savedTillFunction);
    }

    @PreAuthorize("hasAuthority('till:function:view')")
    @GetMapping
    ResponseEntity<List<TillFunction>> getAllTillFunctions(){
        return ResponseEntity.ok(tillFunctionService.getAllTillFunctions());
    }

    @PreAuthorize("hasAuthority('till:function:view')")
    @GetMapping("/active")
    ResponseEntity<List<TillFunction>> getActiveTillFunctions(){
        return ResponseEntity.ok(tillFunctionService.getActiveTillFunctions());
    }

    @PreAuthorize("hasAuthority('till:function:view')")
    @GetMapping("/non-active")
    ResponseEntity<List<TillFunction>> getNonActiveTillFunctions(){
        return ResponseEntity.ok(tillFunctionService.getNonActiveTillFunctions());
    }

    @PreAuthorize("hasAuthority('till:function:view')")
    @GetMapping("/id/{id}")
    ResponseEntity<TillFunction> getTillFunctionById(@PathVariable Long id){
        return ResponseEntity.ok(tillFunctionService.getTillFunctionById(id));
    }

    @PreAuthorize("hasAuthority('till:function:view')")
    @GetMapping("/name/{name}")
    ResponseEntity<TillFunction> getTillFunctionById(@PathVariable String name){
        return ResponseEntity.ok(tillFunctionService.getTillFunctionByName(name));
    }

    @PreAuthorize("hasAuthority('till:function:edit')")
    @PutMapping("/{id}")
    ResponseEntity<TillFunction> updateTillFunction(@PathVariable Long id, @RequestBody TillFunction tillFunction, Authentication authentication){
        String username = authentication.getName();
        ///////////////////////
        System.out.println("Received TillFunction:");
        System.out.println("Name: " + tillFunction.getName());
        System.out.println("Cash Direction: " + tillFunction.getCashChangeDirection());
        System.out.println("Float Direction: " + tillFunction.getFloatChangeDirection());
        /// ///////////////////
        TillFunction updateTillFunction = tillFunctionService.updateTillFunction(id, tillFunction, username);
        return ResponseEntity.ok(updateTillFunction);
    }

    @PreAuthorize("hasAuthority('till:function:delete')")
    @PutMapping("/{id}/void")
    ResponseEntity<TillFunction> voidTillFunction(@PathVariable Long id, Authentication authentication){
       String username = authentication.getName();
        TillFunction voidTillFunction = tillFunctionService.voidTillFunction(id, 1, username);
       return ResponseEntity.ok(voidTillFunction);
    }

    @PreAuthorize("hasAuthority('till:function:delete')")
    @PutMapping("/{id}/restore")
    ResponseEntity<TillFunction> restoreTillFunction(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        TillFunction restoreTillFunction = tillFunctionService.voidTillFunction(id, 0, username);
        return ResponseEntity.ok(restoreTillFunction);
    }

}
