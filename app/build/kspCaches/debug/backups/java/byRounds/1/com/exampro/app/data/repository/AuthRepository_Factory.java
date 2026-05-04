package com.exampro.app.data.repository;

import android.content.SharedPreferences;
import com.exampro.app.data.api.AuthApi;
import com.exampro.app.data.db.AppDatabase;
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
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<AuthApi> authApiProvider;

  private final Provider<SharedPreferences> sharedPreferencesProvider;

  private final Provider<AppDatabase> databaseProvider;

  public AuthRepository_Factory(Provider<AuthApi> authApiProvider,
      Provider<SharedPreferences> sharedPreferencesProvider,
      Provider<AppDatabase> databaseProvider) {
    this.authApiProvider = authApiProvider;
    this.sharedPreferencesProvider = sharedPreferencesProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(authApiProvider.get(), sharedPreferencesProvider.get(), databaseProvider.get());
  }

  public static AuthRepository_Factory create(Provider<AuthApi> authApiProvider,
      Provider<SharedPreferences> sharedPreferencesProvider,
      Provider<AppDatabase> databaseProvider) {
    return new AuthRepository_Factory(authApiProvider, sharedPreferencesProvider, databaseProvider);
  }

  public static AuthRepository newInstance(AuthApi authApi, SharedPreferences sharedPreferences,
      AppDatabase database) {
    return new AuthRepository(authApi, sharedPreferences, database);
  }
}
