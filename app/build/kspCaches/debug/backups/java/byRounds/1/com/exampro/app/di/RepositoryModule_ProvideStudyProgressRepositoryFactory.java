package com.exampro.app.di;

import com.exampro.app.data.db.dao.StudyProgressDao;
import com.exampro.app.data.repository.StudyProgressRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class RepositoryModule_ProvideStudyProgressRepositoryFactory implements Factory<StudyProgressRepository> {
  private final Provider<StudyProgressDao> studyProgressDaoProvider;

  public RepositoryModule_ProvideStudyProgressRepositoryFactory(
      Provider<StudyProgressDao> studyProgressDaoProvider) {
    this.studyProgressDaoProvider = studyProgressDaoProvider;
  }

  @Override
  public StudyProgressRepository get() {
    return provideStudyProgressRepository(studyProgressDaoProvider.get());
  }

  public static RepositoryModule_ProvideStudyProgressRepositoryFactory create(
      Provider<StudyProgressDao> studyProgressDaoProvider) {
    return new RepositoryModule_ProvideStudyProgressRepositoryFactory(studyProgressDaoProvider);
  }

  public static StudyProgressRepository provideStudyProgressRepository(
      StudyProgressDao studyProgressDao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideStudyProgressRepository(studyProgressDao));
  }
}
