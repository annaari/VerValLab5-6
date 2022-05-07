package service;

import domain.Grade;
import domain.Homework;
import domain.Student;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.GradeValidator;
import validation.HomeworkValidator;
import validation.StudentValidator;
import validation.Validator;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceTest {
    private static Service service;

    @BeforeAll
    public static void setUp() throws Exception {
        Validator<Student> studentValidator = new StudentValidator();
        Validator<Homework> homeworkValidator = new HomeworkValidator();
        Validator<Grade> gradeValidator = new GradeValidator();

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(studentValidator, "students.xml");
        HomeworkXMLRepository fileRepository2 = new HomeworkXMLRepository(homeworkValidator, "homework.xml");
        GradeXMLRepository fileRepository3 = new GradeXMLRepository(gradeValidator, "grades.xml");

        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    @Test
    public void listingStudentsShouldDisplayStudents() {
        Collection<Student> students = (Collection<Student>) service.findAllStudents();

        Assertions.assertNotNull(students);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "5", "12", "1"})
    public void deleteingNonExistentHomeworkShouldCauseError(String id) {
        Assertions.assertEquals(0, service.deleteHomework(id));
    }

    @ParameterizedTest
    @CsvSource({"10, 1"})
    public void extendingDeadlineShouldAddWeeksToDeadline(String id, int noWeeks) {
        Collection<Homework> homeworks = (Collection<Homework>) service.findAllHomework();
        AtomicInteger oldDeadline = new AtomicInteger();
        homeworks.forEach(homework -> {
            if (homework.getID().equals(id)){
                oldDeadline.set(homework.getDeadline());
            }
        });

        service.extendDeadline(id, noWeeks);
        homeworks = (Collection<Homework>) service.findAllHomework();
        AtomicInteger newDeadline = new AtomicInteger();
        homeworks.forEach(homework -> {
            if (homework.getID().equals(id)){
                newDeadline.set(homework.getDeadline());
                service.extendDeadline(id, noWeeks);
            }
        });
        Assertions.assertTrue(oldDeadline.intValue() < newDeadline.intValue());

    }

    @ParameterizedTest
    @CsvSource({"3, Arianna, 531", "3, Eszti, 340"})
    public void addingStudentWithSameIdShouldCauseError(String id, String name, int group) {
        Assertions.assertEquals(0, service.saveStudent(id, name, group));
    }

    @Test
    public void listingGradesShouldDisplayGrades() {
        Assertions.assertNotNull(service.findAllGrades());
    }


    @ParameterizedTest
    @CsvSource({"5, le, 6, 4"})
    public void saveingHomeworkWithValidData(String id, String description, int deadline, int startline) {
        Assertions.assertEquals(1, service.saveHomework(id, description, deadline, startline));

    }

}