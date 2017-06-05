package net.test.repository;

import net.test.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * @author Zbynek Vavros (zbynek.vavros@i.cz)
 */
public interface PersonRepository extends JpaRepository<Person, Long>, QueryDslPredicateExecutor<Person> {
}
