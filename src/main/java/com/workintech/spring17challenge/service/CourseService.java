package com.workintech.spring17challenge.service;

import com.workintech.spring17challenge.exceptions.CourseNotFoundException;
import com.workintech.spring17challenge.exceptions.CourseValidationException;
import com.workintech.spring17challenge.model.Course;
import com.workintech.spring17challenge.model.HighCourseGpa;
import com.workintech.spring17challenge.model.LowCourseGpa;
import com.workintech.spring17challenge.model.MediumCourseGpa;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class CourseService {

    private final List<Course> courses = new ArrayList<>();
    private final LowCourseGpa lowCourseGpa;
    private final MediumCourseGpa mediumCourseGpa;
    private final HighCourseGpa highCourseGpa;

    public CourseService(LowCourseGpa lowCourseGpa, MediumCourseGpa mediumCourseGpa, HighCourseGpa highCourseGpa) {
        this.lowCourseGpa = lowCourseGpa;
        this.mediumCourseGpa = mediumCourseGpa;
        this.highCourseGpa = highCourseGpa;
    }

    public List<Course> getAll() {
        return courses;
    }

    public Course getByName(String name) {
        return courses.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new CourseNotFoundException("Course not found: " + name));
    }

    public Course getById(Long id) {
        return courses.stream()
                .filter(c -> c.getId() != null && c.getId().equals(id.intValue()))
                .findFirst()
                .orElseThrow(() -> new CourseNotFoundException("Course not found id: " + id));
    }

    public int calculateTotalGpa(Course c) {
        int credit = c.getCredit();
        int coef = c.getGrade().getCoefficient();

        if (credit <= 2) {
            return coef * credit * lowCourseGpa.getGpa();
        } else if (credit == 3) {
            return coef * credit * mediumCourseGpa.getGpa();
        } else {

            return coef * credit * highCourseGpa.getGpa();
        }
    }

    private void validateCourseForAdd(Course course) {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new CourseValidationException("Course name cannot be empty");
        }
        if (course.getCredit() == null || course.getCredit() < 0 || course.getCredit() > 4) {
            throw new CourseValidationException("Credit must be between 0 and 4");
        }
        boolean exists = courses.stream()
                .anyMatch(c -> c.getName() != null && c.getName().equalsIgnoreCase(course.getName()));
        if (exists) {
            throw new CourseValidationException("Course with same name already exists: " + course.getName());
        }
        if (course.getGrade() == null) {
            throw new CourseValidationException("Grade cannot be null");
        }
    }

    private void validateCourseForUpdate(Course course) {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new CourseValidationException("Course name cannot be empty");
        }
        if (course.getCredit() == null || course.getCredit() < 0 || course.getCredit() > 4) {
            throw new CourseValidationException("Credit must be between 0 and 4");
        }
        if (course.getGrade() == null) {
            throw new CourseValidationException("Grade cannot be null");
        }
    }

    public Course addCourse(Course course) {
        courses.clear();
        validateCourseForAdd(course);
        course.setId((int) (courses.size() + 1));
        courses.add(course);
        return course;
    }

    public Course updateCourse(Long id, Course newCourse) {
        Course existing = getById(id);
        validateCourseForUpdate(newCourse);
        existing.setName(newCourse.getName());
        existing.setCredit(newCourse.getCredit());
        existing.setGrade(newCourse.getGrade());
        return existing;
    }

    public void deleteCourse(Integer id) {
        boolean removed = courses.removeIf(c -> c.getId() != null && c.getId().equals(id));
        if (!removed) {
            throw new CourseNotFoundException("Course not found id: " + id);
        }
    }

    public void clearAll() {
        courses.clear();
    }
}