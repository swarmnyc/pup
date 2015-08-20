# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html


-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose


# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontobfuscate
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.


-keep class com.swarmnyc.pup.** { *; }
-keep class org.apache.http.** { *; }
-dontwarn org.apache.**
-dontwarn android.net.http.AndroidHttpClient
-keepattributes *Annotation*

-libraryjars  <java.home>/lib/rt.jar

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keepclassmembers class android.support.design.widget.FloatingActionButton$Behavior { public <init>(); }

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**


# ********************************************
# *** basic 'conservative' proguard settings
# ********************************************
# from http://omgitsmgp.com/2013/09/09/a-conservative-guide-to-proguard-for-android/
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.billing.IInAppBillingService
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}

#Parse
-keep class com.parse.** { *; }
-dontwarn com.parse.**
#-libraryjars libs/Parse-1.5.1.jar
-keep class com.facebook.** { *; }



# *****************************************
# *** recommended exclusions for Guava
# *****************************************
# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization/20935044#20935044
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# **********************************************
# *** recommended exclusions for Apache commons
# **********************************************
# http://stackoverflow.com/questions/13122476/org-apache-commons-collections-beanmap-cant-find-referenced-class-java-beans-i
-dontwarn org.apache.commons.**
-dontwarn org.apache.commons.collections.BeanMap
-dontwarn java.beans.**
-dontwarn okio.**
-keep public class org.simpleframework.**{ 	*; }
-keep class org.simpleframework.xml.**{ 	*; }
-keep class org.simpleframework.xml.core.**{ 	*; }
 -keep class org.simpleframework.xml.util.**{ 	*; }

# *****************************************
# *** recommended exclusions for Otto
# *****************************************
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
    }

# *****************************************
# *** recommended exclusions for Retrofit
# *****************************************
# http://stackoverflow.com/questions/22881681/proguard-no-longer-works-with-retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-keepnames class com.levelup.http.okhttp.** { *; }
-keepnames interface com.levelup.http.okhttp.** { *; }

-keepnames class com.squareup.okhttp.** { *; }
-keepnames interface com.squareup.okhttp.** { *; }

# OkHttp oddities
-dontwarn com.squareup.okhttp.internal.http.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn dagger.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# *****************************************
# *** Google Play Services
# *****************************************
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**

# *****************************************
# *** Comscore
# *****************************************
-libraryjars libs


-keep class com.google.android.maps.MapActivity { *; }
-keep class com.comscore.instrumentation.** { *; }
-keep interface com.comscore.instrumentation.** { *; }
-keep class com.google.android.maps.** { *; }
-keep interface com.google.android.maps.** { *; }

-keep class android.support.v7.widget.ShareActionProvider { *; }
-keep class android.support.v7.widget.SearchView { *; }

-dontwarn com.comscore.instrumentation.**
-dontwarn com.google.android.maps.**
-dontwarn org.apache.http.**

#-keep interface org.apache.http.** { *; }
#-keep class org.apache.http.** { *; }

-keepattributes *Annotation*

# *****************************************
# *** Butterknife
# *****************************************

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# *****************************************
# *** Quickblox
# *****************************************

-keep class org.jivesoftware.smack.** { public *; }
-keep class org.jivesoftware.smackx.** { public *; }
-keep class com.quickblox.** { public *; }
-keep class * extends org.jivesoftware.smack { public *; }
-keep class * implements org.jivesoftware.smack.debugger.SmackDebugger { public *; }

# *****************************************
# *** Misc
# *****************************************
# keep the original source line number to help with debugging
# http://stackoverflow.com/questions/10158849/android-proguard-return-line-number
-keepattributes SourceFile,LineNumberTable

# From southern Experience
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keep public class com.experience.android.views.JsObject
-keep public class * implements com.experience.android.views.JsObject
-keepclassmembers class com.experience.android.views.JsObject {
    <methods>;
}




