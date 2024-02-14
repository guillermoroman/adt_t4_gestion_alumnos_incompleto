package com.example;

import com.example.model.Curso;
import com.example.model.Estudiante;
import com.example.model.Profesor;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MetodosHibernateTest {

    @Test
    public void testSave() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Estudiante estudiante = new Estudiante("Estudiante nuevo", "email1@dominio.com");
        Long id = (Long) session.save(estudiante);
        session.getTransaction().commit();
        assertNotNull(id);
        session.close();
    }


    /*
    Si el objeto tiene un identificador nulo o transitorio
    (es decir, no asociado con una sesión),
    Hibernate asume que es un nuevo objeto y realiza un INSERT.\
    Si el objeto tiene un identificador y está asociado con una sesión,
    Hibernate realizará un UPDATE.
     */
    @Test
    public void testSaveOrUpdate() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Estudiante estudiante = new Estudiante("Estudiante Nuevo", "emailNuevo@dominio.com");
        session.saveOrUpdate(estudiante); // Guarda si es nuevo, actualiza si ya existe.
        session.getTransaction().commit();
        session.close();
    }


    @Test
    public void testPersist() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Estudiante estudiante = new Estudiante("Estudiante Persist", "emailPersist@dominio.com");
        session.persist(estudiante); // Guarda el estudiante, no devuelve el id
        session.getTransaction().commit();
        session.close();
    }

    /*
    Hemos estado utilizando el método find, pero realmente este pertenece a JPA
     */
    @Test
    public void testGet() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Estudiante estudiante = session.get(Estudiante.class, 1L); // Intenta obtener el estudiante con id 1
        session.close();
        assertNotNull(estudiante);
    }


    @Test
    public void testLoad() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Estudiante estudiante = session.load(Estudiante.class, 1L); // Carga el estudiante con id 1
            assertNotNull(estudiante.getNombre());
        } finally {
            session.close();
        }
    }


    @Test
    public void testRefresh() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Estudiante estudiante = session.get(Estudiante.class, 1L);
        // Supongamos que el estudiante ha sido modificado fuera de la sesión actual
        session.refresh(estudiante); // Actualiza el estado del objeto con la base de datos
        session.getTransaction().commit();
        session.close();
    }


    @Test
    public void testDelete() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Estudiante estudiante = session.get(Estudiante.class, 1L);
        session.delete(estudiante); // Elimina el estudiante
        session.getTransaction().commit();
        session.close();
    }


    @Test
    public void testEvict() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Estudiante estudiante = session.get(Estudiante.class, 1L);
        session.evict(estudiante); // El estudiante ya no está gestionado por esta sesión
        session.getTransaction().commit();
        session.close();
    }


    @Test
    public void testClear() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        // Supongamos que hemos cargado varios objetos en la sesión
        session.clear(); // Todos los objetos se desasocian de la sesión
        session.getTransaction().commit();
        session.close();
    }


    @Test
    public void testLock() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Estudiante estudiante = new Estudiante("Estudiante Lock", "emailLock@dominio.com");
        session.beginTransaction();
        session.save(estudiante);
        session.lock(estudiante, LockMode.NONE); // Reatacha el objeto a la sesión sin un bloqueo específico
        session.getTransaction().commit();
        session.close();
    }


    @Test
    public void testMerge() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Estudiante estudiante = new Estudiante("Estudiante Merge", "emailMerge@dominio.com");
        session.save(estudiante);
        session.getTransaction().commit();
        session.close();

        estudiante.setNombre("Nombre Actualizado");
        Session newSession = HibernateUtil.getSessionFactory().openSession();
        newSession.beginTransaction();
        Estudiante estudianteActualizado = (Estudiante) newSession.merge(estudiante);
        newSession.getTransaction().commit();
        newSession.close();
    }


    @BeforeEach
    public void seed(){

        // Crear estudiantes
        Estudiante e1 = new Estudiante("Estudiante 1", "e1@escuela.com");
        Estudiante e2 = new Estudiante("Estudiante 2", "e2@escuela.com");
        Estudiante e3 = new Estudiante("Estudiante 3", "e3@escuela.com");
        Estudiante e4 = new Estudiante("Estudiante 4", "e4@escuela.com");
        Estudiante e5 = new Estudiante("Estudiante 5", "e5@escuela.com");
        Estudiante e6 = new Estudiante("Estudiante 6", "e6@escuela.com");

        // Crear profesores
        Profesor p1 = new Profesor("Profesor 1", "p1@escuela.com");
        Profesor p2 = new Profesor("Profesor 2", "p2@escuela.com");

        // Crear cursos
        Curso c1 = new Curso("Curso 1", 6.0, p1);
        Curso c2 = new Curso("Curso 2", 4.0, p1);
        Curso c3 = new Curso("Curso 3", 8.0, p2);
        Curso c4 = new Curso("Curso 4", 5.0, p2);

        // Matricular estudiantes en cursos
        e1.getCursos().add(c1);
        e1.getCursos().add(c2);
        e2.getCursos().add(c2);
        e2.getCursos().add(c3);
        e2.getCursos().add(c4);
        e3.getCursos().add(c1);
        e4.getCursos().add(c2);
        e4.getCursos().add(c4);
        e5.getCursos().add(c1);
        e5.getCursos().add(c2);
        e5.getCursos().add(c3);
        e5.getCursos().add(c4);
        e6.getCursos().add(c3);
        e6.getCursos().add(c4);



        // Persistir
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.persist(p1);
        session.persist(p2);

        session.persist(c1);
        session.persist(c2);
        session.persist(c3);
        session.persist(c4);

        session.persist(e1);
        session.persist(e2);
        session.persist(e3);
        session.persist(e4);
        session.persist(e5);
        session.persist(e6);


        session.getTransaction().commit();

        session.close();

    }
}
