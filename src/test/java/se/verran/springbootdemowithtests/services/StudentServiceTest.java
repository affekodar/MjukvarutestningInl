package se.verran.springbootdemowithtests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.verran.springbootdemowithtests.entities.Student;
import se.verran.springbootdemowithtests.repositories.StudentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    StudentService studentService;
    StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository = mock(StudentRepository.class);
        studentService = new StudentService(studentRepository);
    }

    @Test
    void testsAddStudentWhenEmailDoNotExist() {
        //Arrange
        Student student = new Student();
        student.setEmail("alfonsito@mail.com");
        when(studentRepository.existsStudentByEmail(student.getEmail())).thenReturn(false);
        when(studentRepository.save(student)).thenReturn(student);

        //Act
        Student result = studentService.addStudent(student);

        //Assert
        assertEquals(student, result);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void testsAddStudentWhenEmailAlreadyExistsAndThrowsException() {
        //Arrange
        Student student = new Student();
        student.setEmail("alfonsinho@mail.com");
        when(studentRepository.existsStudentByEmail(student.getEmail())).thenReturn(true);

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> studentService.addStudent(student));

        //Assert
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Email " + student.getEmail() + " already exists", exception.getReason());
    }
    @Test
    void testsGetAllStudentsReturnsExpectedSize() {
        //Arrange
        Student student1 = new Student("Alfons", "Lindberg", LocalDate.now(), "alfie@mail.com");
        Student student2 = new Student("Anton", "Lindeberg", LocalDate.now(), "antonito@mail.com");
        Student student3 = new Student("Arvid", "Lindbergh", LocalDate.now(), "arvido@mail.com");

        List<Student> students = new ArrayList<>();

        students.add(student1);
        students.add(student2);
        students.add(student3);

        when(studentRepository.findAll()).thenReturn(students);

        //Act
        List<Student> result = studentService.getAllStudents();

        //Assert
        assertEquals(students.size(), result.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void testDeleteStudentWhenStudentDoNotExistsException() {
        //Arrange
        int studentId = 1;
        when(studentRepository.existsById(studentId)).thenReturn(false);

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> studentService.deleteStudent(studentId));

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Could not find and delete student by id " + studentId, exception.getReason());
        verify(studentRepository, times(1)).existsById(studentId);
    }

    @Test
    void testDeleteStudentWhenStudentExistsSuccessful() {
        //Arrange
        int studentId = 1;
        when(studentRepository.existsById(studentId)).thenReturn(true);

        //Act
        studentService.deleteStudent(studentId);

        //Assert
        verify(studentRepository, times(1)).existsById(studentId);
        verify(studentRepository, times(1)).deleteById(studentId);
    }
    @Test
    void testUpdateStudentWhenStudentDoNotExistException() {
        //Arrange
        Student student = new Student();
        student.setId(1);
        when(studentRepository.existsById(student.getId())).thenReturn(false);

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> studentService.updateStudent(student));

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Could not find and update student by id " + student.getId(), exception.getReason());
        verify(studentRepository, times(1)).existsById(student.getId());
    }

    @Test
    void testUpdateStudentWhenStudentExistsSuccessful() {
        //Arrange
        Student student = new Student();
        student.setId(1);
        when(studentRepository.existsById(student.getId())).thenReturn(true);
        when(studentRepository.save(student)).thenReturn(student);

        //Act
        Student result = studentService.updateStudent(student);

        //Assert
        assertEquals(student, result);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void testGetStudentById() {
        //Arrange
        int studentId = 1;
        Student student = new Student();
        student.setId(studentId);
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        //Act
        Student result = studentService.getStudentById(studentId);

        //Assert
        assertEquals(student, result);
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testSetGradeForStudentByIdInvalidGradeException() {
        //Arrange
        int studentId = 1;
        String gradeAsString = "InvalidGrade";

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> studentService.setGradeForStudentById(studentId, gradeAsString));

        //Assert
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatusCode());
        assertEquals("Valid grades are 0.0 - 5.0", exception.getReason());
    }

    @Test
    void testSetGradeForStudentByIdGradeOutsideRangeException() {
        //Arrange
        int studentId = 1;
        String gradeAsString = "6.0";

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> studentService.setGradeForStudentById(studentId, gradeAsString));

        //Assert
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatusCode());
        assertEquals("Valid grades are 0.0 - 5.0", exception.getReason());
    }

    @Test
    void testSetGradeForStudentThatDoNotExistException() {
        //Arrange
        int studentId = 1;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> studentService.setGradeForStudentById(studentId, "3.0"));

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Could not find and update grades for student by id " + studentId, exception.getReason());
        verify(studentRepository, times(1)).findById(studentId);
    }

    @Test
    void testSetGradeForExistingStudentSuccessful() {
        //Arrange
        Student student = new Student();
        student.setId(1);
        when(studentRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);

        //Act
        Student result = studentService.setGradeForStudentById(student.getId(), "3.0");

        //Assert
        assertEquals(3.0, result.getJavaProgrammingGrade());
        verify(studentRepository, times(1)).save(student);
        verify(studentRepository, times(1)).findById(student.getId());
    }
}