# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes *Annotation*
-keepclassmembers class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
   }


-keepattributes SourceFile,LineNumberTable

#- for GCm
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.firebase.messaging.*

#-for support lib
-dontwarn android.support.**

-dontwarn org.apache.**
-keep class in.org.npci.** {*;}
-keep class org.npci.upi.** {*;}

-keep interface android.support.v4.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.** { *; }

####################################################################  REMOVE WARNINGS


-dontwarn android.support.design.internal.**
-dontwarn com.google.android.gms.**
-dontwarn android.support.v4.**


# Enable proguard with Google libs
-keep class com.google.** { *;}
-dontwarn com.google.common.**
-dontwarn com.google.ads.**
######################################################################
#support-v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-dontwarn org.**
-dontwarn javax.xml.**


##################################################################
-dontwarn org.androidannotations.api.rest.**

#######################################################################
#support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }


#################################################
#################################################
#CrashAnalytics

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-dontwarn android.test.**
-dontwarn okio.**


#################################################
#################################################
# Retrofit
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8

################################################
################################################
#Applozic

-dontwarn org.apache.commons.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

-keepclassmembernames class * extends com.applozic.mobicommons.json.JsonMarker {
	!static !transient <fields>;
}
-keepclassmembernames class * extends com.applozic.mobicommons.json.JsonParcelableMarker {
	!static !transient <fields>;
}
#google-play-serivces_lib
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

#GSON Config
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger { *; }
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

# Keep SafeParcelable value, needed for reflection. This is required to support backwards
# compatibility of some classes.
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-keep class com.google.gson.** { *; }

-keep public class * extends View {
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
public void set*(...);
}
