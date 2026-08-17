package com.clinic.dao;

import com.clinic.model.Patient;
import java.util.Optional;

public interface PatientDAO {
    Optional<Patient> findById(Long id);
    Optional<Patient> findByNameAndContactNumber(String name, String contactNumber);
    Patient save(Patient patient);
}
