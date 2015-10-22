package org.stepic.droid.store.operations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.stepic.droid.model.CachedVideo;
import org.stepic.droid.model.Course;
import org.stepic.droid.model.Section;
import org.stepic.droid.model.Video;
import org.stepic.droid.store.structure.DBStructureCourses;
import org.stepic.droid.store.structure.DbStructureCachedVideo;
import org.stepic.droid.store.structure.DbStructureSections;
import org.stepic.droid.util.DbParseHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager extends DbManagerBase {

    public enum Table {
        enrolled(DBStructureCourses.ENROLLED_COURSES),
        featured(DBStructureCourses.FEATURED_COURSES);

        private String description;

        Table(String description) {
            this.description = description;
        }

        private String getStoreName() {
            return description;
        }
    }

    public DatabaseManager(Context context) {
        super(context);
    }


    public List<Course> getAllCourses(DatabaseManager.Table type) {

        try {
            open();
            List<Course> courses = new ArrayList<>();
            Cursor cursor = getCourseCursor(type);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Course course = parseCourse(cursor);
                courses.add(course);
                cursor.moveToNext();
            }
            cursor.close();
            return courses;

        } finally {
            close();
        }
    }

    public void addCourse(Course course, DatabaseManager.Table type) {

        try {
            open();
            ContentValues values = new ContentValues();

            values.put(DBStructureCourses.Column.COURSE_ID, course.getCourseId());
            values.put(DBStructureCourses.Column.SUMMARY, course.getSummary());
            values.put(DBStructureCourses.Column.COVER_LINK, course.getCover());
            values.put(DBStructureCourses.Column.INTRO_LINK_VIMEO, course.getIntro());
            values.put(DBStructureCourses.Column.TITLE, course.getTitle());
            values.put(DBStructureCourses.Column.LANGUAGE, course.getLanguage());
            values.put(DBStructureCourses.Column.BEGIN_DATE_SOURCE, course.getBegin_date_source());
            values.put(DBStructureCourses.Column.LAST_DEADLINE, course.getLast_deadline());
            values.put(DBStructureCourses.Column.DESCRIPTION, course.getDescription());

            String instructorsParsed = DbParseHelper.parseLongArrayToString(course.getInstructors());
            values.put(DBStructureCourses.Column.INSTRUCTORS, instructorsParsed);

            values.put(DBStructureCourses.Column.REQUIREMENTS, course.getRequirements());
            values.put(DBStructureCourses.Column.ENROLLMENT, course.getEnrollment());

            String sectionsParsed = DbParseHelper.parseLongArrayToString(course.getSections());
            values.put(DBStructureCourses.Column.SECTIONS, sectionsParsed);

            database.insert(type.getStoreName(), null, values);

        } finally {
            close();
        }


    }

    public void deleteCourse(Course course, DatabaseManager.Table type) {
        try {
            open();
            long courseId = course.getCourseId();
            database.delete(type.getStoreName(),
                    DBStructureCourses.Column.COURSE_ID + " = " + courseId,
                    null);
        } finally {
            close();
        }
    }

    public boolean isCourseInDB(Course course, DatabaseManager.Table type) {
        try {
            open();
            String Query = "Select * from " + type.getStoreName() + " where " + DBStructureCourses.Column.COURSE_ID + " = " + course.getCourseId();
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;

        } finally {
            close();
        }
    }

    public void addSection(Section section) {
        try {
            open();
            ContentValues values = new ContentValues();

            values.put(DbStructureSections.Column.SECTION_ID, section.getId());
            values.put(DbStructureSections.Column.TITLE, section.getTitle());
            values.put(DbStructureSections.Column.SLUG, section.getSlug());
            values.put(DbStructureSections.Column.IS_ACTIVE, section.is_active());
            values.put(DbStructureSections.Column.BEGIN_DATE, section.getBegin_date());
            values.put(DbStructureSections.Column.SOFT_DEADLINE, section.getSoft_deadline());
            values.put(DbStructureSections.Column.HARD_DEADLINE, section.getHard_deadline());
            values.put(DbStructureSections.Column.COURSE, section.getCourse());
            values.put(DbStructureSections.Column.POSITION, section.getPosition());
            values.put(DbStructureSections.Column.UNITS, DbParseHelper.parseLongArrayToString(section.getUnits()));


            database.insert(DbStructureSections.SECTIONS, null, values);

        } finally {
            close();
        }
    }

    public void deleteSection(Section section) {
        try {
            open();
            long sectionId = section.getId();
            database.delete(DbStructureSections.SECTIONS,
                    DbStructureSections.Column.SECTION_ID + " = " + sectionId,
                    null);

        } finally {
            close();
        }
    }

    public List<Section> getAllSections() {
        try {
            open();
            List<Section> sections = new ArrayList<>();

            Cursor cursor = getSectionCursor();
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Section section = parseSection(cursor);
                sections.add(section);
                cursor.moveToNext();
            }

            cursor.close();
            return sections;
        } finally {
            close();
        }
    }

    public boolean isSectionInDb(Section section) {
        try {
            open();

            String Query = "Select * from " + DbStructureSections.SECTIONS + " where " + DbStructureSections.Column.SECTION_ID + " = " + section.getId();
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        } finally {
            close();
        }
    }

    public void addVideo(CachedVideo cachedVideo) {
        try {
            open();
            ContentValues values = new ContentValues();

            values.put(DbStructureCachedVideo.Column.VIDEO_ID, cachedVideo.getVideoId());
            values.put(DbStructureCachedVideo.Column.URL, cachedVideo.getUrl());

            database.insert(DbStructureCachedVideo.CACHED_VIDEO, null, values);
        } finally {
            close();
        }
    }

    public void deleteVideo(Video video) {
        try {
            open();
            long videoId = video.getId();
            database.delete(DbStructureCachedVideo.CACHED_VIDEO,
                    "\"" + DbStructureCachedVideo.Column.VIDEO_ID + "\"" + " = " + videoId,
                    null);
        } finally {
            close();
        }
    }

    public void deleteVideoByUrl(String path) {
        try {
            open();
            database.delete(DbStructureCachedVideo.CACHED_VIDEO,
                    DbStructureCachedVideo.Column.URL + " = " + "\"" + path + "\"",
                    null);
        } finally {
            close();
        }
    }


    public List<String> getPathsForAllCachedVideo() {
        try {
            open();
            List<String> cachedPaths = new ArrayList<>();

            Cursor cursor = getCachedVideosCursor();
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                CachedVideo cachedVideo = parseCachedVideo(cursor);
                cachedPaths.add(cachedVideo.getUrl());
                cursor.moveToNext();
            }

            cursor.close();
            return cachedPaths;
        } finally {
            close();
        }
    }

    /**
     * getPath of cached video
     *
     * @param video video which we check for contains in db
     * @return null if video not existing in db, otherwise path to disk
     */
    public String getPathIfExist(Video video) {
        try {
            open();
            String Query = "Select * from " + DbStructureCachedVideo.CACHED_VIDEO + " where " + DbStructureCachedVideo.Column.VIDEO_ID + " = " + video.getId();
            Cursor cursor = database.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return null;
            }
            cursor.moveToFirst();
            int columnNumberOfPath = 1;
            String path = cursor.getString(columnNumberOfPath);
            cursor.close();
            return path;

        } finally {
            close();
        }
    }

    public void clearCacheCourses(DatabaseManager.Table type) {
        List<Course> courses = getAllCourses(type);

        for (Course courseItem : courses) {
            deleteCourse(courseItem, type);
        }
    }


    private CachedVideo parseCachedVideo(Cursor cursor) {
        CachedVideo cachedVideo = new CachedVideo();
        int columnNumber = 0;
        cachedVideo.setVideoId(cursor.getLong(columnNumber++));
        cachedVideo.setUrl(cursor.getString(columnNumber++));
        return cachedVideo;
    }


    private boolean isVideoInDb(Video video) {
        String Query = "Select * from " + DbStructureCachedVideo.CACHED_VIDEO + " where " + DbStructureCachedVideo.Column.VIDEO_ID + " = " + video.getId();
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }


    private Cursor getCachedVideosCursor() {
        return database.query(DbStructureCachedVideo.CACHED_VIDEO, DbStructureCachedVideo.getUsedColumns(),
                null, null, null, null, null);
    }

    private Cursor getSectionCursor() {
        return database.query(DbStructureSections.SECTIONS, DbStructureSections.getUsedColumns(),
                null, null, null, null, null);
    }

    private Section parseSection(Cursor cursor) {
        Section section = new Section();
        //ignore id of table
        int columnNumber = 1;

        section.setId(cursor.getInt(columnNumber++));
        section.setTitle(cursor.getString(columnNumber++));
        section.setSlug(cursor.getString(columnNumber++));
        section.setIs_active(cursor.getInt(columnNumber++) > 0);
        section.setBegin_date(cursor.getString(columnNumber++));
        section.setSoft_deadline(cursor.getString(columnNumber++));
        section.setHard_deadline(cursor.getString(columnNumber++));

        return section;
    }


    private Course parseCourse(Cursor cursor) {
        Course course = new Course();
        //ignore id of table
        int columnNumber = 1;
        course.setId(cursor.getLong(columnNumber++));
        course.setSummary(cursor.getString(columnNumber++));
        course.setCover(cursor.getString(columnNumber++));
        course.setIntro(cursor.getString(columnNumber++));
        course.setTitle(cursor.getString(columnNumber++));
        course.setLanguage(cursor.getString(columnNumber++));
        course.setBegin_date_source(cursor.getString(columnNumber++));
        course.setLast_deadline(cursor.getString(columnNumber++));
        course.setDescription(cursor.getString(columnNumber++));
        course.setInstructors(DbParseHelper.parseStringToLongArray(cursor.getString(columnNumber++)));
        course.setRequirements(cursor.getString(columnNumber++));
        course.setEnrollment(cursor.getInt(columnNumber++));
        course.setSections(DbParseHelper.parseStringToLongArray(cursor.getString(columnNumber++)));

        return course;
    }


    private Cursor getCourseCursor(DatabaseManager.Table type) {
        return database.query(type.getStoreName(), DBStructureCourses.getUsedColumns(),
                null, null, null, null, null);
    }
}
