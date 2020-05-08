package dk.bringlarsen.hibernate.envarsexploration.model;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PersonTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    private final Person personTest1 = new Person()
            .setId(1)
            .setName("Test1")
            .setAge(42);

    @BeforeEach
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(personTest1);
        entityManager.getTransaction().commit();
    }

    @Test
    public void testQueryRevisionTable() {
        updateNameOfPersonWithId("UpdatedName", 1);

        String name = getNameOfPersonWithIdAndRevision(1, 1);

        assertEquals("Test1", name);
    }

    private void updateNameOfPersonWithId(String name, int id) {
        entityManager.getTransaction().begin();
        Person person = entityManager.find(Person.class, id);
        person.setName(name);
        entityManager.getTransaction().commit();
    }

    private String getNameOfPersonWithIdAndRevision(int id, int revision) {
        AuditReader reader = AuditReaderFactory.get(entityManager);
        Person person = reader.find(Person.class, id, revision);
        return person.getName();
    }
}