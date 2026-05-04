package com.exampro.app.di;

import com.exampro.app.data.db.AppDatabase;
import com.exampro.app.data.db.dao.ExamDao;
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
public final class DatabaseModule_ProvideExamDaoFactory implements Factory<ExamDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideExamDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ExamDao get() {
    return provideExamDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideExamDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideExamDaoFactory(databaseProvider);
  }

  public static ExamDao provideExamDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideExamDao(database));
  }
}
