#include "WebThreadImpl.h"

#include <list>
#include "util/Debug.h"

using namespace blink;

namespace {

struct JMethods {
    jclass cls;
    jmethodID nativeCurrent;
    jmethodID yieldCurrent;
    jmethodID constructor;
    jmethodID destroy;
    jmethodID cancelNativeTask;
    jmethodID postNativeTask;
    jmethodID postNativeDelayedTask;
    jmethodID isCurrentThread;
    jmethodID threadId;
    jmethodID addNativeTaskObserver;
    jmethodID removeNativeTaskObserver;
};

JMethods jmethods;

}

namespace typo {

void WebThreadImpl::registerJni() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();

    jmethods.cls = (jclass) env->NewGlobalRef(env->FindClass("com/dmi/typoweb/WebThreadImpl"));
    jmethods.nativeCurrent = env->GetStaticMethodID(jmethods.cls, "nativeCurrent", "()J");
    jmethods.yieldCurrent = env->GetStaticMethodID(jmethods.cls, "yieldCurrent", "()V");
    jmethods.constructor = env->GetMethodID(jmethods.cls, "<init>", "(JLjava/lang/String;)V");
    jmethods.destroy = env->GetMethodID(jmethods.cls, "destroy", "()V");
    jmethods.cancelNativeTask = env->GetMethodID(jmethods.cls, "cancelNativeTask", "(J)V");
    jmethods.postNativeTask = env->GetMethodID(jmethods.cls, "postNativeTask", "(J)V");
    jmethods.postNativeDelayedTask = env->GetMethodID(jmethods.cls, "postNativeDelayedTask", "(JJ)V");
    jmethods.isCurrentThread = env->GetMethodID(jmethods.cls, "isCurrentThread", "()Z");
    jmethods.threadId = env->GetMethodID(jmethods.cls, "threadId", "()J");
    jmethods.addNativeTaskObserver = env->GetMethodID(jmethods.cls, "addNativeTaskObserver", "(J)V");
    jmethods.removeNativeTaskObserver = env->GetMethodID(jmethods.cls, "removeNativeTaskObserver", "(J)V");

    static JNINativeMethod nativeMethods[] = {
        {"nativeRunTask", "(J)V", (void*) &nativeRunTask},
        {"nativeDeleteTask", "(J)V", (void*) &nativeDeleteTask},
        {"nativeWillProcessTask", "(J)V", (void*) &nativeWillProcessTask},
        {"nativeDidProcessTask", "(J)V", (void*) &nativeDidProcessTask},
    };
    env->RegisterNatives(jmethods.cls, nativeMethods, sizeof(nativeMethods) / sizeof(nativeMethods[0]));
}

WebThreadImpl* WebThreadImpl::current() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    return (WebThreadImpl*) env->CallStaticLongMethod(jmethods.cls, jmethods.nativeCurrent);
}

void WebThreadImpl::yieldCurrent() {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->CallStaticVoidMethod(jmethods.cls, jmethods.yieldCurrent);
}

WebThreadImpl::WebThreadImpl(const char* name) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    jstring jname = env->NewStringUTF(name);
    jobj_ = env->NewGlobalRef(env->NewObject(jmethods.cls, jmethods.constructor, (jlong) this, jname));
    webScheduler_ = new WebSchedulerImpl();
}

WebThreadImpl::~WebThreadImpl() {
    delete webScheduler_;
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->CallVoidMethod(jobj_, jmethods.destroy);
    env->DeleteGlobalRef(jobj_);
}

void WebThreadImpl::cancelTask(Task* task) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->CallVoidMethod(jobj_, jmethods.cancelNativeTask, (jlong) task);
}

void WebThreadImpl::postTask(const WebTraceLocation&, Task* task) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->CallVoidMethod(jobj_, jmethods.postNativeTask, (jlong) task);
}

void WebThreadImpl::postDelayedTask(const WebTraceLocation&, Task* task, long long delayMs) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->CallVoidMethod(jobj_, jmethods.postNativeDelayedTask, (jlong) task, delayMs);
}

bool WebThreadImpl::isCurrentThread() const {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    return env->CallBooleanMethod(jobj_, jmethods.isCurrentThread);
}

PlatformThreadId WebThreadImpl::threadId() const {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    return env->CallLongMethod(jobj_, jmethods.threadId);
}

void WebThreadImpl::addTaskObserver(TaskObserver* taskObserver) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->CallVoidMethod(jobj_, jmethods.addNativeTaskObserver, (jlong) taskObserver);
}

void WebThreadImpl::removeTaskObserver(TaskObserver* taskObserver) {
    JNIScope jniScope;
    JNIEnv* env = jniScope.getEnv();
    env->CallVoidMethod(jobj_, jmethods.removeNativeTaskObserver, (jlong) taskObserver);
}

WebScheduler* WebThreadImpl::scheduler() const {
    return webScheduler_;
}

void WebThreadImpl::nativeRunTask(JNIEnv*, jobject, jlong nativeTask) {
    WebThread::Task* task = (WebThread::Task*) nativeTask;
    task->run();
}

void WebThreadImpl::nativeDeleteTask(JNIEnv*, jobject, jlong nativeTask) {
    WebThread::Task* task = (WebThread::Task*) nativeTask;
    delete task;
}

void WebThreadImpl::nativeWillProcessTask(JNIEnv*, jobject, jlong nativeTaskObserver) {
    WebThread::TaskObserver* taskObserver = (WebThread::TaskObserver*) nativeTaskObserver;
    taskObserver->willProcessTask();
}

void WebThreadImpl::nativeDidProcessTask(JNIEnv*, jobject, jlong nativeTaskObserver) {
    WebThread::TaskObserver* taskObserver = (WebThread::TaskObserver*) nativeTaskObserver;
    taskObserver->didProcessTask();
}

}
