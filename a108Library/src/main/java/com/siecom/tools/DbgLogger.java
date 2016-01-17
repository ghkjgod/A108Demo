package com.siecom.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

@SuppressWarnings("unused")
public class DbgLogger
{
  
private static final boolean LOG = true;
  private static final String LOG_TAG = "DbgLogger";
  private static final String CLASS_METHOD_LINE_FORMAT = "<%s.%s()  Line:%d> ";
  private static String _packageNames;

  public static boolean isEmulator()
  {
    return (Build.MODEL.equals("sdk")) || 
      (Build.MODEL.equals("google_sdk"));
  }

  public static void initAppPackageNames(Context context)
  {
    _packageNames = "";
    PackageInfo info = null;
    try
    {
      info = context.getPackageManager().getPackageInfo(
        context.getPackageName(), 0);
      _packageNames = info.packageName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  private static String formatLogPrefix(String className, String methodName, int lineNumber)
  {
    String logPrefix = "";
    if (_packageNames != null) {
      className = className.replaceFirst(_packageNames + ".", "");
      logPrefix = String.format("<%s.%s()  Line:%d> ", new Object[] { className, 
        methodName, Integer.valueOf(lineNumber) });
    } else {
      logPrefix = String.format("<%s.%s()  Line:%d> ", new Object[] { className, 
        methodName, Integer.valueOf(lineNumber) });
    }

    return logPrefix;
  }

  private static String formatLogContent(String logPrefix, Object[] objects)
  {
    String logString = "";
    if ((objects != null) && (objects.length > 0)) {
      String format = "";
      String logContent = "";
      int objArrayLen = objects.length;

      if ((objArrayLen > 1) && ((objects[0] instanceof String))) {
        Object[] objArray = new Object[objArrayLen - 1];

        format = (String)objects[0];
        System.arraycopy(objects, 1, objArray, 0, objArrayLen - 1);
        logContent = String.format(format, objArray);
      } else {
        for (int i = 0; i < objArrayLen; i++) {
          logContent = logContent + objects[i] + ",";
        }
        logContent = logContent.substring(0, logContent.length() - 1);
      }

      logString = logPrefix + logContent;
    }

    return logString;
  }

  public static void v(String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.v(tag, msg);
    } else {
      msg = tag;
      Log.v("DbgLogger", msg);
    }
  }

  public static void v(Throwable tr, String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.v(tag, msg, tr);
    } else {
      msg = tag;
      Log.v("DbgLogger", msg, tr);
    }
  }

  public static void d(String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.d(tag, msg);
    } else {
      msg = tag;
      Log.d("DbgLogger", msg);
    }
  }

  public static void d(Throwable tr, String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.d(tag, msg, tr);
    } else {
      msg = tag;
      Log.d("DbgLogger", msg, tr);
    }
  }

  public static void i(String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.i(tag, msg);
    } else {
      msg = tag;
      Log.i("DbgLogger", msg);
    }
  }

  public static void i(Throwable tr, String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.i(tag, msg, tr);
    } else {
      msg = tag;
      Log.i("DbgLogger", msg, tr);
    }
  }

  public static void w(String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.w(tag, msg);
    } else {
      msg = tag;
      Log.w("DbgLogger", msg);
    }
  }

  public static void w(Throwable tr, String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.w(tag, msg, tr);
    } else {
      msg = tag;
      Log.w("DbgLogger", msg, tr);
    }
  }

  public static void e(String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.e(tag, msg);
    } else {
      msg = tag;
      Log.e("DbgLogger", msg);
    }
  }

  public static void e(Throwable tr, String tag, Object[] objects)
  {
    StackTraceElement traceElement = Thread.currentThread()
      .getStackTrace()[3];
    String logPrefix = formatLogPrefix(traceElement.getClassName(), 
      traceElement.getMethodName(), traceElement.getLineNumber());

    String msg = "";
    if (objects.length > 0) {
      msg = formatLogContent(logPrefix, objects);
      Log.e(tag, msg, tr);
    } else {
      msg = tag;
      Log.e("DbgLogger", msg, tr);
    }
  }
}