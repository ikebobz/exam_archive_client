package com.exampro.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.exampro.app.data.db.dao.ExamDao
import com.exampro.app.data.db.dao.QuestionDao
import com.exampro.app.data.db.dao.StudyProgressDao
import com.exampro.app.data.db.dao.SubjectDao
import com.exampro.app.data.db.entities.AnswerEntity
import com.exampro.app.data.db.entities.ExamEntity
import com.exampro.app.data.db.entities.QuestionEntity
import com.exampro.app.data.db.entities.StudyProgressEntity
import com.exampro.app.data.db.entities.SubjectEntity

@Database(
    entities = [
        ExamEntity::class,
        SubjectEntity::class,
        QuestionEntity::class,
        AnswerEntity::class,
        StudyProgressEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun examDao(): ExamDao
    abstract fun subjectDao(): SubjectDao
    abstract fun questionDao(): QuestionDao
    abstract fun studyProgressDao(): StudyProgressDao
}
