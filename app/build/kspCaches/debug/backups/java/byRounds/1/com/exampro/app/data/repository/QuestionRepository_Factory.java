package com.exampro.app.data.repository;

import com.exampro.app.data.db.dao.BookmarkDao;
import com.exampro.app.data.db.dao.QuestionDao;
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
public final class QuestionRepository_Factory implements Factory<QuestionRepository> {
  private final Provider<QuestionDao> questionDaoProvider;

  private final Provider<BookmarkDao> bookmarkDaoProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  public QuestionRepository_Factory(Provider<QuestionDao> questionDaoProvider,
      Provider<BookmarkDao> bookmarkDaoProvider, Provider<AuthRepository> authRepositoryProvider) {
    this.questionDaoProvider = questionDaoProvider;
    this.bookmarkDaoProvider = bookmarkDaoProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public QuestionRepository get() {
    return newInstance(questionDaoProvider.get(), bookmarkDaoProvider.get(), authRepositoryProvider.get());
  }

  public static QuestionRepository_Factory create(Provider<QuestionDao> questionDaoProvider,
      Provider<BookmarkDao> bookmarkDaoProvider, Provider<AuthRepository> authRepositoryProvider) {
    return new QuestionRepository_Factory(questionDaoProvider, bookmarkDaoProvider, authRepositoryProvider);
  }

  public static QuestionRepository newInstance(QuestionDao questionDao, BookmarkDao bookmarkDao,
      AuthRepository authRepository) {
    return new QuestionRepository(questionDao, bookmarkDao, authRepository);
  }
}
