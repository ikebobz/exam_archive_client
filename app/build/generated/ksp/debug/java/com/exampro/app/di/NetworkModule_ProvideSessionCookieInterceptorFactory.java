package com.exampro.app.di;

import android.content.SharedPreferences;
import com.exampro.app.data.api.SessionCookieInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.exampro.app.di.CookiePrefs")
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
public final class NetworkModule_ProvideSessionCookieInterceptorFactory implements Factory<SessionCookieInterceptor> {
  private final Provider<SharedPreferences> prefsProvider;

  public NetworkModule_ProvideSessionCookieInterceptorFactory(
      Provider<SharedPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public SessionCookieInterceptor get() {
    return provideSessionCookieInterceptor(prefsProvider.get());
  }

  public static NetworkModule_ProvideSessionCookieInterceptorFactory create(
      Provider<SharedPreferences> prefsProvider) {
    return new NetworkModule_ProvideSessionCookieInterceptorFactory(prefsProvider);
  }

  public static SessionCookieInterceptor provideSessionCookieInterceptor(SharedPreferences prefs) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideSessionCookieInterceptor(prefs));
  }
}
