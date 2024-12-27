package org.springframework.samples.petclinic.endpoint.record;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import org.springframework.samples.petclinic.backend.owner.Owner;

import com.vaadin.hilla.Nonnull;
import com.vaadin.hilla.Nullable;

/**
 * @author Andreas Asplund
 */
public record OwnerRecord(
        @Nullable Integer id,
        @Nonnull @NotEmpty(message = "The first name is required") @Length(min = 3) String firstName,
        @Nonnull @NotEmpty(message = "The last name is required") String lastName,
        @Nonnull @NotEmpty(message = "The address is required") String address,
        @Nonnull @NotEmpty(message = "The city is required") String city,
        @Nonnull @NotEmpty(message = "The phone number is required") String telephone,
        @Nonnull @Valid List<@Nonnull @Valid PetRecord> pets) {

    public Owner toOwner() {
        var owner = new Owner();
        owner.setId(id);
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress(address);
        owner.setCity(city);
        owner.setTelephone(telephone);
        owner.setPetsInternal(pets.stream().map(p -> p.toPet(owner)).collect(Collectors.toSet()));
        return owner;
    }

    public static OwnerRecord fromOwner(Owner owner) {
        return new OwnerRecord(
                owner.getId(),
                owner.getFirstName(),
                owner.getLastName(),
                owner.getAddress(),
                owner.getCity(),
                owner.getTelephone(),
                owner.getPets().stream().map(PetRecord::fromPet).collect(Collectors.toList()));
    }
}
