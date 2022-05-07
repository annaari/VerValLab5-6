package service;

import domain.Grade;
import domain.Homework;
import domain.Pair;
import domain.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ServiceMockTest {
    private static Service service;

    @Mock
    StudentXMLRepository studentRepository;
    @Mock
    HomeworkXMLRepository homeworkRepository;
    @Mock
    GradeXMLRepository gradeRepository;


    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        service = new Service(studentRepository, homeworkRepository, gradeRepository);
    }

    @ParameterizedTest
    @CsvSource({"3, Arianna, 531", "3, Eszti, 340"})
    public void addingStudentWithSameIdShouldCauseError(String id, String name, int group) {
        Student student = new Student(id, name, group);
        when(studentRepository.save(student)).thenReturn(student);
        Assertions.assertEquals(0, service.saveStudent(id, name, group));
    }


    @Test
    public void listingGradesShouldDisplayGrades() {
        Collection<Grade> grades = new ArrayList<Grade>();
        grades.add(new Grade(new Pair<String, String>("3", "5"), 5, 10, "OK"));
        when(gradeRepository.findAll()).thenReturn(grades);
        Assertions.assertNotNull(service.findAllGrades());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "5", "12", "1"})
    public void deleteingNonExistentHomeworkShouldCauseError(String id) {
        homeworkRepository.delete(id);
        Mockito.verify(homeworkRepository).delete(id);
        Assertions.assertEquals(0, service.deleteHomework(id));
    }

    @Test
    public void saveingHomeworkWithValidData() {
        String id = "5";
        String description = "file";
        int deadline = 6;
        int startline = 4;
        when(homeworkRepository.save(any(Homework.class))).thenReturn(null);

        Assertions.assertEquals(1, service.saveHomework(id, description, deadline, startline));

    }
}
