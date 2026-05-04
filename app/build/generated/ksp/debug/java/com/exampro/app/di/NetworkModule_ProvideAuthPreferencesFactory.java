package com.exampro.app.di;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "com.exampro.app.di.AuthPrefs",
    "dagger.hilt.android.qualifiers.ApplicationContext"
})
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
public final class NetworkModule_ProvideAuthPreferencesFactory implements Factory<SharedPreferences> {
  private final Provider<Context> contextProvider;

  public NetworkModule_ProvideAuthPreferencesFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SharedPreferences get() {
    return provideAuthPreferences(contextProvider.get());
  }

  public static NetworkModule_ProvideAuthPreferencesFactory create(
      Provider<Context> contextProvider) {
    return new NetworkModule_ProvideAuthPreferencesFactory(contextProvider);
  }

  public static SharedPreferences provideAuthPreferences(Context context) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAuthPreferences(context));
  }
}
