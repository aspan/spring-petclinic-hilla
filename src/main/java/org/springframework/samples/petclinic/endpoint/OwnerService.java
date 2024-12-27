package org.springframework.samples.petclinic.endpoint;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.backend.owner.Owner;
import org.springframework.samples.petclinic.backend.owner.OwnerName;
import org.springframework.samples.petclinic.backend.owner.OwnerRepository;
import org.springframework.samples.petclinic.backend.owner.Pet;
import org.springframework.samples.petclinic.backend.visit.VisitRepository;
import org.springframework.samples.petclinic.endpoint.record.OwnerRecord;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;

@BrowserCallable
@AnonymousAllowed
public class OwnerService {

	private final OwnerRepository ownerRepository;
	private final VisitRepository visitRepository;

	public OwnerService(OwnerRepository ownerRepository, VisitRepository visitRepository) {
		this.ownerRepository = ownerRepository;
		this.visitRepository = visitRepository;
	}

	private OwnerRepository getRepository() {
		return ownerRepository;
	}

	public Page<OwnerRecord> findByLastName(String lastName, Pageable pageable) {
		return getRepository().findByLastName(lastName, pageable).map(OwnerRecord::fromOwner);
	}

	public int countByLastName(String lastName) {
		return getRepository().countByLastName(lastName);
	}

	public OwnerName findPersonById(Integer id) {
		Optional<OwnerName> personById = getRepository().findPersonById(id);
		return personById.orElseThrow();
	}

	public OwnerRecord findOwner(Integer ownerId) {
		Owner owner = getRepository().findById(ownerId).orElseThrow();
		for (Pet pet : owner.getPets()) {
			visitRepository.findByPetId(pet.getId()).forEach(pet::addVisit);
		}

		return OwnerRecord.fromOwner(owner);
	}

	public OwnerRecord save(OwnerRecord value) {
		return OwnerRecord.fromOwner(getRepository().save(value.toOwner()));
	}

	public Optional<OwnerRecord> get(Integer petId) {
		return getRepository().findById(petId).map(OwnerRecord::fromOwner);
	}
}
