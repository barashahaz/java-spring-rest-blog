package com.pluralsight.blog;

import com.pluralsight.blog.data.DatabaseLoader;
import com.pluralsight.blog.model.Post;
import com.pluralsight.blog.data.PostRepository;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
//@AutoConfigureMockMvc
@PrepareForTest(DatabaseLoader.class)
public class Module1_Tests {

    @Autowired
    private DatabaseLoader databaseLoader;

    @Autowired
    private PostRepository postRepository;

    private PostRepository spyRepository;

    @Before
    public void setup() {
        Constructor<DatabaseLoader> constructor = null;
        try {
            constructor = DatabaseLoader.class.getDeclaredConstructor(PostRepository.class);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        }

        spyRepository = Mockito.spy(postRepository);
        try {
            databaseLoader = constructor.newInstance(spyRepository); //new BlogController(spyRepository);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }


    @Test
    public void task_1() {
        Class c = PostRepository.class;
        Class[] interfaces = c.getInterfaces();

        assertEquals("Task 1: PostRepository should extend 1 interface - JpaRepository.",
                1, interfaces.length);

        assertEquals("Task 1: PostRepository should be an interface that extends JpaRepository<Post, Long>.",
                JpaRepository.class, interfaces[0]);
    }

    @Test
    public void task_2() {
        // Task 1 - Add field PostRepository postRepository; to DatabaseLoader
        Field[] fields = DatabaseLoader.class.getDeclaredFields();

        boolean postRepositoryExists = false;
        boolean annotationExists = false;
        for (Field field : fields) {
            if (field.getName().equals("postRepository") && field.getType().equals(PostRepository.class)) {
                postRepositoryExists = true;
            }
        }

        String message = "Task 2: A field called postRepository of type PostRepository does not exist in DatabaseLoader.";
        assertTrue(message, postRepositoryExists);

        // Check for DatabaseLoader constructor with PostRepository parameter
        Constructor<DatabaseLoader> constructor = null;
        try {
            constructor = DatabaseLoader.class.getDeclaredConstructor(PostRepository.class);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        }

        message = "Task 2: A DatabaseLoader constructor with a PostRepository parameter does not exist.";
        assertNotNull(message, constructor);

        Annotation[] annotations = {};
        // Check for @Autowired
        try {
            annotations =
                    DatabaseLoader.class.getDeclaredConstructor(PostRepository.class).getDeclaredAnnotations();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        assertTrue("There should be 1 annotation, @Autowired, on the DatabaseLoader class.", annotations.length == 1);

        assertEquals("The annotation on the DatabaseLoader constructor is not of type @Autowired.", Autowired.class,annotations[0].annotationType());
    }

    @Test
    public void task_3() {

        Mockito.when(spyRepository.saveAll(databaseLoader.randomPosts)).thenReturn(null);
        try {
            databaseLoader.run(new DefaultApplicationArguments(new String[]{}));
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean calledSaveAll = false;
        try {
            Mockito.verify(spyRepository).saveAll(databaseLoader.randomPosts);
            calledSaveAll = true;
        } catch (Error e) {
            //e.printStackTrace();
        }

        String message = "Task 3: Did not call PostRepository's `saveAll()` in DatabaseLoader's run() method.";
        assertTrue(message, calledSaveAll);
    }

    @Test
    public void task_4() {
        // Replace data-categories.sql file to add Categories
        // Open data-categories.sql file and check contents
        Path path = Paths.get("src/main/resources/application.properties");
        String result = "";
        try {
            final String output = "";
            List<String> allLines = Files.readAllLines(path);
            result = String.join("\n", allLines);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        assertTrue("Task 6: The `data.sql` file is not the same as `data-categories.sql`.", result.contains("spring.data.rest.default-page-size=8"));
    }

    @Test
    public void task_5() {
        Method method = null;
        try {
            method = PostRepository.class.getMethod("findByTitleContaining", String.class);
        } catch (Exception e) {
            ////e.printStackTrace();
        }

        assertNotNull("Task 5: The method findByTitleContaining() doesn't exist in the PostRepository class.", method );
    }

//    @Test
//    public void task_1() {
//        // Verify @Entity annotation
//        // TODO All tests will fail with errors if SpringBootTest is used and The Entity doesn't have an @Id
//        Annotation[] annotations =  Post.class.getDeclaredAnnotations();
//
//        assertTrue("There should be 1 annotation, @Entity, on the Post class.", annotations.length == 1);
//
//        assertEquals("The annotation on the Post class is not of type @Entity.", Entity.class,annotations[0].annotationType());
//
//        Annotation[] fieldAnnotations = null;
//
//        try {
//            Field field = Post.class.getDeclaredField("id");
//            fieldAnnotations = field.getDeclaredAnnotations();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        String message = "The field id should have two annotations @Id and @GeneratedValue(strategy = GenerationType.IDENTITY).";
//        assertTrue(message, fieldAnnotations.length == 2);
//
//        boolean hasIdAnnotation = false;
//        boolean hasGeneratedAnnotation = false;
//
//        for (Annotation annotation : fieldAnnotations) {
//            if (annotation.annotationType() == Id.class) hasIdAnnotation = true;
//            if (annotation.annotationType() == GeneratedValue.class) hasGeneratedAnnotation = true;
//            System.out.println("annotation = " + annotation);
//        }
//
//        assertTrue("The field id does not have the annotation @Id.", hasIdAnnotation);
//        assertTrue("The field id does not have the annotation @GeneratedValue(strategy = GenerationType.IDENTITY).", hasGeneratedAnnotation);
//    }
//
//    @Test
//    public void task_2() {
//        Annotation[] fieldAnnotations = null;
//
//        try {
//            Field field = Post.class.getDeclaredField("body");
//            fieldAnnotations = field.getDeclaredAnnotations();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        String message = "The field body should have 2 annotations @Column(length=1000000) and @Lob.";
//        assertTrue(message, fieldAnnotations.length == 2);
//
//        boolean hasColumnAnnotation = false;
//        boolean hasLobAnnotation = false;
//
//        for (Annotation annotation : fieldAnnotations) {
//            if (annotation.annotationType() == Column.class) {hasColumnAnnotation = true;}
//            if (annotation.annotationType() == Lob.class) hasLobAnnotation = true;
//            System.out.println("annotation = " + annotation);
//        }
//
//        assertTrue("The field body does not have the annotation @Column(length=1000000).", hasColumnAnnotation);
//        assertTrue("The field body does not have the annotation @Lob.", hasLobAnnotation);
//    }
//
//    @Test
//    public void task_3() {
//        Annotation[] fieldAnnotations = null;
//
//        try {
//            Field field = Post.class.getDeclaredField("date");
//            fieldAnnotations = field.getDeclaredAnnotations();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//
//        String message = "The field date should have 1 annotation @Temporal(TemporalType.DATE).";
//        assertTrue(message, fieldAnnotations.length == 1);
//
//        System.out.println("annotation = " + fieldAnnotations[0]);
//
//        message = "The field date does not have the annotation @Temporal(TemporalType.DATE).";
//        assertTrue(message, fieldAnnotations[0].annotationType() == Temporal.class);
//    }
//
//    @Test
//    public void task_4() {
//        Class c = PostRepository.class;
//        Class[] interfaces = c.getInterfaces();
//
//        assertEquals("PostRepository should extend 1 interface - JpaRepository.",
//                1, interfaces.length);
//
//        assertEquals("PostRepository should be an interface that extends JpaRepository<Post, Long>.",
//                JpaRepository.class, interfaces[0]);
//    }
//
//    @Test
//    public void task_5() {
//        List<Post> posts = postRepository.findAll();
//        assertNotNull("PostRepository's findAll() method returns null.", posts);
//
//        List<String> titles = new ArrayList<>(Arrays.asList("Earbuds",
//                "Smart Speakers",
//                "Device Charger",
//                "Smart Home Lock",
//                "Smart Instant Pot",
//                "Mobile Tripod",
//                "Travel Keyboard",
//                "SD Card Reader"));
//
//        assertEquals("There should be " + titles.size() + " Posts loaded from data-categories.sql.", titles.size(), posts.size());
//
//
//        boolean titlesMatch = true;
//        for (int i = 0; i<posts.size(); i++) {
//            if (!posts.get(i).getTitle().equals(titles.get(i))) {
//                titlesMatch = false;
//                break;
//            }
//        }
//
//        assertTrue("The titles loaded from data-categories.sql do not match the expected titles.", titlesMatch);
//    }

}