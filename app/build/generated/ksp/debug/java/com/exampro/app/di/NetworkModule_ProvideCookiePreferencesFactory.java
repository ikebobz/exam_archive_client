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
    "com.exampro.app.di.CookiePrefs",
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
public final class NetworkModule_ProvideCookiePreferencesFactory implements Factory<SharedPreferences> {
  private final Provider<Context> contextProvider;

  public NetworkModule_ProvideCookiePreferencesFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SharedPreferences get() {
    return provideCookiePreferences(contextProvider.get());
  }

  public static NetworkModule_ProvideCookiePreferencesFactory create(
      Provider<Context> contextProvider) {
    return new NetworkModule_ProvideCookiePreferencesFactory(contextProvider);
  }

  public static SharedPreferences provideCookiePreferences(Context context) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideCookiePreferences(context));
  }
}
