package com.clinic.dao.impl;

import com.clinic.dao.PatientDAO;
import com.clinic.dao.PatientRepository;
import com.clinic.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class PatientDAOImpl implements PatientDAO {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientDAOImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    public Optional<Patient> findByNameAndContactNumber(String name, String contactNumber) {
        return patientRepository.findByNameAndContactNumber(name, contactNumber);
    }

    @Override
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }
}
