package se.verran.springbootdemowithtests.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import se.verran.springbootdemowithtests.entities.Student;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;



    @BeforeEach
    void setUp() {
    }

    @Test
    void testExistsStudentByEmailWithMatchingEmail() {
        Student student = new Student("Alfons", "Lindberg", LocalDate.now(), "alfons@mail.com");
        studentRepository.save(student);

        assertTrue(studentRepository.existsStudentByEmail("alfons@mail.com"));
    }

    @Test
    void testExitsStudentByEmailWithNoMatchingEmail() {
        Student student = new Student("Alfons", "Lindberg", LocalDate.now(), "alfons@mail.com");
        studentRepository.save(student);

        assertFalse(studentRepository.existsStudentByEmail("notAlfons@gmail.com"));
    }
}