package com.congratulator.beta.controller;

import com.congratulator.beta.dto.*;
import com.congratulator.beta.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonDTO>> getAll() {
        return ResponseEntity.ok(personService.getAll());
    }

    @GetMapping("/today")
    public ResponseEntity<List<PersonDTO>> getTodayBirthdays() {
        return ResponseEntity.ok(personService.getTodayBirthdays());
    }

    @GetMapping("/near")
    public ResponseEntity<List<PersonDTO>> getNearBirthdays(@RequestParam(defaultValue = "14") int days) {
        return ResponseEntity.ok(personService.getNearBirthdays(days));
    }

//    @PostMapping
//    public ResponseEntity<PersonDTO> create(
//            @Valid @RequestBody CreatePersonRequest createPersonRequest) {
//        PersonDTO created = personService.createPerson(createPersonRequest);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(created);
//    }
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<PersonDTO> partialUpdate(
//            @PathVariable UUID id,
//            @Valid @RequestBody UpdatePersonRequest request) {
//        request.setId(id);
//        PersonDTO updated = personService.partialUpdate(request);
//        return ResponseEntity.ok(updated);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/with-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PersonDTO> createWithPhoto(
            @ModelAttribute @Valid Creation request) {

        CreatePersonRequest personRequest = CreatePersonRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthDate())
                .build();

        PersonDTO created = personService.createPerson(personRequest, request.getPhoto());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping(value = "/{id}/with-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PersonDTO> updateWithPhoto( @PathVariable UUID id,
            @ModelAttribute @Valid Creation request) {

        UpdatePersonRequest personRequest = UpdatePersonRequest.builder()
                .id(id)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthDate())
                .build();

        PersonDTO updated = personService.partialUpdate(personRequest, request.getPhoto());
        return ResponseEntity.ok(updated);
    }
//
//    @GetMapping("/{id}/photo")
//    public ResponseEntity<Resource> photo(@PathVariable UUID id) throws IOException {
//        String pathStr = String.valueOf(personService.getFoto(id));
//        if (pathStr == null || pathStr.isBlank()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Path file = Path.of(pathStr);
//        if (!Files.exists(file) || !Files.isRegularFile(file)) {
//            return ResponseEntity.notFound().build();
//        }
//
//        var res = new org.springframework.core.io.FileSystemResource(file);
//
//        String ct = Files.probeContentType(file);
//        MediaType mt = (ct != null) ? MediaType.parseMediaType(ct) : MediaType.APPLICATION_OCTET_STREAM;
//
//        return ResponseEntity.ok()
//                .contentType(mt)
//                .contentLength(Files.size(file))
//                .cacheControl(CacheControl.maxAge(Duration.ofDays(7)))
//                .body(res);
//    }


}