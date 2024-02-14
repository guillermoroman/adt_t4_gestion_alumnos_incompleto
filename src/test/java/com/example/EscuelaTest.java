package com.example;

import com.example.model.Curso;
import com.example.model.Estudiante;
import com.example.model.Profesor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EscuelaTest {

    @Test
    public void buscarEstudianteTest(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Estudiante e = session.find(Estudiante.class, 1L);
        Set<Curso> cursos = e.getCursos();

        // Impresión
        System.out.println("==================================");
        System.out.println("Estudiante con ID = 1 y sus cursos");
        System.out.println("----------------------------------");
        System.out.println(e);
        for(Curso curso: cursos){
            System.out.println("  " + curso);
        }
        System.out.println("==================================");

        session.close();
    }

    @Test
    public void listarEstudiantes(){
        Session session = HibernateUtil.getSessionFactory().openSession();

        Query query = session.createQuery("from Estudiante");
        List<Estudiante> estudiantes = (List<Estudiante>) query.getResultList();
        for(Estudiante estudiante: estudiantes){
            System.out.println(estudiante);
        }

        session.close();
    }

    @Test
    public void listarEstudiantesEnUnCursoConHQL(){
        int cursoId = 2;

        Session session = HibernateUtil.getSessionFactory().openSession();


        Query<Estudiante> query = session.createQuery(
                "SELECT e FROM Estudiante e JOIN e.cursos c WHERE c.id = :cursoId", Estudiante.class);
        query.setParameter("cursoId", cursoId);


        List<Estudiante> estudiantes = query.getResultList();

        // Impresión de todos los estudiantes en el curso especificado
        System.out.println("Estudiantes en el curso ID " + cursoId + ":");
        for (Estudiante estudiante : estudiantes) {
            System.out.println(estudiante);
        }

        session.close();
    }

    @Test
    public void listarEstudiantesEnUnCursoSinHQL(){
        Long cursoId = 2L;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Curso curso = session.find(Curso.class, cursoId);
        Set<Estudiante> estudiantes = curso.getEstudiantes();

        // Impresión
        System.out.println("Estudiantes en el curso ID " + cursoId + ":");
        System.out.println(curso);
        for(Estudiante estudiante : estudiantes){
            System.out.println("  " + estudiante);
        }

        session.close();
    }

    @Test
    public void listarEstudiantesEnDosCursosPorId(){
        Session session = HibernateUtil.getSessionFactory().openSession();

        List<Long> cursoIds = new ArrayList<>();
        cursoIds.add(2L);
        cursoIds.add(3L);


        Query<Estudiante> query = session.createQuery(
                "SELECT DISTINCT e FROM Estudiante e JOIN e.cursos c WHERE c.id IN (:cursoIds)", Estudiante.class);
        query.setParameter("cursoIds", cursoIds);

        List<Estudiante> estudiantes = query.getResultList();

        System.out.println("Estudiantes en los cursos con ID 2 o 3:");
        for (Estudiante estudiante : estudiantes) {
            System.out.println(estudiante);
        }

        session.close();

    }

    @Test
    public void listarEstudiantesEnCursosPorNombre() {
        Session session = HibernateUtil.getSessionFactory().openSession();

        List<String> nombresCursos = new ArrayList<>();
        nombresCursos.add("Curso 2");
        nombresCursos.add("Curso 3");

        Query<Estudiante> query = session.createQuery(
                "SELECT DISTINCT e FROM Estudiante e JOIN e.cursos c WHERE c.nombre IN (:nombresCursos)", Estudiante.class);
        query.setParameter("nombresCursos", nombresCursos);

        List<Estudiante> estudiantes = query.getResultList();

        System.out.println("Estudiantes en los cursos 'Curso 2' o 'Curso 3':");
        for (Estudiante estudiante : estudiantes) {
            System.out.println(estudiante);
        }

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
