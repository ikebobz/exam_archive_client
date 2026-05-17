package com.exampro.app.data.services;

import com.exampro.app.data.api.DeviceApi;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MyFirebaseMessagingService_MembersInjector implements MembersInjector<MyFirebaseMessagingService> {
  private final Provider<DeviceApi> deviceApiProvider;

  public MyFirebaseMessagingService_MembersInjector(Provider<DeviceApi> deviceApiProvider) {
    this.deviceApiProvider = deviceApiProvider;
  }

  public static MembersInjector<MyFirebaseMessagingService> create(
      Provider<DeviceApi> deviceApiProvider) {
    return new MyFirebaseMessagingService_MembersInjector(deviceApiProvider);
  }

  @Override
  public void injectMembers(MyFirebaseMessagingService instance) {
    injectDeviceApi(instance, deviceApiProvider.get());
  }

  @InjectedFieldSignature("com.exampro.app.data.services.MyFirebaseMessagingService.deviceApi")
  public static void injectDeviceApi(MyFirebaseMessagingService instance, DeviceApi deviceApi) {
    instance.deviceApi = deviceApi;
  }
}
