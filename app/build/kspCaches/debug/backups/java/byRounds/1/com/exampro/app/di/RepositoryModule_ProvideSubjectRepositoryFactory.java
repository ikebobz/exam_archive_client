package com.exampro.app.di;

import com.exampro.app.data.db.dao.SubjectDao;
import com.exampro.app.data.repository.SubjectRepository;
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
public final class RepositoryModule_ProvideSubjectRepositoryFactory implements Factory<SubjectRepository> {
  private final Provider<SubjectDao> subjectDaoProvider;

  public RepositoryModule_ProvideSubjectRepositoryFactory(Provider<SubjectDao> subjectDaoProvider) {
    this.subjectDaoProvider = subjectDaoProvider;
  }

  @Override
  public SubjectRepository get() {
    return provideSubjectRepository(subjectDaoProvider.get());
  }

  public static RepositoryModule_ProvideSubjectRepositoryFactory create(
      Provider<SubjectDao> subjectDaoProvider) {
    return new RepositoryModule_ProvideSubjectRepositoryFactory(subjectDaoProvider);
  }

  public static SubjectRepository provideSubjectRepository(SubjectDao subjectDao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideSubjectRepository(subjectDao));
  }
}
