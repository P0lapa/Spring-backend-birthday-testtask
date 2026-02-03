package com.congratulator.beta.service;

import com.congratulator.beta.dto.CreatePersonRequest;
import com.congratulator.beta.dto.PersonDTO;
import com.congratulator.beta.dto.UpdatePersonRequest;
import com.congratulator.beta.entity.Person;
import com.congratulator.beta.mapper.PersonMapper;
import com.congratulator.beta.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PhotoService photoService;

    @Value("${upload.dir:/uploads}")
    private String uploadDir;

    public List<PersonDTO> getAll() {
        return personRepository.findAll().stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PersonDTO> getTodayBirthdays() {
        LocalDate today = LocalDate.now();
        return personRepository.findByBirthdayMonthAndDay(
                        today.getMonthValue(),
                        today.getDayOfMonth()
                ).stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PersonDTO> getNearBirthdays(int days) {
        return personRepository.findNearBirthday(days).stream()
                .map(personMapper::toDto)
                .collect(Collectors.toList());
    }

    public PersonDTO createPerson(CreatePersonRequest createPersonRequest, MultipartFile photo) {
        Person person = personMapper.toEntity(createPersonRequest);

        if (photo != null && !photo.isEmpty()) {
            String filename = photoService.savePhoto(photo);
            person.setPhotoPath(filename);
        }

        Person savedPerson = personRepository.save(person);
        return personMapper.toDto(savedPerson);
    }

    public PersonDTO partialUpdate(UpdatePersonRequest request, MultipartFile photo) {
        UUID personId = request.getId();

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Person с ID %s не найден", personId)
                ));

        personMapper.updateEntity(person, request);

        if (photo != null && !photo.isEmpty()) {
            if (person.getPhotoPath() != null) {
                photoService.deletePhoto(person.getPhotoPath());
            }
            String filename = photoService.savePhoto(photo);
            person.setPhotoPath(filename);
        }

        Person saved = personRepository.save(person);
        return personMapper.toDto(saved);
    }

    public void delete(UUID personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Person с ID %s не найден", personId)
                ));

        if (person.getPhotoPath() != null) {
            photoService.deletePhoto(person.getPhotoPath());
        }

        personRepository.deleteById(personId);
    }
}
