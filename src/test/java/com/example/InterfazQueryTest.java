package com.example;

import com.example.model.Curso;
import com.example.model.Estudiante;
import com.example.model.Profesor;
import jakarta.persistence.NoResultException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class InterfazQueryTest {

    // getSingleResult()
    @Test
    public void encontrarProfesorPorNombre() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Profesor> query = session.createQuery("FROM Profesor p WHERE p.nombre = :nombre", Profesor.class);
            query.setParameter("nombre", "Profesor 1");
            Profesor profesor = query.getSingleResult();
            System.out.println(profesor);
        } catch (NoResultException e) {
            System.out.println("No se encontró el profesor");
        } catch (NonUniqueResultException e) {
            System.out.println("Más de un profesor encontrado");
        } finally {
            session.close();
        }
    }


    // executeUpdate()
    @Test
    public void actualizarEmailProfesor() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Profesor p1 = session.find(Profesor.class, 1L);
        System.out.println(p1);

        Query query = session.createQuery("UPDATE Profesor p SET p.email = :nuevoEmail WHERE p.nombre = :nombre");
        query.setParameter("nuevoEmail", "nuevoemail@escuela.com");
        query.setParameter("nombre", "Profesor 1");
        int filasAfectadas = query.executeUpdate();
        session.getTransaction().commit();
        System.out.println("Filas afectadas: " + filasAfectadas);

        Profesor p2 = session.find(Profesor.class, 1L);
        System.out.println(p2);

        // Es necesario refrescar el objeto para ver los cambios,
        // incluso después de commit
        // o cerrar la sesión

        //session.refresh(p1);
        System.out.println(p1);

        session.close();


        Session s2 = HibernateUtil.getSessionFactory().openSession();
        Profesor p3 = s2.find(Profesor.class, 1L);
        System.out.println(p3);
    }


    // setParameterList()
    @Test
    public void listarEstudiantesPorListaDeIds() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Long> ids = new ArrayList();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);

        Query<Estudiante> query = session.createQuery("FROM Estudiante e WHERE e.id IN (:ids)", Estudiante.class);
        query.setParameterList("ids", ids);
        List<Estudiante> estudiantes = query.getResultList();
        estudiantes.forEach(System.out::println);
        session.close();
    }

    // setReadOnly (boolean readonly)
    // Indica que los objetos recuperados no serán modificados,
    // optimizando así el rendimiento de Hibernate
    // al evitar comprobaciones innecesarias de estado.
    @Test
    public void listarCursosSoloLectura() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Curso> query = session.createQuery("FROM Curso", Curso.class).setReadOnly(true);
        List<Curso> cursos = query.getResultList();
        cursos.forEach(System.out::println);
        session.close();
    }


    // setFirstResult(int posInicial)
    // setMaxResult(int maxResult)
    // Paginación de resultados
    @Test
    public void listarEstudiantesConPaginacion() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query<Estudiante> query = session.createQuery("FROM Estudiante", Estudiante.class);
        query.setFirstResult(0); // Empezando del primer resultado
        query.setMaxResults(3); // Máximo 3 resultados
        List<Estudiante> estudiantes = query.getResultList();
        estudiantes.forEach(System.out::println);
        session.close();
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
