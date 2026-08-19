package com.clinic.dao;

import com.clinic.model.HelpGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelpGuideRepository extends JpaRepository<HelpGuide, Long> {
}
