package com.exampro.app.di;

import android.content.SharedPreferences;
import com.exampro.app.data.api.AuthApi;
import com.exampro.app.data.db.AppDatabase;
import com.exampro.app.data.repository.AuthRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.exampro.app.di.AuthPrefs")
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
public final class RepositoryModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final Provider<AuthApi> authApiProvider;

  private final Provider<SharedPreferences> prefsProvider;

  private final Provider<AppDatabase> databaseProvider;

  public RepositoryModule_ProvideAuthRepositoryFactory(Provider<AuthApi> authApiProvider,
      Provider<SharedPreferences> prefsProvider, Provider<AppDatabase> databaseProvider) {
    this.authApiProvider = authApiProvider;
    this.prefsProvider = prefsProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(authApiProvider.get(), prefsProvider.get(), databaseProvider.get());
  }

  public static RepositoryModule_ProvideAuthRepositoryFactory create(
      Provider<AuthApi> authApiProvider, Provider<SharedPreferences> prefsProvider,
      Provider<AppDatabase> databaseProvider) {
    return new RepositoryModule_ProvideAuthRepositoryFactory(authApiProvider, prefsProvider, databaseProvider);
  }

  public static AuthRepository provideAuthRepository(AuthApi authApi, SharedPreferences prefs,
      AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideAuthRepository(authApi, prefs, database));
  }
}
