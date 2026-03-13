package com.exampro.app.di

import android.content.SharedPreferences
import com.exampro.app.data.api.AuthApi
import com.exampro.app.data.api.ExamApi
import com.exampro.app.data.api.QuestionApi
import com.exampro.app.data.api.SubjectApi
import com.exampro.app.data.db.AppDatabase
import com.exampro.app.data.db.dao.ExamDao
import com.exampro.app.data.db.dao.QuestionDao
import com.exampro.app.data.db.dao.StudyProgressDao
import com.exampro.app.data.db.dao.SubjectDao
import com.exampro.app.data.repository.AuthRepository
import com.exampro.app.data.repository.ExamRepository
import com.exampro.app.data.repository.QuestionRepository
import com.exampro.app.data.repository.StudyProgressRepository
import com.exampro.app.data.repository.SubjectRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        @AuthPrefs prefs: SharedPreferences,
        database: AppDatabase
    ): AuthRepository {
        return AuthRepository(authApi, prefs, database)
    }

    @Provides
    @Singleton
    fun provideExamRepository(
        examApi: ExamApi,
        examDao: ExamDao
    ): ExamRepository {
        return ExamRepository(examApi, examDao)
    }

    @Provides
    @Singleton
    fun provideSubjectRepository(
        subjectApi: SubjectApi,
        subjectDao: SubjectDao
    ): SubjectRepository {
        return SubjectRepository(subjectApi, subjectDao)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(
        questionApi: QuestionApi,
        questionDao: QuestionDao
    ): QuestionRepository {
        return QuestionRepository(questionApi, questionDao)
    }

    @Provides
    @Singleton
    fun provideStudyProgressRepository(
        studyProgressDao: StudyProgressDao
    ): StudyProgressRepository {
        return StudyProgressRepository(studyProgressDao)
    }
}
