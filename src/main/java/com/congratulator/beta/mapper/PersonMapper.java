package com.congratulator.beta.mapper;

import com.congratulator.beta.dto.CreatePersonRequest;
import com.congratulator.beta.dto.PersonDTO;
import com.congratulator.beta.dto.UpdatePersonRequest;
import com.congratulator.beta.entity.Person;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(target = "photoPath", expression = "java(person.getPhotoPath() != null ? \"/files/\" + person.getPhotoPath() : null)")
    PersonDTO toDto(Person person);

    Person toEntity(PersonDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoPath", ignore = true)
    Person toEntity(CreatePersonRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoPath", ignore = true)
    Person toEntity(UpdatePersonRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoPath", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Person person, UpdatePersonRequest request);


//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "photoPath", ignore = true)
//    void updateEntity(CreatePersonDTO dto, @MappingTarget Person person);
}
