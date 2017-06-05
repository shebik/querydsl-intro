package net.test;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import net.test.dto.PersonDTO;
import net.test.entity.Address;
import net.test.entity.Person;
import net.test.entity.QAddress;
import net.test.entity.QPerson;
import net.test.repository.AddressRepository;
import net.test.repository.PersonRepository;
import org.hibernate.LazyInitializationException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Iterator;
import java.util.List;

import static net.test.predicate.PersonPredicates.nameEquals;
import static net.test.predicate.PersonPredicates.nameLike;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class QuerydslIntroApplicationTests {

    public static final String NAME = "NAME";
    public static final String SURNAME = "SURNAME";

    public static final String STREET = "STREET";

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() {
        personRepository.deleteAll();

        Person person = new Person();
        person.setName(NAME);
        person.setSurname(SURNAME);

        Address address = new Address();
        address.setStreet(STREET);
        person.getAddresses().add(address);
        address.setPerson(person);

        personRepository.save(person);
    }

    // ------------------------------------------------------
    // Setup verification
    // ------------------------------------------------------

    @Test
    public void testInsert() {
        List<Person> all = personRepository.findAll();

        assertEquals(1, all.size());
        assertEquals(NAME, all.get(0).getName());
        assertEquals(SURNAME, all.get(0).getSurname());
    }

    // ------------------------------------------------------
    // Type safe queries intro
    // ------------------------------------------------------

    @Test
    public void testFindByNameHQLInline() {
        TypedQuery<Person> query = entityManager.createQuery("from Person p where p.name = " + NAME, Person.class);
        Person person = query.getSingleResult();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindByNameHQL() {
        TypedQuery<Person> query = entityManager.createQuery("from Person p where p.name = :name", Person.class);
        query.setParameter("name", NAME);
        Person person = query.getSingleResult();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindByNameNamedQuery() {
        TypedQuery<Person> query = entityManager.createNamedQuery(Person.QUERY_FIND_BY_NAME, Person.class);
        query.setParameter("name", NAME);
        Person person = query.getSingleResult();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAllHQL_IllegalArgumentException() {
        Query query = entityManager.createQuery("from Person p where p.name = :name");
        query.setParameter("name", 1L);
        List<Person> all = query.getResultList();

        assertEquals(1, all.size());
        assertEquals(NAME, all.get(0).getName());
        assertEquals(SURNAME, all.get(0).getSurname());
    }

    // ------------------------------------------------------
    // Querydsl intro
    // ------------------------------------------------------

    @Test
    public void testFindAllQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        List<Person> all = jpaQueryFactory.selectFrom(QPerson.person).fetch();

        assertEquals(1, all.size());
        assertEquals(NAME, all.get(0).getName());
        assertEquals(SURNAME, all.get(0).getSurname());
    }

    @Test
    public void testFindOneQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Person person = jpaQueryFactory.selectFrom(QPerson.person).fetchOne();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test(expected = NonUniqueResultException.class)
    public void testFindOneQuerydsl_NonUniqueResultException() {

        Person p = new Person();
        p.setName(NAME);
        p.setSurname(SURNAME);
        personRepository.save(p);

        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Person person = jpaQueryFactory.selectFrom(QPerson.person).fetchOne();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindByNameQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Person person = jpaQueryFactory.selectFrom(QPerson.person).where(QPerson.person.name.eq(NAME)).fetchOne();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindByNameQuerydslAnd() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Person person = jpaQueryFactory.selectFrom(QPerson.person).where(QPerson.person.name.eq(NAME).and(QPerson
                .person.surname.eq(SURNAME))).fetchOne();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindByNameQuerydslOr() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Person person = jpaQueryFactory.selectFrom(QPerson.person).where(QPerson.person.name.eq(NAME).or(QPerson
                .person.surname.eq(SURNAME))).fetchOne();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindByNameQuerydslOrderBy() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Person person = jpaQueryFactory.selectFrom(QPerson.person).where(QPerson.person.name.eq(NAME)).orderBy
                (QPerson.person.name.asc()).fetchOne();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindByNameQuerydslGroupBy() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        String name = jpaQueryFactory.select(QPerson.person.name).from(QPerson.person).where(QPerson.person.name.eq
                (NAME)).groupBy(QPerson.person.name).fetchOne();

        assertEquals(NAME, name);
    }

    @Test
    public void testFindAllQuerydslPageable() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        List<Person> all = jpaQueryFactory.selectFrom(QPerson.person).offset(1).limit(1).fetch();

        assertTrue(all.isEmpty());
    }

    @Test
    @javax.transaction.Transactional
    public void testDeleteQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

        // JPQL Language Reference 10.2.9.
        // A delete operation only applies to entities of the specified class and its subclasses.
        // It does not cascade to related entities.
        jpaQueryFactory.delete(QAddress.address).where(QAddress.address.street.eq(STREET)).execute();
        jpaQueryFactory.delete(QPerson.person).where(QPerson.person.name.eq(NAME)).execute();

        assertEquals(0, personRepository.count());
    }

    @Test
    @javax.transaction.Transactional
    public void testUpdateQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        jpaQueryFactory.update(QPerson.person).set(QPerson.person.name, SURNAME).where(QPerson.person.name.eq(NAME))
                .execute();

        Person person = jpaQueryFactory.selectFrom(QPerson.person).where(QPerson.person.name.eq(SURNAME)).fetchOne();

        // we need to refresh the state from DB, otherwise NAME = "NAME"
        entityManager.refresh(person);

        assertEquals(SURNAME, person.getName());
    }

    // ------------------------------------------------------
    // Collection loading
    // ------------------------------------------------------

    @Test(expected = LazyInitializationException.class)
    public void testLoadCollection_LazyInitializationException() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Person person = jpaQueryFactory.selectFrom(QPerson.person).where(QPerson.person.name.eq(NAME)).fetchOne();

        person.getAddresses().size();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testLoadCollectionHQL() {
        TypedQuery<Person> query = entityManager.createQuery("from Person p LEFT JOIN FETCH p.addresses", Person.class);
        Person person = query.getSingleResult();

        person.getAddresses().size();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testLoadCollectionQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        Person person = jpaQueryFactory.selectFrom(QPerson.person).where(QPerson.person.name.eq(NAME)).leftJoin
                (QPerson.person.addresses).fetchJoin().fetchOne();

        person.getAddresses().size();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testProjectionsQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        PersonDTO personDTO = jpaQueryFactory.select(Projections.constructor(PersonDTO.class, QPerson.person.id,
                QPerson.person.name, QPerson.person.surname)).from(QPerson.person).fetchOne();

        assertEquals(NAME, personDTO.getName());
        assertEquals(SURNAME, personDTO.getSurname());
    }

    // ------------------------------------------------------
    // Subqueries and functions/procedures
    // ------------------------------------------------------

    @Test
    public void testSubqueryQuerydsl() {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        QPerson p = new QPerson("p");

        Person person = jpaQueryFactory.selectFrom(QPerson.person).where(QPerson.person.name.eq(JPAExpressions.select
                (p.name.max()).from(p))).fetchOne();

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    // ------------------------------------------------------
    // Spring Data JPA integration
    // ------------------------------------------------------

    @Test
    public void testFindAllSpring() {
        Iterable<Person> all = personRepository.findAll(QPerson.person.name.eq(NAME));

        int size = 0;
        Iterator<Person> iterator = all.iterator();
        while (iterator.hasNext()) {
            size++;
            iterator.next();
        }

        Person person = all.iterator().next();

        assertEquals(1, size);
        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindOneSpring() {
        Person person = personRepository.findOne(QPerson.person.name.eq(NAME));

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test(expected = NonUniqueResultException.class)
    public void testFindOneSpring_NonUniqueResultException() {

        Person p = new Person();
        p.setName(NAME);
        p.setSurname(SURNAME);
        personRepository.save(p);

        Person person = personRepository.findOne(QPerson.person.name.eq(NAME));

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindByNameSpringAnd() {
        Person person = personRepository.findOne(nameEquals(NAME).and(nameLike(NAME)));

        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindByNameSpringOrderBy() {
        Iterable<Person> all = personRepository.findAll(QPerson.person.name.eq(NAME), QPerson.person.name.asc());

        int size = 0;
        Iterator<Person> iterator = all.iterator();
        while (iterator.hasNext()) {
            size++;
            iterator.next();
        }

        Person person = all.iterator().next();

        assertEquals(1, size);
        assertEquals(NAME, person.getName());
        assertEquals(SURNAME, person.getSurname());
    }

    @Test
    public void testFindAllSpringPageable() {

        Page<Person> all = personRepository.findAll(QPerson.person.name.eq(NAME), new PageRequest(1, 1));

        assertEquals(1, all.getNumberOfElements());
        assertEquals(NAME, all.getContent().get(0).getName());
        assertEquals(SURNAME, all.getContent().get(0).getSurname());
    }
}
