package com.tiliregister.app.controller;

import com.tiliregister.app.model.TillFunctionMap;
import com.tiliregister.app.model.TillFunctionMapRequest;
import com.tiliregister.app.service.TillFunctionMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/till-function-map")
public class TillFunctionMapController {

    private final TillFunctionMapService tillFunctionMapService;

    @Autowired
    public TillFunctionMapController(TillFunctionMapService tillFunctionMapService) {
        this.tillFunctionMapService = tillFunctionMapService;
    }

    @PostMapping
    public ResponseEntity<String> createTillFunctionMap(
            @RequestBody TillFunctionMapRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        tillFunctionMapService.assignFunctionsToTill(
                request.getTillId(),
                request.getTillFunctionIds(),
                username
        );
        return ResponseEntity.ok("Till Functions assigned successfully");
    }

    @GetMapping("/{id}")
    ResponseEntity<TillFunctionMap> getTillFunctionMapById(@PathVariable Long id){
        return ResponseEntity.ok(tillFunctionMapService.getTillFunctionById(id));
    }

    @GetMapping("/till-id/{id}")
    ResponseEntity<List<TillFunctionMap>> getTillFunctionMapByTillId(@PathVariable Long id){
        return ResponseEntity.ok(tillFunctionMapService.getTillFunctionByTillId(id));
    }

    @GetMapping("/function-id/{id}")
    ResponseEntity<List<TillFunctionMap>> getTillFunctionMapByFunctionId(@PathVariable Long id){
        return ResponseEntity.ok(tillFunctionMapService.getTillFunctionByFunctionId(id));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> deleteTillFunctionMap(@PathVariable Long id){
        boolean success = tillFunctionMapService.deleteTillFunctionById(id);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/till/{id}/till-functions")
    ResponseEntity<?> removeFunctionsFromTill(@PathVariable Long id, @RequestParam Set<Long> functionIds){
        boolean success = tillFunctionMapService.removeFunctionsFromTill(id, functionIds);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
