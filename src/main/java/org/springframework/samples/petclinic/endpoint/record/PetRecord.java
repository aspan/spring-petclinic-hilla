package org.springframework.samples.petclinic.endpoint.record;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.backend.owner.Owner;
import org.springframework.samples.petclinic.backend.owner.Pet;
import org.springframework.samples.petclinic.backend.owner.PetType;

import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;

public record PetRecord(
        @Nullable Integer id,
        @Nonnull @NotEmpty(message = "The name is required") String name,
        @Nonnull @NotNull(message = "The birth date is required") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
        @Nonnull @NotNull(message = "The type is required") PetType type,
        @Nullable @Length(min = 3, max = 100) String description,
        List<VisitRecord> visits) {

    public Pet toPet(Owner owner) {
        var pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setBirthDate(birthDate);
        pet.setType(type);
        pet.setDescription(description);
        pet.setOwner(owner);
        return pet;
    }

    public static PetRecord fromPet(Pet pet) {
        return new PetRecord(
                pet.getId(),
                pet.getName(),
                pet.getBirthDate(),
                pet.getType(),
                pet.getDescription(),
                pet.getVisits().stream().map(v -> new VisitRecord(v.getDate(), v.getDescription(), v.getPetId())).toList());
    }
}
