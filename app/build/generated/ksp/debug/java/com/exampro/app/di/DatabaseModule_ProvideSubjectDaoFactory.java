package com.exampro.app.di;

import com.exampro.app.data.db.AppDatabase;
import com.exampro.app.data.db.dao.SubjectDao;
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
public final class DatabaseModule_ProvideSubjectDaoFactory implements Factory<SubjectDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideSubjectDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SubjectDao get() {
    return provideSubjectDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideSubjectDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideSubjectDaoFactory(databaseProvider);
  }

  public static SubjectDao provideSubjectDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSubjectDao(database));
  }
}
