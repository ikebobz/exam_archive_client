package com.exampro.app.di;

import com.exampro.app.data.db.dao.BookmarkDao;
import com.exampro.app.data.db.dao.QuestionDao;
import com.exampro.app.data.repository.AuthRepository;
import com.exampro.app.data.repository.QuestionRepository;
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
public final class RepositoryModule_ProvideQuestionRepositoryFactory implements Factory<QuestionRepository> {
  private final Provider<QuestionDao> questionDaoProvider;

  private final Provider<BookmarkDao> bookmarkDaoProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  public RepositoryModule_ProvideQuestionRepositoryFactory(
      Provider<QuestionDao> questionDaoProvider, Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    this.questionDaoProvider = questionDaoProvider;
    this.bookmarkDaoProvider = bookmarkDaoProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public QuestionRepository get() {
    return provideQuestionRepository(questionDaoProvider.get(), bookmarkDaoProvider.get(), authRepositoryProvider.get());
  }

  public static RepositoryModule_ProvideQuestionRepositoryFactory create(
      Provider<QuestionDao> questionDaoProvider, Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    return new RepositoryModule_ProvideQuestionRepositoryFactory(questionDaoProvider, bookmarkDaoProvider, authRepositoryProvider);
  }

  public static QuestionRepository provideQuestionRepository(QuestionDao questionDao,
      BookmarkDao bookmarkDao, AuthRepository authRepository) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideQuestionRepository(questionDao, bookmarkDao, authRepository));
  }
}
