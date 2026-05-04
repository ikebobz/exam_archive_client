package com.exampro.app.data.repository;

import com.exampro.app.data.db.dao.StudyProgressDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class StudyProgressRepository_Factory implements Factory<StudyProgressRepository> {
  private final Provider<StudyProgressDao> studyProgressDaoProvider;

  public StudyProgressRepository_Factory(Provider<StudyProgressDao> studyProgressDaoProvider) {
    this.studyProgressDaoProvider = studyProgressDaoProvider;
  }

  @Override
  public StudyProgressRepository get() {
    return newInstance(studyProgressDaoProvider.get());
  }

  public static StudyProgressRepository_Factory create(
      Provider<StudyProgressDao> studyProgressDaoProvider) {
    return new StudyProgressRepository_Factory(studyProgressDaoProvider);
  }

  public static StudyProgressRepository newInstance(StudyProgressDao studyProgressDao) {
    return new StudyProgressRepository(studyProgressDao);
  }
}
