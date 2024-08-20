package se.verran.springbootdemowithtests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.verran.springbootdemowithtests.entities.Student;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SchoolServiceTest {

    SchoolService schoolService;
    StudentService studentService;


    @BeforeEach
    void setUp() {
        studentService = mock(StudentService.class);
        schoolService = new SchoolService(studentService);
    }

    private List<Student> generateStudentList(int numberOfStudents) {
        List<Student> students = new ArrayList<>();

        for (int i = 0; i < numberOfStudents; i++) {
            Student student = new Student();
            students.add(student);
        }
        return students;
    }

    //Tests For numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups()
    @Test
    void testNumberOfGroupsTooLow() {
        //Arrange
        int numberOfGroups = 1;

        //Act
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        //Assert
        assertEquals(result, "There should be at least two groups");
    }

    @Test
    void testNumberOfStudentsSmallerThanNumberOfGroups() {
        //Arrange
        int numberOfGroups = 4;
        List<Student> students = generateStudentList(3);
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        //Assert
        assertEquals(result, String.format("Not able to divide %s students into %s groups", students.size(), numberOfGroups));
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testStudentsPerGroupTooLow() {
        //Arrange
        int numberOfGroups = 2;
        List<Student> students = generateStudentList(2);
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        //Assert
        assertEquals("Not able to manage " + numberOfGroups + " groups with " + students.size() + " students", result);
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testRemainderTwoGroupsFiveStudents() {
        //Arrange
        int numberOfGroups = 2;
        List<Student> students = generateStudentList(5);
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        //Assert
        assertEquals("2 groups could be formed with 2 students per group, but that would leave 1 student hanging", result);
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testDivisionWithTwoGroupsFourStudents() {
        //Arrange
        int numberOfGroups = 2;
        List<Student> students = generateStudentList(4);
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        String result = schoolService.numberOfStudentsPerGroupWhenDivideIntoNumberOfGroups(numberOfGroups);

        //Assert
        assertEquals("2 groups could be formed with 2 students per group", result);
        verify(studentService, times(1)).getAllStudents();
    }

    //Tests for numberOfGroupsWhenDividedIntoGroupsOf()
    @Test
    void testStudentsPerGroupSmallerThanRequested() {
        //Arrange
        int studentsPerGroup = 1;

        //Act
        String result = schoolService.numberOfGroupsWhenDividedIntoGroupsOf(studentsPerGroup);

        //Assert
        assertEquals("Size of group should be at least 2", result);
    }

    @Test
    void testNumberOfStudentsSmallerThanStudentsPerGroup() {
        //Arrange
        int studentsPerGroup = 4;
        List<Student> students = generateStudentList(3);
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        String result = schoolService.numberOfGroupsWhenDividedIntoGroupsOf(studentsPerGroup);

        //Assert
        assertEquals("Not able to manage groups of 4 with only 3 students", result);
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testNumberOfStudentsDividedByStudentsPerGroupSmallerThanTwo() {
        //Arrange
        int studentsPerGroup = 2;
        List<Student> students = generateStudentList(3);
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        String result = schoolService.numberOfGroupsWhenDividedIntoGroupsOf(studentsPerGroup);

        //Assert
        assertEquals("Not able to manage groups of 2 with only 3 students", result);
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testDivisionWithFourStudentsIntoTwoGroupsNoRemainder() {
        //Arrange
        int studentsPerGroup = 2;
        List<Student> students = generateStudentList(4);
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        String result = schoolService.numberOfGroupsWhenDividedIntoGroupsOf(studentsPerGroup);

        //Assert
        assertEquals("2 students per group is possible, there will be 2 groups", result);
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testDivisionWithFiveStudentsIntoTwoGroupsOneRemainder() {
        //Arrange
        int studentsPerGroup = 2;
        List<Student> students = generateStudentList(5);
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        String result = schoolService.numberOfGroupsWhenDividedIntoGroupsOf(studentsPerGroup);

        //Assert
        assertEquals("2 students per group is possible, there will be 2 groups, there will be 1 student hanging", result);
        verify(studentService, times(1)).getAllStudents();
    }


    //Tests for calculateAverageGrade()
    @Test
    void testForStudentListEmptyException() {
        //Arrange
        List<Student> students = new ArrayList<>();
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> schoolService.calculateAverageGrade());

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No students found", exception.getReason());
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testCalculateAvgGradeSuccessful() {
        //Arrange
        List<Student> students = generateStudentList(5);
        double grade = -1.0;
        for (Student student : students) {
            grade++;
            student.setJavaProgrammingGrade(grade);
        }
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        String result = schoolService.calculateAverageGrade();

        //Assert
        assertEquals("Average grade is 2,0", result);
        verify(studentService, times(1)).getAllStudents();
    }


    //Test for getTopScoringStudents()
    @Test
    void testTopScoringStudentsEmptyStudentsException() {
        //Arrange
        List<Student> students = new ArrayList<>();
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> schoolService.getTopScoringStudents());

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No students found", exception.getReason());
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testTopScoringStudentsFiveStudentsReturnsOne() {
        //Arrange
        List<Student> students = generateStudentList(5);
        for (Student student : students) {
            double grade = -1.0;
            student.setJavaProgrammingGrade(grade + 1.0);
        }
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        int result = schoolService.getTopScoringStudents().size();

        //Assert
        assertEquals(1, result);
        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testTopScoringStudentsTenStudentsReturnsTwo() {
        //Arrange
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Student student = new Student();
            student.setJavaProgrammingGrade((double) i);
            students.add(student);
        }
        when(studentService.getAllStudents()).thenReturn(students);

        //Act
        int result = schoolService.getTopScoringStudents().size();

        //Assert
        assertEquals(2, result);
        verify(studentService, times(1)).getAllStudents();
    }

}