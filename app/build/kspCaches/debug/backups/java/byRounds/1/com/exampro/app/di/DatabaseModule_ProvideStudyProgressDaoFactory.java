package com.exampro.app.di;

import com.exampro.app.data.db.AppDatabase;
import com.exampro.app.data.db.dao.StudyProgressDao;
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
public final class DatabaseModule_ProvideStudyProgressDaoFactory implements Factory<StudyProgressDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideStudyProgressDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public StudyProgressDao get() {
    return provideStudyProgressDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideStudyProgressDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideStudyProgressDaoFactory(databaseProvider);
  }

  public static StudyProgressDao provideStudyProgressDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideStudyProgressDao(database));
  }
}
