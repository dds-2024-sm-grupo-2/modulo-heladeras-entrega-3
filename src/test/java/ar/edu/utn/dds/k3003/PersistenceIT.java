package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Temperatura;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

public class PersistenceIT {

    static EntityManagerFactory entityManagerFactory ;
    EntityManager entityManager ;

    @BeforeAll
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("tpDDS");
    }
    @BeforeEach
    public void setup() throws Exception {
        entityManager = entityManagerFactory.createEntityManager();
    }
    @Test
    public void testConectar() {
// vacío, para ver que levante el ORM
    }
    @Test
    public void testInsert() {
        Heladera heladera = new Heladera("Carlos");
        Heladera heladera1 = new Heladera("Tomas");
        Heladera heladera2 = new Heladera("Lucio");
        Heladera heladera3 = new Heladera("Puto");
        entityManager.getTransaction().begin();
        entityManager.persist(heladera);
        entityManager.persist(heladera1);
        entityManager.persist(heladera2);
        entityManager.persist(heladera3);
        entityManager.getTransaction().commit();


    }
}
